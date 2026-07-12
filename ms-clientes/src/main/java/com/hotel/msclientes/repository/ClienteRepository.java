package com.hotel.msclientes.repository;

import com.hotel.msclientes.model.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Clientes, Long> {
    // Añadimos este método para poder validar correos duplicados en el Service más adelante
    Optional<Clientes> findByEmail(String email);
}