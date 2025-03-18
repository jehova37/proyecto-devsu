package com.desarrollo.serviciocuentamovimiento.controller;

import com.desarrollo.serviciocuentamovimiento.model.ReporteResponse;
import com.desarrollo.serviciocuentamovimiento.entity.Movimiento;
import com.desarrollo.serviciocuentamovimiento.service.MovimientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
public class MovimientoControllerTest {

    @InjectMocks
    private MovimientoController movimientoController;

    @Mock
    private MovimientoService movimientoService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(movimientoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testRegistrarMovimientoExitoso() throws Exception {
        
        Movimiento movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setTipoMovimiento("DEPOSITO");
        movimiento.setValor(BigDecimal.valueOf(50));
        movimiento.setSaldo(BigDecimal.valueOf(150));

        when(movimientoService.registrarMovimiento(1L, "DEPOSITO", BigDecimal.valueOf(50)))
                .thenReturn(movimiento);

     
        mockMvc.perform(post("/movimientos")
                        .param("cuentaId", "1")
                        .param("tipoMovimiento", "DEPOSITO")
                        .param("valor", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.valor").value(50));
        verify(movimientoService, times(1)).registrarMovimiento(1L, "DEPOSITO", BigDecimal.valueOf(50));
    }

    @Test
    public void testRegistrarMovimientoSaldoInsuficiente() throws Exception {
        
        when(movimientoService.registrarMovimiento(1L, "RETIRO", BigDecimal.valueOf(100)))
                .thenThrow(new RuntimeException("Saldo insuficiente"));

       
        mockMvc.perform(post("/movimientos")
                        .param("cuentaId", "1")
                        .param("tipoMovimiento", "RETIRO")
                        .param("valor", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Saldo insuficiente"));
        verify(movimientoService, times(1)).registrarMovimiento(1L, "RETIRO", BigDecimal.valueOf(100));
    }

    @Test
    public void testObtenerReporteExitoso() throws Exception {
       
        ReporteResponse reporte = new ReporteResponse();
        reporte.setCliente("Jose Lema");
        reporte.setNumeroCuenta("CUENTA-001");
        reporte.setTipoMovimiento("DEPOSITO");
        reporte.setValor(BigDecimal.valueOf(50));
        reporte.setFecha(LocalDateTime.now());

        when(movimientoService.generarReporte(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(reporte));

        
        mockMvc.perform(get("/movimientos/reportes")
                        .param("clienteId", "1")
                        .param("fechaInicio", "2025-03-17T00:00:00")
                        .param("fechaFin", "2025-03-18T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente").value("Jose Lema"))
                .andExpect(jsonPath("$[0].numeroCuenta").value("CUENTA-001"));
        verify(movimientoService, times(1)).generarReporte(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}