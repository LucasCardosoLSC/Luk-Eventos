package com.lukeventos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lukeventos.components.DebugViewManager;
import com.lukeventos.models.Usuario;
import com.lukeventos.services.UsuarioService;

@Controller
@RequestMapping("/debug")
public class DebugViewController {

    @Autowired
    private DebugViewManager debugViewManager;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String debugView(Model model) {
        Usuario usuario = usuarioService.getCurrentUser();
        if (!debugViewManager.isDebugger(usuario)) {
            return "redirect:/";
        }
        
        model.addAttribute("currentView", debugViewManager.getCurrentView());
        model.addAttribute("availableViews", new String[]{"ADMIN", "ATLETICA", "UNIVERSITARIO"});
        return "debug";
    }
} 