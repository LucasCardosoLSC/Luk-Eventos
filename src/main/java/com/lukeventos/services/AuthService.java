package com.lukeventos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lukeventos.models.Usuario;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    public void authenticateUser(String email, String senha) {
        logger.debug("Iniciando processo de autenticação para o usuário: {}", email);
        try {
            logger.debug("Criando token de autenticação para o usuário: {}", email);
            
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(email, senha);
            
            logger.debug("Tentando autenticar o usuário: {}", email);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            
            logger.debug("Usuário autenticado com sucesso: {}", email);
            logger.debug("Autoridades do usuário: {}", authentication.getAuthorities());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Contexto de segurança atualizado para o usuário: {}", email);
        } catch (BadCredentialsException e) {
            logger.error("Credenciais inválidas para o usuário: {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao autenticar usuário: {}", email, e);
            throw e;
        }
    }

    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Buscando usuário atual: {}", authentication.getName());
        return usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
} 