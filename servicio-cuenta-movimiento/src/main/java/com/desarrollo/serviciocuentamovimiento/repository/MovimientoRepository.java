package com.desarrollo.serviciocuentamovimiento.repository;

import com.desarrollo.serviciocuentamovimiento.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import com.desarrollo.serviciocuentamovimiento.entity.Cuenta;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaAndFechaBetween(Cuenta cuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
