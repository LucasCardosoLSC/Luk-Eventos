package com.lukeventos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lukeventos.models.Usuario;
import com.lukeventos.services.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public String perfilUsuario(Model model) {
        try {
            Usuario usuario = usuarioService.getCurrentUser();
            model.addAttribute("usuario", usuario);
            return "perfil";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar usuário atual: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String detalhesUsuario(@PathVariable Long id, Model model) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            return "usuario-detalhes";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar usuário: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarUsuario(@PathVariable Long id, Model model) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            return "usuario-form";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar usuário: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarUsuario(@PathVariable Long id, Usuario usuarioAtualizado, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setAtivo(usuarioAtualizado.isAtivo());
            
            usuarioService.atualizarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
            return "redirect:/usuarios/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/usuarios/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public String desativarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desativarUsuario(id);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário desativado com sucesso!");
            return "redirect:/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao desativar usuário: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }
} 