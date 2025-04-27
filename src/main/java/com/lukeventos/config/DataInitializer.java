package com.lukeventos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lukeventos.models.Atletica;
import com.lukeventos.models.Perfil;
import com.lukeventos.models.Usuario;
import com.lukeventos.repositories.AtleticaRepository;
import com.lukeventos.repositories.PerfilRepository;
import com.lukeventos.repositories.UsuarioRepository;
import com.lukeventos.services.UsuarioService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private AtleticaRepository atleticaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Inicializando dados base...");
        
        // Criar perfis se não existirem
        List<String> perfisBase = Arrays.asList("ROLE_ADMIN", "ROLE_ATLETICA", "ROLE_UNIVERSITARIO");
        criarPerfisSeNaoExistirem(perfisBase);

        // Criar usuário admin
        criarUsuarioSeNaoExistir(
            "admin@eventos.com",
            "Administrador",
            "admin123",
            Set.of("ROLE_ADMIN"),
            Usuario.TipoUsuario.ADMIN
        );

        // Criar usuário debugger
        criarUsuarioSeNaoExistir(
            "debug@eventos.com",
            "Debugger",
            "debug123",
            Set.of("ROLE_ADMIN", "ROLE_ATLETICA", "ROLE_UNIVERSITARIO"),
            Usuario.TipoUsuario.ADMIN
        );

        // Criar usuário teste
        criarUsuarioSeNaoExistir(
            "teste@teste.com",
            "Usuário Teste",
            "123456",
            Set.of("ROLE_UNIVERSITARIO"),
            Usuario.TipoUsuario.UNIVERSITARIO
        );

        // Criar atlética de teste
        if (!atleticaRepository.existsByNome("Atlética Teste")) {
            Usuario debugger = usuarioRepository.findByEmail("debug@eventos.com")
                .orElseThrow(() -> new RuntimeException("Usuário debugger não encontrado"));

            Atletica atletica = new Atletica();
            atletica.setNome("Atlética Teste");
            atletica.setUsuario(debugger);
            atletica = atleticaRepository.save(atletica);
            logger.info("Atlética criada: {}", atletica.getNome());
        }

        logger.info("Dados base inicializados com sucesso!");
    }

    @Transactional
    private void criarPerfisSeNaoExistirem(List<String> nomesPerfis) {
        for (String nome : nomesPerfis) {
            if (!perfilRepository.existsByNome(nome)) {
                Perfil perfil = new Perfil();
                perfil.setNome(nome);
                perfilRepository.save(perfil);
                logger.info("Perfil criado: {}", nome);
            }
        }
    }

    @Transactional
    private void criarUsuarioSeNaoExistir(String email, String nome, String senha, Set<String> perfis, Usuario.TipoUsuario tipo) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNome(nome);
            
            String senhaCodificada = passwordEncoder.encode(senha);
            logger.debug("Senha original: {}", senha);
            logger.debug("Senha codificada: {}", senhaCodificada);
            usuario.setSenha(senhaCodificada);
            
            usuario.setAtivo(true);
            usuario.setDataCadastro(LocalDateTime.now());
            usuario.setTipo(tipo);

            Set<Perfil> perfisEntity = new HashSet<>();
            for (String perfilNome : perfis) {
                perfilRepository.findByNome(perfilNome)
                    .ifPresent(perfisEntity::add);
            }
            usuario.setPerfis(perfisEntity);

            usuarioRepository.save(usuario);
            logger.info("Usuário criado: {}", email);
        }
    }
} 