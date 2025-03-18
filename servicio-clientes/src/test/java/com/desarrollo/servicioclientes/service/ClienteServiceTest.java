package com.desarrollo.servicioclientes.service;

import com.desarrollo.common.entity.ClienteMessage;
import com.desarrollo.servicioclientes.entity.Cliente;
import com.desarrollo.servicioclientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
//import org.springframework.boot.test.context.SpringBootTest;


//@SpringBootTest
public class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private MessageConverter messageConverter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearClienteExitoso() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Jose Lema");
        cliente.setIdentificacion("1234567890");
        cliente.setClienteId("CLI001");
        cliente.setContraseña("1234");
        cliente.setEstado(true);

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cliente resultado = clienteService.crear(cliente);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Jose Lema", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(jmsTemplate, times(1)).convertAndSend(eq("cliente-queue"), any(ClienteMessage.class));
    }

    @Test
    public void testListarClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNombre("Jose Lema");
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("Maria Perez");
        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        when(clienteRepository.findAll()).thenReturn(clientes);

        List<Cliente> resultado = clienteService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Jose Lema", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorIdExitoso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.obtenerPorId(1L);

        assertEquals("Jose Lema", resultado.getNombre());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    public void testObtenerPorIdNoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.obtenerPorId(1L);
        });
        assertEquals("Cliente no encontrado con ID: 1", exception.getMessage());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    public void testActualizarClienteExitoso() {
        Cliente existente = new Cliente();
        existente.setId(1L);
        existente.setNombre("Jose Lema");

        Cliente actualizado = new Cliente();
        actualizado.setNombre("Jose Lema Actualizado");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);

        Cliente resultado = clienteService.actualizar(1L, actualizado);

        assertEquals("Jose Lema Actualizado", resultado.getNombre());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(existente);
    }

    @Test
    public void testEliminarCliente() {
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.eliminar(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }
}