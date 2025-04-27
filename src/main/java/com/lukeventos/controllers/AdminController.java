package com.lukeventos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lukeventos.models.Evento;
import com.lukeventos.models.Usuario;
import com.lukeventos.services.EventoService;
import com.lukeventos.services.UsuarioService;
import com.lukeventos.services.CarteirinhaService;
import com.lukeventos.repositories.AtleticaRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private EventoService eventoService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AtleticaRepository atleticaRepository;

    @Autowired
    private CarteirinhaService carteirinhaService;

    @GetMapping
    public String dashboard(Model model) {
        // Eventos pendentes e rejeitados
        List<Evento> eventosPendentes = eventoService.listarEventosPendentes();
        List<Evento> eventosRejeitados = eventoService.listarEventosRejeitados();
        model.addAttribute("eventosPendentes", eventosPendentes);
        model.addAttribute("eventosRejeitados", eventosRejeitados);
        
        // Usuários
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        
        // Carteirinhas pendentes
        model.addAttribute("carteirinhasPendentes", carteirinhaService.listarCarteirinhasPendentes());
        
        // Estatísticas do projeto
        long totalUsuarios = usuarios.size();
        long totalAtleticas = atleticaRepository.count();
        long totalEventos = eventoService.listarEventos().size();
        long eventosAprovados = eventoService.listarEventosAprovados().size();
        long eventosPendentesCount = eventosPendentes.size();
        long eventosRejeitadosCount = eventosRejeitados.size();
        
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalAtleticas", totalAtleticas);
        model.addAttribute("totalEventos", totalEventos);
        model.addAttribute("eventosAprovados", eventosAprovados);
        model.addAttribute("eventosPendentesCount", eventosPendentesCount);
        model.addAttribute("eventosRejeitadosCount", eventosRejeitadosCount);
        
        model.addAttribute("content", "admin");
        return "layout/base";
    }

    @PostMapping("/eventos/{id}/aprovar")
    public String aprovarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.aprovarEvento(id);
            redirectAttributes.addFlashAttribute("mensagem", "Evento aprovado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aprovar evento: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/eventos/{id}/rejeitar")
    public String rejeitarEvento(@PathVariable Long id, @RequestParam String feedback, RedirectAttributes redirectAttributes) {
        try {
            eventoService.rejeitarEvento(id, feedback);
            redirectAttributes.addFlashAttribute("mensagem", "Evento rejeitado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao rejeitar evento: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/carteirinhas/{id}/aprovar")
    public String aprovarCarteirinha(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carteirinhaService.aprovarCarteirinha(id);
            redirectAttributes.addFlashAttribute("mensagem", "Carteirinha aprovada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aprovar carteirinha: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/carteirinhas/{id}/rejeitar")
    public String rejeitarCarteirinha(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carteirinhaService.rejeitarCarteirinha(id);
            redirectAttributes.addFlashAttribute("mensagem", "Carteirinha rejeitada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao rejeitar carteirinha: " + e.getMessage());
        }
        return "redirect:/admin";
    }
} 