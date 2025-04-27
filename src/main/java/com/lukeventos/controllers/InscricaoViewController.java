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
import com.lukeventos.models.Inscricao;
import com.lukeventos.models.Usuario;
import com.lukeventos.services.EventoService;
import com.lukeventos.services.InscricaoService;
import com.lukeventos.services.UsuarioService;

@Controller
@RequestMapping("/inscricoes")
public class InscricaoViewController {

    @Autowired
    private InscricaoService inscricaoService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/evento/{eventoId}")
    @PreAuthorize("hasRole('UNIVERSITARIO')")
    public String inscreverEmEvento(@PathVariable Long eventoId, RedirectAttributes redirectAttributes) {
        try {
            Evento evento = eventoService.buscarEvento(eventoId)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
            Usuario usuario = usuarioService.getCurrentUser();
            Inscricao inscricao = inscricaoService.inscreverEmEvento(evento, usuario);
            redirectAttributes.addFlashAttribute("mensagem", "Inscrição realizada com sucesso!");
            return "redirect:/eventos/" + eventoId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao realizar inscrição: " + e.getMessage());
            return "redirect:/eventos/" + eventoId;
        }
    }

    @GetMapping("/evento/{eventoId}")
    @PreAuthorize("hasRole('ATLETICA')")
    public String listarInscricoesPorEvento(@PathVariable Long eventoId, Model model) {
        Evento evento = eventoService.buscarEvento(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        model.addAttribute("evento", evento);
        model.addAttribute("inscricoes", inscricaoService.listarInscricoesPorEvento(evento));
        return "inscricoes-evento";
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('UNIVERSITARIO')")
    public String listarInscricoesDoUsuario(Model model) {
        Usuario usuario = usuarioService.getCurrentUser();
        model.addAttribute("inscricoes", inscricaoService.listarInscricoesPorUsuario(usuario));
        return "minhas-inscricoes";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ATLETICA')")
    public String atualizarStatusInscricao(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            Inscricao inscricao = inscricaoService.atualizarStatusInscricao(id, status);
            redirectAttributes.addFlashAttribute("mensagem", "Status da inscrição atualizado com sucesso!");
            return "redirect:/inscricoes/evento/" + inscricao.getEvento().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar status da inscrição: " + e.getMessage());
            return "redirect:/inscricoes";
        }
    }
} 