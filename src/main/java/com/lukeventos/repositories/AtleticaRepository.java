package com.lukeventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lukeventos.models.Atletica;
import com.lukeventos.models.Usuario;

@Repository
public interface AtleticaRepository extends JpaRepository<Atletica, Long> {
    Atletica findByUsuario(Usuario usuario);
    boolean existsByNome(String nome);
} 