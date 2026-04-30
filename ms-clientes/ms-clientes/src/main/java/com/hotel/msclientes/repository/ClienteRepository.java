package com.hotel.msclientes.repository;

import com.hotel.msclientes.model.Clientes;
import com.hotel.msclientes.model.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Clientes, Long> {
    Optional<Clientes> findByEmail(String email);
}