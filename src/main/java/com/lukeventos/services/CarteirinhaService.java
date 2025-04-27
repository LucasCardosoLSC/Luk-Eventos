package com.lukeventos.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lukeventos.models.Carteirinha;
import com.lukeventos.models.Usuario;
import com.lukeventos.repositories.CarteirinhaRepository;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class CarteirinhaService {

    @Autowired
    private CarteirinhaRepository carteirinhaRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${tess4j.datapath}")
    private String tessDataPath;

    public Carteirinha processarCarteirinha(MultipartFile file) throws IOException, TesseractException {
        // Criar diretório de upload se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Gerar nome único para o arquivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Salvar o arquivo
        Files.copy(file.getInputStream(), filePath);

        // Processar OCR
        String textoExtraido = extrairTextoOCR(filePath.toFile());

        // Criar e salvar carteirinha
        Carteirinha carteirinha = new Carteirinha();
        carteirinha.setImagemUrl(fileName);
        carteirinha.setTextoExtraido(textoExtraido);
        carteirinha.setAprovada(false);
        carteirinha.setDataSubmissao(LocalDateTime.now());

        return carteirinhaRepository.save(carteirinha);
    }

    private String extrairTextoOCR(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("por");
        return tesseract.doOCR(imageFile);
    }

    public List<Carteirinha> listarCarteirinhasPendentes() {
        return carteirinhaRepository.findByAprovada(false);
    }

    public Optional<Carteirinha> buscarPorUsuario(Usuario usuario) {
        return carteirinhaRepository.findByUsuario(usuario);
    }

    public Carteirinha aprovarCarteirinha(Long id) {
        Carteirinha carteirinha = carteirinhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteirinha não encontrada"));
        carteirinha.setAprovada(true);
        carteirinha.setDataAprovacao(LocalDateTime.now());
        return carteirinhaRepository.save(carteirinha);
    }

    public void rejeitarCarteirinha(Long id) {
        Carteirinha carteirinha = carteirinhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteirinha não encontrada"));
        carteirinhaRepository.delete(carteirinha);
    }

    public boolean verificarCarteirinhaAprovada(Usuario usuario) {
        return carteirinhaRepository.existsByUsuarioAndAprovada(usuario, true);
    }
} 