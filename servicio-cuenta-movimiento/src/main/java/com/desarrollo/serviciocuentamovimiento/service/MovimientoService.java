package com.desarrollo.serviciocuentamovimiento.service;

import com.desarrollo.serviciocuentamovimiento.entity.Cuenta;
import com.desarrollo.serviciocuentamovimiento.entity.Movimiento;
import com.desarrollo.serviciocuentamovimiento.repository.CuentaRepository;
import com.desarrollo.serviciocuentamovimiento.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.desarrollo.serviciocuentamovimiento.exception.SaldoNoDisponibleException;
import org.springframework.web.client.RestTemplate;
import com.desarrollo.serviciocuentamovimiento.model.ReporteResponse;
import com.desarrollo.serviciocuentamovimiento.repository.CuentaRepository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private RestTemplate restTemplate;

    /*public Movimiento registrar(Movimiento movimiento) {
        Cuenta cuenta = cuentaRepository.findById(movimiento.getCuentaId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        BigDecimal nuevoSaldo = cuenta.getSaldoInicial().add(movimiento.getValor());
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException("Saldo no Disponible");
        }
        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setFecha(LocalDateTime.now());
        return movimientoRepository.save(movimiento);
    }*/

    public Movimiento registrarMovimiento(Long cuentaId, String tipoMovimiento, BigDecimal valor) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + cuentaId));

        BigDecimal nuevoSaldo;
        if ("DEPOSITO".equalsIgnoreCase(tipoMovimiento)) {
            nuevoSaldo = cuenta.getSaldoInicial().add(valor);
        }
        else if ("RETIRO".equalsIgnoreCase(tipoMovimiento)) {
            if(valor.compareTo(BigDecimal.ZERO) < 0)
                nuevoSaldo = cuenta.getSaldoInicial().add(valor);
            else
                nuevoSaldo = cuenta.getSaldoInicial().subtract(valor);

            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }
        }
        else {
            throw new RuntimeException("Tipo de movimiento no valido");
        }

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(tipoMovimiento.toUpperCase());
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        return movimientoRepository.save(movimiento);
    }

    public List<ReporteResponse> generarReporte(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // aqui consulto el nombre del cliente desde servicio-clientes utilizando endpoint /clientes/nombres/{id}
        String clienteNombre = restTemplate.getForObject(
                "http://localhost:8080/clientes/nombre/" + clienteId,
                String.class
        );

        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        List<ReporteResponse> reportes = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetween(cuenta, fechaInicio, fechaFin);
            for (Movimiento mov : movimientos) {
                ReporteResponse reporte = new ReporteResponse();
                reporte.setFecha(mov.getFecha());
                reporte.setCliente(clienteNombre != null ? clienteNombre : "Cliente " + clienteId);
                reporte.setNumeroCuenta(cuenta.getNumeroCuenta());
                reporte.setTipo(cuenta.getTipo());
                reporte.setSaldoInicial(cuenta.getSaldoInicial());
                reporte.setEstado(cuenta.isEstado());
                reporte.setTipoMovimiento(mov.getTipoMovimiento());
                reporte.setValor(mov.getValor());
                reporte.setSaldoDisponible(mov.getSaldo());
                reportes.add(reporte);
            }
        }
        return reportes;
    }
}
