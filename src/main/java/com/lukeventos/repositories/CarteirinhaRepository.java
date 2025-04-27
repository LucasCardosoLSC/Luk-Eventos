package com.lukeventos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lukeventos.models.Carteirinha;
import com.lukeventos.models.Usuario;

@Repository
public interface CarteirinhaRepository extends JpaRepository<Carteirinha, Long> {
    List<Carteirinha> findByAprovada(boolean aprovada);
    Optional<Carteirinha> findByUsuario(Usuario usuario);
    boolean existsByUsuarioAndAprovada(Usuario usuario, boolean aprovada);
} 