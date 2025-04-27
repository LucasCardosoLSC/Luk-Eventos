package com.lukeventos.dto;

import com.lukeventos.models.Atletica;
import com.lukeventos.models.Usuario;

import lombok.Data;

@Data
public class AtleticaRequest {
    private Atletica atletica;
    private Usuario usuario;
} 