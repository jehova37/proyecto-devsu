package com.desarrollo.servicioclientes.controller;

import com.desarrollo.servicioclientes.entity.Cliente;
import com.desarrollo.servicioclientes.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
public class ClienteControllerTest {

    @InjectMocks
    private ClienteController clienteController;

    @Mock
    private ClienteService clienteService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCrearCliente() throws Exception {
       
        Cliente cliente = new Cliente();
        cliente.setNombre("Jose Lema");
        cliente.setId(1L);

        when(clienteService.crear(any(Cliente.class))).thenReturn(cliente);

       
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Jose Lema\",\"identificacion\":\"1234567890\",\"clienteId\":\"CLI001\",\"contraseña\":\"1234\",\"estado\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Jose Lema"));
        verify(clienteService, times(1)).crear(any(Cliente.class));
    }

    @Test
    public void testListarClientes() throws Exception {
        
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        when(clienteService.listar()).thenReturn(Arrays.asList(cliente));


        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Jose Lema"));
        verify(clienteService, times(1)).listar();
    }

    @Test
    public void testObtenerClienteNoEncontrado() throws Exception {
      
        when(clienteService.obtenerPorId(1L)).thenThrow(new RuntimeException("Cliente no encontrado con ID: 1"));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cliente no encontrado con ID: 1"));
        verify(clienteService, times(1)).obtenerPorId(1L);
    }
}