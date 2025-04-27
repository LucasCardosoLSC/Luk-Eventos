package com.lukeventos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukeventos.models.Evento;
import com.lukeventos.models.Usuario;
import com.lukeventos.models.Atletica;
import com.lukeventos.models.Evento.StatusEvento;
import com.lukeventos.repositories.EventoRepository;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Transactional
    public Evento criarEvento(Evento evento) {
        evento.setStatus(StatusEvento.PENDENTE);
        evento.setDataCriacao(LocalDateTime.now());
        evento.setDataAtualizacao(LocalDateTime.now());
        return eventoRepository.save(evento);
    }

    public List<Evento> listarEventosAprovados() {
        return eventoRepository.findByStatus(StatusEvento.APROVADO);
    }

    public List<Evento> listarEventosPendentes() {
        return eventoRepository.findEventosPendentes();
    }

    public List<Evento> listarEventosRejeitados() {
        return eventoRepository.findByStatus(StatusEvento.REJEITADO);
    }

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarEventosPorAtletica(Atletica atletica) {
        return eventoRepository.findByAtletica(atletica);
    }

    public List<Evento> listarEventosPendentesPorAtletica(Atletica atletica) {
        return eventoRepository.findByAtleticaAndStatus(atletica, StatusEvento.PENDENTE);
    }

    public List<Evento> listarEventosAprovadosPorAtletica(Atletica atletica) {
        return eventoRepository.findByAtleticaAndStatus(atletica, StatusEvento.APROVADO);
    }

    public List<Evento> listarEventosRejeitadosPorAtletica(Atletica atletica) {
        return eventoRepository.findByAtleticaAndStatus(atletica, StatusEvento.REJEITADO);
    }

    public Optional<Evento> buscarEvento(Long id) {
        return eventoRepository.findById(id);
    }

    @Transactional
    public Evento aprovarEvento(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        
        evento.setStatus(StatusEvento.APROVADO);
        evento.setDataAtualizacao(LocalDateTime.now());
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento rejeitarEvento(Long eventoId, String feedback) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        
        evento.setStatus(StatusEvento.REJEITADO);
        evento.setFeedback(feedback);
        evento.setDataAtualizacao(LocalDateTime.now());
        return eventoRepository.save(evento);
    }

    public void deletarEvento(Long id) {
        eventoRepository.deleteById(id);
    }
    
    public Evento salvarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }
    
    public List<Evento> listarEventosAprovadosFuturos() {
        return eventoRepository.findEventosAprovadosFuturos(LocalDateTime.now());
    }
    
    public Long contarEventosPendentes() {
        return eventoRepository.countEventosPendentes();
    }
    
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }
}