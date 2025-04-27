package com.lukeventos.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "carteirinhas")
@Data
public class Carteirinha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @Column(name = "texto_extraido")
    private String textoExtraido;

    private boolean aprovada;

    @Column(name = "data_submissao")
    private LocalDateTime dataSubmissao;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;
} 