-- Criação da tabela de perfis
CREATE TABLE perfis (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

-- Inserção dos perfis padrão
INSERT INTO perfis (nome) VALUES ('ROLE_ADMIN');
INSERT INTO perfis (nome) VALUES ('ROLE_ATLETICA');
INSERT INTO perfis (nome) VALUES ('ROLE_UNIVERSITARIO');

-- Criação da tabela de usuários
CREATE TABLE usuarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_usuario VARCHAR(20) NOT NULL DEFAULT 'UNIVERSITARIO',
    carteirinha_url VARCHAR(255),
    carteirinha_status VARCHAR(20) DEFAULT 'PENDENTE',
    texto_extraido_carteirinha TEXT,
    CONSTRAINT chk_tipo_usuario CHECK (tipo_usuario IN ('ADMIN', 'ATLETICA', 'UNIVERSITARIO')),
    CONSTRAINT chk_carteirinha_status CHECK (carteirinha_status IN ('PENDENTE', 'APROVADA', 'REJEITADA'))
);

-- Criação da tabela de relacionamento entre usuários e perfis
CREATE TABLE usuarios_perfis (
    usuario_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (perfil_id) REFERENCES perfis(id) ON DELETE CASCADE
);

-- Criação da tabela de carteirinhas
CREATE TABLE carteirinhas (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    imagem_url VARCHAR(255) NOT NULL,
    texto_extraido TEXT,
    aprovada BOOLEAN NOT NULL DEFAULT FALSE,
    data_submissao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_aprovacao TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Criação da tabela de atléticas
CREATE TABLE atleticas (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT,
    nome VARCHAR(100) NOT NULL,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Criação da tabela de eventos
CREATE TABLE eventos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    atletica_id BIGINT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    local_evento VARCHAR(255) NOT NULL,
    vagas_disponiveis INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    feedback TEXT,
    FOREIGN KEY (atletica_id) REFERENCES atleticas(id) ON DELETE CASCADE,
    CONSTRAINT chk_status_evento CHECK (status IN ('PENDENTE', 'APROVADO', 'REJEITADO'))
);

-- Criação da tabela de inscrições em eventos
CREATE TABLE inscricoes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_inscricao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE (evento_id, usuario_id),
    CONSTRAINT chk_status_inscricao CHECK (status IN ('PENDENTE', 'APROVADA', 'REJEITADA'))
);

-- Criação de índices para melhor performance
CREATE INDEX idx_usuarios_tipo ON usuarios(tipo_usuario);
CREATE INDEX idx_eventos_status ON eventos(status);
CREATE INDEX idx_carteirinhas_status ON usuarios(carteirinha_status); 