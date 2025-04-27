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

import com.lukeventos.models.Evento;
import com.lukeventos.models.Usuario;
import com.lukeventos.models.Atletica;
import com.lukeventos.services.EventoService;
import com.lukeventos.services.UsuarioService;
import com.lukeventos.repositories.AtleticaRepository;

@Controller
@RequestMapping("/meus-eventos")
@PreAuthorize("hasRole('ATLETICA')")
public class MeusEventosViewController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AtleticaRepository atleticaRepository;

    @GetMapping
    public String meusEventos(Model model) {
        Usuario usuario = usuarioService.getCurrentUser();
        Atletica atletica = atleticaRepository.findByUsuario(usuario);
        
        if (atletica == null) {
            throw new RuntimeException("Atlética não encontrada para o usuário");
        }
        
        model.addAttribute("eventosPendentes", eventoService.listarEventosPendentesPorAtletica(atletica));
        model.addAttribute("eventosAprovados", eventoService.listarEventosAprovadosPorAtletica(atletica));
        model.addAttribute("eventosRejeitados", eventoService.listarEventosRejeitadosPorAtletica(atletica));
        model.addAttribute("content", "meus-eventos");
        return "layout/base";
    }

    @PostMapping("/{id}/excluir")
    public String excluirEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Evento evento = eventoService.buscarPorId(id);
            Usuario usuario = usuarioService.getCurrentUser();
            Atletica atletica = atleticaRepository.findByUsuario(usuario);
            
            // Verificar se o evento pertence à atlética do usuário
            if (!evento.getAtletica().getId().equals(atletica.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para excluir este evento.");
                return "redirect:/meus-eventos";
            }
            
            // Verificar se o evento pode ser excluído (apenas pendentes ou rejeitados)
            if (evento.getStatus() != Evento.StatusEvento.PENDENTE && evento.getStatus() != Evento.StatusEvento.REJEITADO) {
                redirectAttributes.addFlashAttribute("erro", "Apenas eventos pendentes ou rejeitados podem ser excluídos.");
                return "redirect:/meus-eventos";
            }
            
            eventoService.deletarEvento(id);
            redirectAttributes.addFlashAttribute("mensagem", "Evento excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir evento: " + e.getMessage());
        }
        return "redirect:/meus-eventos";
    }

    @PostMapping("/{id}/editar")
    public String editarEvento(@PathVariable Long id, Evento eventoAtualizado, RedirectAttributes redirectAttributes) {
        try {
            Evento evento = eventoService.buscarPorId(id);
            Usuario usuario = usuarioService.getCurrentUser();
            Atletica atletica = atleticaRepository.findByUsuario(usuario);
            
            // Verificar se o evento pertence à atlética do usuário
            if (!evento.getAtletica().getId().equals(atletica.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar este evento.");
                return "redirect:/meus-eventos";
            }
            
            // Verificar se o evento pode ser editado (apenas pendentes ou rejeitados)
            if (evento.getStatus() != Evento.StatusEvento.PENDENTE && evento.getStatus() != Evento.StatusEvento.REJEITADO) {
                redirectAttributes.addFlashAttribute("erro", "Apenas eventos pendentes ou rejeitados podem ser editados.");
                return "redirect:/meus-eventos";
            }
            
            // Se o evento estava rejeitado, muda para pendente
            if (evento.getStatus() == Evento.StatusEvento.REJEITADO) {
                evento.setStatus(Evento.StatusEvento.PENDENTE);
                evento.setFeedback(null);
            }
            
            evento.setTitulo(eventoAtualizado.getTitulo());
            evento.setDescricao(eventoAtualizado.getDescricao());
            evento.setDataInicio(eventoAtualizado.getDataInicio());
            evento.setDataFim(eventoAtualizado.getDataFim());
            evento.setLocalEvento(eventoAtualizado.getLocalEvento());
            evento.setVagasDisponiveis(eventoAtualizado.getVagasDisponiveis());
            
            eventoService.salvarEvento(evento);
            redirectAttributes.addFlashAttribute("mensagem", "Evento atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar evento: " + e.getMessage());
        }
        return "redirect:/meus-eventos";
    }
} 