package com.lukeventos.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lukeventos.dto.AtleticaRequest;
import com.lukeventos.models.Usuario;
import com.lukeventos.services.AuthService;
import com.lukeventos.services.AtleticaService;
import com.lukeventos.services.UsuarioService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AtleticaService atleticaService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("content", "login");
        return "layout/base";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String senha,
                       RedirectAttributes redirectAttributes) {
        logger.debug("Tentativa de login - Email: {}", email);
        try {
            authService.authenticateUser(email, senha);
            logger.debug("Login bem-sucedido para: {}", email);
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Falha no login para: {}", email, e);
            redirectAttributes.addFlashAttribute("error", "Email ou senha inválidos");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/cadastro/universitario")
    public String cadastroUniversitarioPage(Model model) {
        model.addAttribute("content", "cadastro-universitario");
        return "layout/base";
    }

    @PostMapping("/cadastro/universitario")
    public String cadastroUniversitario(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.criarUsuario(usuario, "ROLE_UNIVERSITARIO");
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso!");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/cadastro/universitario";
        }
    }

    @GetMapping("/cadastro/atletica")
    public String cadastroAtleticaPage(Model model) {
        model.addAttribute("atleticaRequest", new AtleticaRequest());
        model.addAttribute("content", "cadastro-atletica");
        return "layout/base";
    }

    @PostMapping("/cadastro/atletica")
    public String cadastroAtletica(AtleticaRequest atleticaRequest, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = atleticaRequest.getUsuario();
            usuario = usuarioService.criarUsuario(usuario, "ROLE_ATLETICA");
            atleticaRequest.getAtletica().setUsuario(usuario);
            atleticaService.criarAtletica(atleticaRequest.getAtletica(), usuario);
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso!");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/cadastro/atletica";
        }
    }
} 