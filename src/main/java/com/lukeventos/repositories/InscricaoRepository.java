package com.lukeventos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lukeventos.models.Evento;
import com.lukeventos.models.Inscricao;
import com.lukeventos.models.Usuario;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    List<Inscricao> findByEvento(Evento evento);
    List<Inscricao> findByUsuario(Usuario usuario);
    Optional<Inscricao> findByEventoAndUsuario(Evento evento, Usuario usuario);
    boolean existsByEventoAndUsuario(Evento evento, Usuario usuario);
    
    @Query("SELECT i FROM Inscricao i WHERE i.usuario = :usuario AND i.evento.status = 'APROVADO'")
    List<Inscricao> findInscricoesEmEventosAprovados(Usuario usuario);
    
    @Query("SELECT COUNT(i) FROM Inscricao i WHERE i.evento = :evento")
    Long countInscricoesPorEvento(Evento evento);
    
    @Query("SELECT i FROM Inscricao i WHERE i.evento.atletica = :atletica")
    List<Inscricao> findInscricoesPorAtletica(Usuario atletica);
    
    @Query("SELECT i FROM Inscricao i WHERE i.usuario = :usuario AND i.status = 'PENDENTE'")
    List<Inscricao> findInscricoesPendentesPorUsuario(Usuario usuario);
} 