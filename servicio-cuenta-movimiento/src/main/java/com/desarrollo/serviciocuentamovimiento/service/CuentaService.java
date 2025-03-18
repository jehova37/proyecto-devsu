package com.desarrollo.serviciocuentamovimiento.service;

import com.desarrollo.serviciocuentamovimiento.entity.Cuenta;
import com.desarrollo.serviciocuentamovimiento.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import com.desarrollo.common.entity.ClienteMessage;
import com.desarrollo.serviciocuentamovimiento.entity.Movimiento;
import com.desarrollo.serviciocuentamovimiento.repository.MovimientoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @JmsListener(destination = "cliente-queue")
    public void crearCuentaDesdeCliente(ClienteMessage clienteMessage) {

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("CUENTA-" + clienteMessage.getClienteId());
        cuenta.setTipo("Ahorros");
        cuenta.setSaldoInicial(BigDecimal.ZERO);
        cuenta.setEstado(true);
        cuenta.setClienteId(clienteMessage.getId());
        cuentaRepository.save(cuenta);
        System.out.println("Cuenta creada para cliente: " + clienteMessage.getNombre());
    }

    public Cuenta crear(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public List<Cuenta> listar() {
        return cuentaRepository.findAll();
    }

    public Cuenta actualizar(Long id, Cuenta cuenta) {
        Cuenta existente = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        existente.setNumeroCuenta(cuenta.getNumeroCuenta());
        existente.setTipo(cuenta.getTipo());
        existente.setSaldoInicial(cuenta.getSaldoInicial());
        existente.setEstado(cuenta.isEstado());
        existente.setClienteId(cuenta.getClienteId());
        return cuentaRepository.save(existente);
    }

    public Movimiento registrarMovimiento(Long cuentaId, String tipoMovimiento, BigDecimal valor) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        BigDecimal nuevoSaldo;
        if ("DEPOSITO".equals(tipoMovimiento)) {
            nuevoSaldo = cuenta.getSaldoInicial().add(valor);
        } else if ("RETIRO".equals(tipoMovimiento)) {
            nuevoSaldo = cuenta.getSaldoInicial().subtract(valor);
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }
        } else {
            throw new RuntimeException("Tipo de movimiento no válido");
        }

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        return movimientoRepository.save(movimiento);
    }
}
