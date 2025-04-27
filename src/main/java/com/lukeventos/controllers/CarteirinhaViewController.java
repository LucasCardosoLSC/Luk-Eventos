package com.lukeventos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lukeventos.models.Carteirinha;
import com.lukeventos.models.Usuario;
import com.lukeventos.services.CarteirinhaService;
import com.lukeventos.services.UsuarioService;

@Controller
@RequestMapping("/carteirinha")
public class CarteirinhaViewController {

    @Autowired
    private CarteirinhaService carteirinhaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('UNIVERSITARIO')")
    public String carteirinha(Model model) {
        Usuario usuario = usuarioService.getCurrentUser();
        carteirinhaService.buscarPorUsuario(usuario).ifPresent(carteirinha -> {
            model.addAttribute("carteirinha", carteirinha);
        });
        
        return "carteirinha";
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('UNIVERSITARIO')")
    public String uploadCarteirinha(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            Carteirinha carteirinha = carteirinhaService.processarCarteirinha(file);
            redirectAttributes.addFlashAttribute("mensagem", "Carteirinha enviada com sucesso!");
            return "redirect:/carteirinha";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar carteirinha: " + e.getMessage());
            return "redirect:/carteirinha";
        }
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarCarteirinhasPendentes(Model model) {
        model.addAttribute("carteirinhas", carteirinhaService.listarCarteirinhasPendentes());
        return "carteirinhas-pendentes";
    }

    @PutMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public String aprovarCarteirinha(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Carteirinha carteirinha = carteirinhaService.aprovarCarteirinha(id);
            redirectAttributes.addFlashAttribute("mensagem", "Carteirinha aprovada com sucesso!");
            return "redirect:/carteirinha/pendentes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aprovar carteirinha: " + e.getMessage());
            return "redirect:/carteirinha/pendentes";
        }
    }

    @PutMapping("/{id}/rejeitar")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejeitarCarteirinha(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carteirinhaService.rejeitarCarteirinha(id);
            redirectAttributes.addFlashAttribute("mensagem", "Carteirinha rejeitada com sucesso!");
            return "redirect:/carteirinha/pendentes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao rejeitar carteirinha: " + e.getMessage());
            return "redirect:/carteirinha/pendentes";
        }
    }
} 