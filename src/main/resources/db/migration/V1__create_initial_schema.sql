-- =========================
-- TABELA ENDERECO
-- =========================
CREATE TABLE Endereco (
    id VARCHAR(255) PRIMARY KEY,
    cep VARCHAR(9) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    bairro VARCHAR(255) NOT NULL,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(255),
    complemento VARCHAR(255)
);

-- =========================
-- TABELA USUARIO
-- =========================
CREATE TABLE Usuario (
    id VARCHAR(255) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    role VARCHAR(20) CHECK (role IN ('TUTOR', 'FUNCIONARIO', 'ADMIN')) NOT NULL,
    cpf VARCHAR(14),
    telefone VARCHAR(15),
    endereco_id VARCHAR(255),
    tipo_usuario VARCHAR(255),
    CONSTRAINT fk_endereco_id FOREIGN KEY (endereco_id) REFERENCES endereco (id)
);

-- =========================
-- TABELA ANIMAL
-- =========================
CREATE TABLE Animal (
    id VARCHAR(255) PRIMARY KEY,
    imagem VARCHAR(255),
    matricula VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    raca VARCHAR(255) NOT NULL,
    sexo VARCHAR(20) CHECK (sexo IN ('MACHO', 'FEMEA')) NOT NULL,
    porte VARCHAR(20) CHECK (porte IN ('PEQUENO', 'MEDIO', 'GRANDE')) NOT NULL,
    descricao VARCHAR(255),
    data_nascimento DATE NOT NULL,
    numero_tag VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE NOT NULL
);

-- =========================
-- TABELA TUTOR_ANIMAIS
-- =========================
CREATE TABLE Tutor_Animais (
    animal_id VARCHAR(255),
    tutor_id VARCHAR(255),
    PRIMARY KEY (animal_id, tutor_id),
    FOREIGN KEY (animal_id) REFERENCES Animal(id),
    FOREIGN KEY (tutor_id) REFERENCES Usuario(id)
);

-- =========================
-- TABELA FUNCIONARIO_ANIMAIS
-- =========================
CREATE TABLE Funcionario_Animais (
    animal_id VARCHAR(255),
    funcionario_id VARCHAR(255),
    PRIMARY KEY (animal_id, funcionario_id),
    FOREIGN KEY (animal_id) REFERENCES Animal(id),
    FOREIGN KEY (funcionario_id) REFERENCES Usuario(id)
);


-- =========================
-- TABELA TAG
-- =========================
CREATE TABLE Tag (
    id VARCHAR(255) PRIMARY KEY,
    numero VARCHAR(255) NOT NULL,
    animal_id VARCHAR(255),
    latitude VARCHAR(255) NOT NULL,
    longitude VARCHAR(255) NOT NULL,
    data_criado TIMESTAMP NOT NULL,
    saida_nao_autorizada BOOLEAN DEFAULT FALSE,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT fk_animal_id FOREIGN KEY (animal_id) REFERENCES animal (id)
);

-- =========================
-- TABELA LOCAL_COORDENADAS
-- =========================
CREATE TABLE Local_Coordenadas (
    id VARCHAR(255) PRIMARY KEY,
    latitude VARCHAR(255) NOT NULL,
    longitude VARCHAR(255) NOT NULL,
    raio INT NOT NULL,
    cep VARCHAR(9) NOT NULL
);

-- =========================
-- TABELA EMAIL_TOKEN
-- =========================
CREATE TABLE Email_Token (
    id VARCHAR(255) PRIMARY KEY,
    token INT NOT NULL,
    usuario_id VARCHAR(255) NOT NULL,
    data_criado TIMESTAMP NOT NULL,
    data_expirado TIMESTAMP NOT NULL,
    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- =========================
-- TABELA ANEXO
-- =========================
-- CREATE TABLE anexo (
--     id VARCHAR(36) PRIMARY KEY,
--     nome_arquivo VARCHAR(255),
--     url VARCHAR(1000),
--     usuario_id VARCHAR(36),
--     CONSTRAINT fk_anexo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
-- );