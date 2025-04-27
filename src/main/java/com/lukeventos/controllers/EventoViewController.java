package com.lukeventos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lukeventos.models.Evento;
import com.lukeventos.models.Usuario;
import com.lukeventos.models.Atletica;
import com.lukeventos.services.EventoService;
import com.lukeventos.services.UsuarioService;
import com.lukeventos.repositories.AtleticaRepository;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/eventos")
public class EventoViewController {

    @Autowired
    private EventoService eventoService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AtleticaRepository atleticaRepository;

    private void logToFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("evento_logs.txt", true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + " - " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    public String listarEventos(Model model, CsrfToken token) {
        model.addAttribute("eventos", eventoService.listarEventosAprovados());
        model.addAttribute("content", "eventos");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "layout/base";
    }

    @GetMapping("/{id}")
    public String detalhesEvento(@PathVariable Long id, Model model, CsrfToken token) {
        Evento evento = eventoService.buscarEvento(id).orElseThrow();
        model.addAttribute("evento", evento);
        model.addAttribute("content", "evento-detalhes");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "layout/base";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ATLETICA')")
    public String novoEvento(Model model, CsrfToken token) {
        logToFile("Iniciando criação de novo evento");
        try {
            Usuario usuarioAtual = usuarioService.getCurrentUser();
            logToFile("Usuário atual: " + usuarioAtual.getEmail());
            
            Atletica atletica = atleticaRepository.findByUsuario(usuarioAtual);
            if (atletica == null) {
                logToFile("ERRO: Atlética não encontrada para o usuário");
                throw new RuntimeException("Atlética não encontrada para o usuário");
            }
            
            Evento evento = new Evento();
            evento.setAtletica(atletica);
            evento.setDataInicio(LocalDateTime.now().plusDays(1));
            evento.setDataFim(LocalDateTime.now().plusDays(2));
            evento.setStatus(Evento.StatusEvento.PENDENTE);
            logToFile("Atlética definida com ID: " + atletica.getId());
            
            model.addAttribute("evento", evento);
            model.addAttribute("content", "evento-form");
            if (token != null) {
                model.addAttribute("_csrf", token);
            }
            logToFile("Formulário preparado com sucesso");
            return "layout/base";
        } catch (Exception e) {
            logToFile("ERRO ao preparar formulário: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ATLETICA')")
    public String criarEvento(@Valid Evento evento, BindingResult result, RedirectAttributes redirectAttributes) {
        logToFile("Iniciando processamento do formulário de evento");
        logToFile("Dados do evento recebidos: " + evento.toString());
        
        if (result.hasErrors()) {
            logToFile("Erros de validação encontrados: " + result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.evento", result);
            redirectAttributes.addFlashAttribute("evento", evento);
            return "redirect:/eventos/novo";
        }

        try {
            // Validar atlética
            if (evento.getAtletica() == null || evento.getAtletica().getId() == null) {
                logToFile("Erro: Atlética não definida");
                redirectAttributes.addFlashAttribute("erro", "A atlética é obrigatória.");
                return "redirect:/eventos/novo";
            }

            // Validar datas
            if (evento.getDataInicio().isBefore(LocalDateTime.now())) {
                logToFile("Erro: Data de início anterior à data atual");
                redirectAttributes.addFlashAttribute("erro", "A data de início deve ser futura.");
                return "redirect:/eventos/novo";
            }

            if (evento.getDataFim().isBefore(evento.getDataInicio())) {
                logToFile("Erro: Data de término anterior à data de início");
                redirectAttributes.addFlashAttribute("erro", "A data de término deve ser posterior à data de início.");
                return "redirect:/eventos/novo";
            }

            logToFile("Tentando salvar evento no banco de dados");
            eventoService.criarEvento(evento);
            logToFile("Evento salvo com sucesso");
            
            redirectAttributes.addFlashAttribute("mensagem", "Evento criado com sucesso! Aguardando aprovação.");
            return "redirect:/eventos";
        } catch (Exception e) {
            logToFile("ERRO ao criar evento: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar evento: " + e.getMessage());
            return "redirect:/eventos/novo";
        }
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ATLETICA')")
    public String editarEvento(@PathVariable Long id, Model model, CsrfToken token) {
        Evento evento = eventoService.buscarEvento(id).orElseThrow();
        Usuario usuario = usuarioService.getCurrentUser();
        Atletica atletica = atleticaRepository.findByUsuario(usuario);
        
        // Verificar se o evento pertence à atlética
        if (!evento.getAtletica().getId().equals(atletica.getId())) {
            return "redirect:/eventos";
        }
        
        model.addAttribute("evento", evento);
        model.addAttribute("content", "evento-form");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "layout/base";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ATLETICA')")
    public String atualizarEvento(@PathVariable Long id, @Valid Evento eventoAtualizado, 
                                BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erro", "Por favor, corrija os erros no formulário.");
            return "redirect:/eventos/" + id + "/editar";
        }

        try {
            Evento evento = eventoService.buscarEvento(id).orElseThrow();
            Usuario usuario = usuarioService.getCurrentUser();
            Atletica atletica = atleticaRepository.findByUsuario(usuario);
            
            // Verificar se o evento pertence à atlética
            if (!evento.getAtletica().getId().equals(atletica.getId())) {
                return "redirect:/eventos";
            }

            // Validar datas
            if (eventoAtualizado.getDataInicio().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("erro", "A data de início deve ser futura.");
                return "redirect:/eventos/" + id + "/editar";
            }

            if (eventoAtualizado.getDataFim().isBefore(eventoAtualizado.getDataInicio())) {
                redirectAttributes.addFlashAttribute("erro", "A data de término deve ser posterior à data de início.");
                return "redirect:/eventos/" + id + "/editar";
            }

            // Se o evento estava rejeitado, muda para pendente e limpa o feedback
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
            return "redirect:/eventos/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar evento: " + e.getMessage());
            return "redirect:/eventos/" + id + "/editar";
        }
    }

    @GetMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ATLETICA')")
    public String excluirEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Evento evento = eventoService.buscarEvento(id).orElseThrow();
            Usuario usuario = usuarioService.getCurrentUser();
            Atletica atletica = atleticaRepository.findByUsuario(usuario);
            
            // Verificar se o evento pertence à atlética
            if (!evento.getAtletica().getId().equals(atletica.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para excluir este evento.");
                return "redirect:/eventos/" + id;
            }
            
            // Verificar se o evento pode ser excluído (apenas pendentes ou rejeitados)
            if (evento.getStatus() != Evento.StatusEvento.PENDENTE && evento.getStatus() != Evento.StatusEvento.REJEITADO) {
                redirectAttributes.addFlashAttribute("erro", "Apenas eventos pendentes ou rejeitados podem ser excluídos.");
                return "redirect:/eventos/" + id;
            }
            
            eventoService.deletarEvento(id);
            redirectAttributes.addFlashAttribute("mensagem", "Evento excluído com sucesso!");
            return "redirect:/meus-eventos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir evento: " + e.getMessage());
            return "redirect:/eventos/" + id;
        }
    }

    @GetMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public String aprovarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.aprovarEvento(id);
            redirectAttributes.addFlashAttribute("mensagem", "Evento aprovado com sucesso!");
            return "redirect:/eventos/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aprovar evento: " + e.getMessage());
            return "redirect:/eventos/admin";
        }
    }

    @GetMapping("/{id}/rejeitar")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejeitarEvento(@PathVariable Long id, @RequestParam(required = false) String feedback, RedirectAttributes redirectAttributes) {
        try {
            if (feedback == null || feedback.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "É necessário fornecer um motivo para a rejeição.");
                return "redirect:/eventos/" + id;
            }
            
            eventoService.rejeitarEvento(id, feedback);
            redirectAttributes.addFlashAttribute("mensagem", "Evento rejeitado com sucesso!");
            return "redirect:/eventos/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao rejeitar evento: " + e.getMessage());
            return "redirect:/eventos/admin";
        }
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ATLETICA')")
    public String listarMeusEventos(Model model, CsrfToken token) {
        Usuario usuario = usuarioService.getCurrentUser();
        Atletica atletica = atleticaRepository.findByUsuario(usuario);
        
        if (atletica == null) {
            throw new RuntimeException("Atlética não encontrada para o usuário");
        }
        
        model.addAttribute("eventos", eventoService.listarEventosPorAtletica(atletica));
        model.addAttribute("content", "eventos-atletica");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "layout/base";
    }
} 