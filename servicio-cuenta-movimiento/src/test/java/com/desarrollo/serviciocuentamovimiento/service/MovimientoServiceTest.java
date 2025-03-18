package com.desarrollo.serviciocuentamovimiento.service;

import com.desarrollo.serviciocuentamovimiento.model.ReporteResponse;
import com.desarrollo.serviciocuentamovimiento.entity.Cuenta;
import com.desarrollo.serviciocuentamovimiento.entity.Movimiento;
import com.desarrollo.serviciocuentamovimiento.repository.CuentaRepository;
import com.desarrollo.serviciocuentamovimiento.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


//@SpringBootTest
public class MovimientoServiceTest {
    @InjectMocks
    private MovimientoService movimientoService;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegistrarMovimientoDepositoExitoso() {
        
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldoInicial(BigDecimal.valueOf(100));
        cuenta.setEstado(true);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArguments()[0]);

     
        Movimiento resultado = movimientoService.registrarMovimiento(1L, "DEPOSITO", BigDecimal.valueOf(50));

      
        assertNotNull(resultado);
        assertEquals("DEPOSITO", resultado.getTipoMovimiento());
        assertEquals(BigDecimal.valueOf(150), resultado.getSaldo());
        verify(cuentaRepository, times(1)).findById(1L);
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    public void testRegistrarMovimientoRetiroExitoso() {
     
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldoInicial(BigDecimal.valueOf(100));
        cuenta.setEstado(true);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArguments()[0]);

     
        Movimiento resultado = movimientoService.registrarMovimiento(1L, "RETIRO", BigDecimal.valueOf(50));

       
        assertNotNull(resultado);
        assertEquals("RETIRO", resultado.getTipoMovimiento());
        assertEquals(BigDecimal.valueOf(50), resultado.getSaldo());
        verify(cuentaRepository, times(1)).findById(1L);
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    public void testRegistrarMovimientoRetiroSaldoInsuficiente() {
        
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldoInicial(BigDecimal.valueOf(50));
        cuenta.setEstado(true);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

      
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movimientoService.registrarMovimiento(1L, "RETIRO", BigDecimal.valueOf(100));
        });
        assertEquals("Saldo insuficiente", exception.getMessage());
        verify(cuentaRepository, times(1)).findById(1L);
        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    public void testRegistrarMovimientoTipoInvalido() {
        
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldoInicial(BigDecimal.valueOf(100));
        cuenta.setEstado(true);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movimientoService.registrarMovimiento(1L, "TRANSFERENCIA", BigDecimal.valueOf(50));
        });
        assertEquals("Tipo de movimiento no valido", exception.getMessage());
        verify(cuentaRepository, times(1)).findById(1L);
        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }


}
