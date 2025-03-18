package com.desarrollo.servicioclientes.repository;

import com.desarrollo.servicioclientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}