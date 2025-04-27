package com.lukeventos.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lukeventos.models.Evento;
import com.lukeventos.models.Usuario;
import com.lukeventos.models.Atletica;
import com.lukeventos.models.Evento.StatusEvento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByStatus(StatusEvento status);
    List<Evento> findByAtletica(Atletica atletica);
    List<Evento> findByAtleticaAndStatus(Atletica atletica, StatusEvento status);
    
    @Query("SELECT e FROM Evento e WHERE e.status = 'APROVADO' AND e.dataInicio > :agora")
    List<Evento> findEventosAprovadosFuturos(LocalDateTime agora);
    
    @Query("SELECT e FROM Evento e WHERE e.status = 'PENDENTE'")
    List<Evento> findEventosPendentes();
    
    @Query("SELECT COUNT(e) FROM Evento e WHERE e.status = 'PENDENTE'")
    Long countEventosPendentes();
}