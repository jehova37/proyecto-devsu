package com.desarrollo.serviciocuentamovimiento.controller;

import com.desarrollo.serviciocuentamovimiento.entity.Movimiento;
import com.desarrollo.serviciocuentamovimiento.service.MovimientoService;
import com.desarrollo.serviciocuentamovimiento.exception.SaldoNoDisponibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.desarrollo.serviciocuentamovimiento.service.CuentaService;
import com.desarrollo.serviciocuentamovimiento.model.ReporteResponse;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {
    @Autowired
    private MovimientoService movimientoService;

    //@Autowired
    //private CuentaService cuentaService;

    /*@PostMapping
    public ResponseEntity<?> registrar(@RequestBody Movimiento movimiento) {
        try {
            Movimiento resultado = movimientoService.registrar(movimiento);
            return ResponseEntity.ok(resultado);
        } catch (SaldoNoDisponibleException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }*/

    @PostMapping
    public Movimiento registrarMovimiento(@RequestParam Long cuentaId,
                                          @RequestParam String tipoMovimiento,
                                          @RequestParam BigDecimal valor) {
        return movimientoService.registrarMovimiento(cuentaId, tipoMovimiento, valor);
    }

    @GetMapping("/reportes")
    public List<ReporteResponse> obtenerReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return movimientoService.generarReporte(clienteId, fechaInicio, fechaFin);
    }
}
