package com.lukeventos.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

public class PasswordUtils {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * Gera um hash para uma senha
     * @param senha A senha em texto puro
     * @return O hash da senha
     */
    public static String gerarHash(String senha) {
        return encoder.encode(senha);
    }
    
    /**
     * Verifica se uma senha corresponde a um hash
     * @param senha A senha em texto puro
     * @param hash O hash armazenado
     * @return true se a senha corresponder ao hash
     */
    public static boolean verificarSenha(String senha, String hash) {
        return encoder.matches(senha, hash);
    }
    
    /**
     * Gera hashes para múltiplas senhas
     * @param senhas Map com identificador e senha
     * @return Map com identificador e hash
     */
    public static Map<String, String> gerarHashes(Map<String, String> senhas) {
        Map<String, String> hashes = new HashMap<>();
        for (Map.Entry<String, String> entry : senhas.entrySet()) {
            hashes.put(entry.getKey(), gerarHash(entry.getValue()));
        }
        return hashes;
    }
    
    /**
     * Gera comandos SQL para atualizar senhas
     * @param hashes Map com email e hash
     * @return Array de comandos SQL
     */
    public static String[] gerarComandosSQL(Map<String, String> hashes) {
        return hashes.entrySet().stream()
            .map(entry -> String.format(
                "UPDATE usuarios SET senha = '%s' WHERE email = '%s';",
                entry.getValue(),
                entry.getKey()
            ))
            .toArray(String[]::new);
    }
    
    /**
     * Salva hashes em um arquivo de propriedades
     * @param hashes Map com identificador e hash
     * @param nomeArquivo Nome do arquivo de saída
     * @throws IOException Se houver erro ao salvar o arquivo
     */
    public static void salvarHashesEmArquivo(Map<String, String> hashes, String nomeArquivo) throws IOException {
        Properties props = new Properties();
        hashes.forEach(props::setProperty);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            props.store(writer, "Generated password hashes");
        }
    }
    
    /**
     * Exemplo de uso da classe
     */
    public static void main(String[] args) {
        try {
            // Exemplo de uso
            Map<String, String> senhas = new HashMap<>();
            senhas.put("admin@eventos.com", "admin123");
            senhas.put("debug@eventos.com", "debug123");
            senhas.put("teste@teste.com", "123456");
            
            // Gerar hashes
            Map<String, String> hashes = gerarHashes(senhas);
            
            // Imprimir hashes
            System.out.println("Hashes gerados:");
            hashes.forEach((email, hash) -> 
                System.out.printf("%s: %s%n", email, hash)
            );
            
            // Gerar comandos SQL
            System.out.println("\nComandos SQL:");
            for (String sql : gerarComandosSQL(hashes)) {
                System.out.println(sql);
            }
            
            // Salvar em arquivo
            salvarHashesEmArquivo(hashes, "password_hashes.properties");
            System.out.println("\nHashes salvos em password_hashes.properties");
            
            // Verificar senhas
            System.out.println("\nVerificando senhas:");
            senhas.forEach((email, senha) -> 
                System.out.printf("%s: %s%n", 
                    email, 
                    verificarSenha(senha, hashes.get(email))
                )
            );
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 