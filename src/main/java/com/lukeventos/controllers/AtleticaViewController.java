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

import com.lukeventos.models.Atletica;
import com.lukeventos.repositories.AtleticaRepository;

@Controller
@RequestMapping("/atleticas")
public class AtleticaViewController {

    @Autowired
    private AtleticaRepository atleticaRepository;

    @GetMapping
    public String listarAtleticas(Model model) {
        model.addAttribute("atleticas", atleticaRepository.findAll());
        return "atleticas";
    }

    @GetMapping("/{id}")
    public String detalhesAtletica(@PathVariable Long id, Model model) {
        atleticaRepository.findById(id).ifPresent(atletica -> {
            model.addAttribute("atletica", atletica);
        });
        return "atletica-detalhes";
    }

    @GetMapping("/nova")
    @PreAuthorize("hasRole('ADMIN')")
    public String novaAtletica(Model model) {
        model.addAttribute("atletica", new Atletica());
        return "atletica-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String criarAtletica(Atletica atletica, RedirectAttributes redirectAttributes) {
        try {
            atleticaRepository.save(atletica);
            redirectAttributes.addFlashAttribute("mensagem", "Atlética criada com sucesso!");
            return "redirect:/atleticas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar atlética: " + e.getMessage());
            return "redirect:/atleticas/nova";
        }
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarAtletica(@PathVariable Long id, Model model) {
        atleticaRepository.findById(id).ifPresent(atletica -> {
            model.addAttribute("atletica", atletica);
        });
        return "atletica-form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarAtletica(@PathVariable Long id, Atletica atleticaAtualizada, RedirectAttributes redirectAttributes) {
        try {
            atleticaRepository.findById(id).ifPresent(atletica -> {
                atletica.setNome(atleticaAtualizada.getNome());
                atleticaRepository.save(atletica);
            });
            redirectAttributes.addFlashAttribute("mensagem", "Atlética atualizada com sucesso!");
            return "redirect:/atleticas/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar atlética: " + e.getMessage());
            return "redirect:/atleticas/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/deletar")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletarAtletica(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            atleticaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagem", "Atlética deletada com sucesso!");
            return "redirect:/atleticas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao deletar atlética: " + e.getMessage());
            return "redirect:/atleticas";
        }
    }
} 