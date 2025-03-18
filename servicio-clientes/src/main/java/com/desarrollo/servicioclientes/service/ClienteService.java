package com.desarrollo.servicioclientes.service;

import com.desarrollo.servicioclientes.entity.Cliente;
import com.desarrollo.servicioclientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.desarrollo.common.entity.ClienteMessage;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final JmsTemplate jmsTemplate;
    private final MessageConverter messageConverter;

    public ClienteService(ClienteRepository clienteRepository, JmsTemplate jmsTemplate, MessageConverter messageConverter){
        this.jmsTemplate = jmsTemplate;
        this.clienteRepository = clienteRepository;
        this.messageConverter = messageConverter;
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    public Cliente crear(Cliente cliente) {
        Cliente nuevoCliente = clienteRepository.save(cliente);
        //jmsTemplate.convertAndSend("cliente-queue", nuevoCliente);
        ClienteMessage clienteMessage = new ClienteMessage();
        clienteMessage.setId(nuevoCliente.getId());
        clienteMessage.setNombre(nuevoCliente.getNombre());
        clienteMessage.setGenero(nuevoCliente.getGenero());
        clienteMessage.setEdad(nuevoCliente.getEdad());
        clienteMessage.setIdentificacion(nuevoCliente.getIdentificacion());
        clienteMessage.setDireccion(nuevoCliente.getDireccion());
        clienteMessage.setTelefono(nuevoCliente.getTelefono());
        clienteMessage.setClienteId(nuevoCliente.getClienteId());
        clienteMessage.setContraseña(nuevoCliente.getContraseña());
        clienteMessage.setEstado(nuevoCliente.isEstado());

        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.convertAndSend("cliente-queue", clienteMessage);
        return nuevoCliente;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
        existente.setNombre(cliente.getNombre());
        existente.setGenero(cliente.getGenero());
        existente.setEdad(cliente.getEdad());
        existente.setIdentificacion(cliente.getIdentificacion());
        existente.setDireccion(cliente.getDireccion());
        existente.setTelefono(cliente.getTelefono());
        existente.setClienteId(cliente.getClienteId());
        existente.setContraseña(cliente.getContraseña());
        existente.setEstado(cliente.isEstado());
        return clienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
