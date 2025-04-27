package com.lukeventos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukeventos.models.Evento;
import com.lukeventos.models.Inscricao;
import com.lukeventos.models.Usuario;
import com.lukeventos.repositories.InscricaoRepository;

@Service
public class InscricaoService {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private CarteirinhaService carteirinhaService;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public Inscricao inscreverEmEvento(Evento evento, Usuario usuario) {
        // Verifica se o usuário está aprovado
        if (usuario.getTipo() == Usuario.TipoUsuario.UNIVERSITARIO && 
            usuario.getCarteirinhaStatus() != Usuario.StatusCarteirinha.APROVADA) {
            throw new RuntimeException("Usuário não está aprovado para se inscrever em eventos");
        }
        
        // Verifica se o evento está aprovado
        if (evento.getStatus() != Evento.StatusEvento.APROVADO) {
            throw new RuntimeException("Evento não está aprovado para inscrições");
        }
        
        // Verifica se já existe inscrição
        if (inscricaoRepository.existsByEventoAndUsuario(evento, usuario)) {
            throw new RuntimeException("Usuário já está inscrito neste evento");
        }
        
        // Verifica se há vagas disponíveis
        if (evento.getVagasDisponiveis() != null && 
            evento.getVagasDisponiveis() <= inscricaoRepository.countInscricoesPorEvento(evento)) {
            throw new RuntimeException("Não há vagas disponíveis para este evento");
        }
        
        Inscricao inscricao = new Inscricao();
        inscricao.setEvento(evento);
        inscricao.setUsuario(usuario);
        inscricao.setDataInscricao(LocalDateTime.now());
        inscricao.setStatus(Inscricao.StatusInscricao.PENDENTE);
        
        return inscricaoRepository.save(inscricao);
    }
    
    @Transactional
    public Inscricao aprovarInscricao(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
            .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
        
        inscricao.setStatus(Inscricao.StatusInscricao.APROVADA);
        return inscricaoRepository.save(inscricao);
    }
    
    @Transactional
    public Inscricao rejeitarInscricao(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
            .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
        
        inscricao.setStatus(Inscricao.StatusInscricao.REJEITADA);
        return inscricaoRepository.save(inscricao);
    }
    
    public List<Inscricao> listarInscricoesPorUsuario(Usuario usuario) {
        return inscricaoRepository.findByUsuario(usuario);
    }
    
    public List<Inscricao> listarInscricoesPorEvento(Evento evento) {
        return inscricaoRepository.findByEvento(evento);
    }
    
    public List<Inscricao> listarInscricoesEmEventosAprovados(Usuario usuario) {
        return inscricaoRepository.findInscricoesEmEventosAprovados(usuario);
    }
    
    public List<Inscricao> listarInscricoesPorAtletica(Usuario atletica) {
        return inscricaoRepository.findInscricoesPorAtletica(atletica);
    }
    
    public List<Inscricao> listarInscricoesPendentesPorUsuario(Usuario usuario) {
        return inscricaoRepository.findInscricoesPendentesPorUsuario(usuario);
    }
    
    public Long contarInscricoesPorEvento(Evento evento) {
        return inscricaoRepository.countInscricoesPorEvento(evento);
    }

    public Optional<Inscricao> buscarInscricao(Evento evento, Usuario usuario) {
        return inscricaoRepository.findByEventoAndUsuario(evento, usuario);
    }

    public Inscricao atualizarStatusInscricao(Long id, String statusStr) {
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
        
        Inscricao.StatusInscricao status;
        try {
            status = Inscricao.StatusInscricao.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido: " + statusStr);
        }
        
        inscricao.setStatus(status);
        return inscricaoRepository.save(inscricao);
    }
} 