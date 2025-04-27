package com.lukeventos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lukeventos.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.tipo = 'UNIVERSITARIO' AND u.carteirinhaStatus = 'PENDENTE'")
    List<Usuario> findUniversitariosComCarteirinhaPendente();
    
    @Query("SELECT u FROM Usuario u WHERE u.tipo = 'ATLETICA'")
    List<Usuario> findAllAtleticas();
    
    @Query("SELECT u FROM Usuario u WHERE u.tipo = 'UNIVERSITARIO' AND u.carteirinhaStatus = 'APROVADA'")
    List<Usuario> findUniversitariosAprovados();
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.tipo = 'UNIVERSITARIO' AND u.carteirinhaStatus = 'PENDENTE'")
    Long countUniversitariosComCarteirinhaPendente();
} 