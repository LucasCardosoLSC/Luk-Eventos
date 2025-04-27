package com.lukeventos.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordHashTest {
    
    @Test
    public void testPasswordHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Gerar hash para admin123
        String adminHash = encoder.encode("admin123");
        System.out.println("Hash para admin123: " + adminHash);
        
        // Verificar se a senha admin123 corresponde ao hash
        assertTrue(encoder.matches("admin123", adminHash), "Senha admin123 não corresponde ao hash");
        
        // Gerar hash para debug123
        String debugHash = encoder.encode("debug123");
        System.out.println("Hash para debug123: " + debugHash);
        
        // Verificar se a senha debug123 corresponde ao hash
        assertTrue(encoder.matches("debug123", debugHash), "Senha debug123 não corresponde ao hash");
        
        // Gerar hash para 123456
        String testHash = encoder.encode("123456");
        System.out.println("Hash para 123456: " + testHash);
        
        // Verificar se a senha 123456 corresponde ao hash
        assertTrue(encoder.matches("123456", testHash), "Senha 123456 não corresponde ao hash");
    }
} 