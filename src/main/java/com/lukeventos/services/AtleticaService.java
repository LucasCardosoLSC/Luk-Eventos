package com.lukeventos.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lukeventos.models.Atletica;
import com.lukeventos.models.Usuario;
import com.lukeventos.repositories.AtleticaRepository;

@Service
public class AtleticaService {

    @Autowired
    private AtleticaRepository atleticaRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Atletica criarAtletica(Atletica atletica, Usuario usuario) {
        usuario = usuarioService.criarUsuario(usuario, "ROLE_ATLETICA");
        atletica.setUsuario(usuario);
        return atleticaRepository.save(atletica);
    }

    public Optional<Atletica> buscarPorId(Long id) {
        return atleticaRepository.findById(id);
    }

    public Atletica buscarPorUsuario(Usuario usuario) {
        return atleticaRepository.findByUsuario(usuario);
    }

    public Atletica atualizarAtletica(Atletica atletica) {
        return atleticaRepository.save(atletica);
    }

    public Atletica salvarAtletica(Atletica atletica) {
        return atleticaRepository.save(atletica);
    }
} 