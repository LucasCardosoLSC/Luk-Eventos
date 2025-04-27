package com.lukeventos.components;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.lukeventos.models.Usuario;

@Component
@SessionScope
public class DebugViewManager {
    
    private String currentView = "ADMIN"; // ADMIN, ATLETICA, UNIVERSITARIO
    
    public void setCurrentView(String view) {
        this.currentView = view;
    }
    
    public String getCurrentView() {
        return currentView;
    }
    
    public boolean isDebugger(Usuario usuario) {
        return "debug@eventos.com".equals(usuario.getEmail());
    }
    
    public boolean hasAccess(Usuario usuario, String requiredRole) {
        if (isDebugger(usuario)) {
            return currentView.equals(requiredRole);
        }
        return usuario.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredRole));
    }
} 