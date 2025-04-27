package com.lukeventos.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukeventos.models.Perfil;
import com.lukeventos.models.Usuario;
import com.lukeventos.repositories.PerfilRepository;
import com.lukeventos.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Buscando usuário atual por email: {}", email);
        return buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Iniciando carregamento do usuário por email: {}", email);
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isPresent()) {
                logger.debug("Usuário encontrado: {}", email);
                logger.debug("Perfis do usuário: {}", usuario.get().getPerfis());
                logger.debug("Senha do usuário (hash): {}", usuario.get().getSenha());
                return usuario.get();
            }
            logger.error("Usuário não encontrado: {}", email);
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        } catch (Exception e) {
            logger.error("Erro ao carregar usuário: {}", email, e);
            throw e;
        }
    }

    public Usuario criarUsuario(Usuario usuario, String perfil) {
        logger.debug("Iniciando criação de usuário: {}", usuario.getEmail());
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            logger.error("Email já cadastrado: {}", usuario.getEmail());
            throw new RuntimeException("Email já cadastrado");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);

        Set<Perfil> perfis = new HashSet<>();
        Perfil perfilEntity = perfilRepository.findByNome(perfil)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        perfis.add(perfilEntity);
        usuario.setPerfis(perfis);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        logger.debug("Usuário criado com sucesso: {}", savedUsuario.getEmail());
        return savedUsuario;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        logger.debug("Buscando usuário por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        logger.debug("Buscando usuário por ID: {}", id);
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        logger.debug("Atualizando usuário: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    public void desativarUsuario(Long id) {
        logger.debug("Desativando usuário com ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        logger.debug("Usuário desativado com sucesso: {}", usuario.getEmail());
    }

    public List<Usuario> listarTodos() {
        logger.debug("Listando todos os usuários");
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario cadastrarUniversitario(Usuario usuario) {
        logger.debug("Cadastrando usuário universitário: {}", usuario.getEmail());
        usuario.setTipo(Usuario.TipoUsuario.UNIVERSITARIO);
        usuario.setCarteirinhaStatus(Usuario.StatusCarteirinha.PENDENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario cadastrarAtletica(Usuario usuario) {
        logger.debug("Cadastrando usuário atlética: {}", usuario.getEmail());
        usuario.setTipo(Usuario.TipoUsuario.ATLETICA);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario atualizarCarteirinha(Long usuarioId, String carteirinhaUrl, String textoExtraido) {
        logger.debug("Atualizando carteirinha do usuário: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.setCarteirinhaUrl(carteirinhaUrl);
        usuario.setTextoExtraidoCarteirinha(textoExtraido);
        usuario.setCarteirinhaStatus(Usuario.StatusCarteirinha.PENDENTE);
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario aprovarCarteirinha(Long usuarioId) {
        logger.debug("Aprovando carteirinha do usuário: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.setCarteirinhaStatus(Usuario.StatusCarteirinha.APROVADA);
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario rejeitarCarteirinha(Long usuarioId) {
        logger.debug("Rejeitando carteirinha do usuário: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.setCarteirinhaStatus(Usuario.StatusCarteirinha.REJEITADA);
        return usuarioRepository.save(usuario);
    }
    
    public List<Usuario> listarUniversitariosComCarteirinhaPendente() {
        logger.debug("Listando universitários com carteirinha pendente");
        return usuarioRepository.findUniversitariosComCarteirinhaPendente();
    }
    
    public List<Usuario> listarAtleticas() {
        logger.debug("Listando todas as atléticas");
        return usuarioRepository.findAllAtleticas();
    }
    
    public List<Usuario> listarUniversitariosAprovados() {
        logger.debug("Listando universitários aprovados");
        return usuarioRepository.findUniversitariosAprovados();
    }
    
    public boolean existePorEmail(String email) {
        logger.debug("Verificando existência de usuário por email: {}", email);
        return usuarioRepository.existsByEmail(email);
    }
    
    public Long contarUniversitariosComCarteirinhaPendente() {
        logger.debug("Contando universitários com carteirinha pendente");
        return usuarioRepository.countUniversitariosComCarteirinhaPendente();
    }
} 