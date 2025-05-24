-- Script de Criação de Tabelas para o Projeto DailyTasks
-- Banco de Dados: PostgreSQL

BEGIN;

-- 1. Tabela Funcionario
CREATE TABLE IF NOT EXISTS Funcionario (
    funcionario_id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cargo VARCHAR(100),
    data_admissao DATE DEFAULT CURRENT_DATE,
    ativo BOOLEAN DEFAULT TRUE
);

COMMENT ON TABLE Funcionario IS 'Tabela para armazenar informações dos funcionários.';
COMMENT ON COLUMN Funcionario.funcionario_id IS 'Identificador único do funcionário (Chave Primária).';
COMMENT ON COLUMN Funcionario.nome IS 'Nome completo do funcionário.';
COMMENT ON COLUMN Funcionario.email IS 'Endereço de e-mail do funcionário (deve ser único).';
COMMENT ON COLUMN Funcionario.cargo IS 'Cargo ou posição do funcionário na empresa.';
COMMENT ON COLUMN Funcionario.data_admissao IS 'Data de admissão do funcionário na empresa.';
COMMENT ON COLUMN Funcionario.ativo IS 'Indica se o funcionário está ativo (TRUE) ou inativo (FALSE).';

-- 2. Tabela Equipe
CREATE TABLE IF NOT EXISTS Equipe (
    equipe_id SERIAL PRIMARY KEY,
    nome_equipe VARCHAR(100) NOT NULL UNIQUE,
    descricao_equipe TEXT
);

COMMENT ON TABLE Equipe IS 'Tabela para armazenar informações sobre as equipes.';
COMMENT ON COLUMN Equipe.equipe_id IS 'Identificador único da equipe (Chave Primária).';
COMMENT ON COLUMN Equipe.nome_equipe IS 'Nome da equipe (deve ser único).';
COMMENT ON COLUMN Equipe.descricao_equipe IS 'Descrição opcional da equipe.';

-- 3. Tabela Funcionario_Equipe (Tabela de Associação)
CREATE TABLE IF NOT EXISTS Funcionario_Equipe (
    funcionario_id INT NOT NULL,
    equipe_id INT NOT NULL,
    PRIMARY KEY (funcionario_id, equipe_id),
    CONSTRAINT fk_funcionario
        FOREIGN KEY (funcionario_id) REFERENCES Funcionario (funcionario_id) ON DELETE CASCADE,
    CONSTRAINT fk_equipe
        FOREIGN KEY (equipe_id) REFERENCES Equipe (equipe_id) ON DELETE CASCADE
);

COMMENT ON TABLE Funcionario_Equipe IS 'Tabela de associação para o relacionamento N-M entre Funcionários e Equipes.';
COMMENT ON COLUMN Funcionario_Equipe.funcionario_id IS 'Chave estrangeira referenciando Funcionario.funcionario_id.';
COMMENT ON COLUMN Funcionario_Equipe.equipe_id IS 'Chave estrangeira referenciando Equipe.equipe_id.';

-- 4. Tabela Lista
CREATE TABLE IF NOT EXISTS Lista (
    lista_id SERIAL PRIMARY KEY,
    nome_lista VARCHAR(150) NOT NULL,
    descricao_lista TEXT,
    data_criacao TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    criador_id INT,
    CONSTRAINT fk_criador_lista
        FOREIGN KEY (criador_id) REFERENCES Funcionario (funcionario_id) ON DELETE SET NULL
);

COMMENT ON TABLE Lista IS 'Tabela para armazenar listas de tarefas.';
COMMENT ON COLUMN Lista.lista_id IS 'Identificador único da lista (Chave Primária).';
COMMENT ON COLUMN Lista.nome_lista IS 'Nome da lista de tarefas.';
COMMENT ON COLUMN Lista.descricao_lista IS 'Descrição opcional da lista.';
COMMENT ON COLUMN Lista.data_criacao IS 'Data e hora de criação da lista.';
COMMENT ON COLUMN Lista.criador_id IS 'Chave estrangeira referenciando o Funcionario que criou a lista. Pode ser NULO se o criador for removido.';

-- 5. Tabela Tarefa
CREATE TABLE IF NOT EXISTS Tarefa (
    tarefa_id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    data_vencimento DATE,
    prioridade VARCHAR(20) DEFAULT 'Média' CHECK (prioridade IN ('Baixa', 'Média', 'Alta', 'Urgente')),
    status VARCHAR(50) NOT NULL DEFAULT 'Pendente' CHECK (status IN ('Pendente', 'Em Andamento', 'Concluída', 'Cancelada', 'Bloqueada', 'Aguardando Avaliação')),
    lista_id INT NOT NULL,
    responsavel_id INT,
    criador_id INT,
    CONSTRAINT fk_lista_tarefa
        FOREIGN KEY (lista_id) REFERENCES Lista (lista_id) ON DELETE CASCADE,
    CONSTRAINT fk_responsavel_tarefa
        FOREIGN KEY (responsavel_id) REFERENCES Funcionario (funcionario_id) ON DELETE SET NULL,
    CONSTRAINT fk_criador_tarefa
        FOREIGN KEY (criador_id) REFERENCES Funcionario (funcionario_id) ON DELETE SET NULL
);

COMMENT ON TABLE Tarefa IS 'Tabela para armazenar os detalhes das tarefas.';
COMMENT ON COLUMN Tarefa.tarefa_id IS 'Identificador único da tarefa (Chave Primária).';
COMMENT ON COLUMN Tarefa.titulo IS 'Título ou nome breve da tarefa.';
COMMENT ON COLUMN Tarefa.descricao IS 'Descrição detalhada da tarefa.';
COMMENT ON COLUMN Tarefa.data_criacao IS 'Data e hora de criação da tarefa.';
COMMENT ON COLUMN Tarefa.data_vencimento IS 'Data limite para a conclusão da tarefa.';
COMMENT ON COLUMN Tarefa.prioridade IS 'Nível de prioridade da tarefa (Baixa, Média, Alta, Urgente).';
COMMENT ON COLUMN Tarefa.status IS 'Estado atual da tarefa (Pendente, Em Andamento, Concluída, etc.).';
COMMENT ON COLUMN Tarefa.lista_id IS 'Chave estrangeira referenciando a Lista à qual esta tarefa pertence. Se a Lista for deletada, as tarefas associadas também serão.';
COMMENT ON COLUMN Tarefa.responsavel_id IS 'Chave estrangeira referenciando o Funcionario responsável pela tarefa. Pode ser NULO se não atribuída ou se o responsável for removido.';
COMMENT ON COLUMN Tarefa.criador_id IS 'Chave estrangeira referenciando o Funcionario que criou a tarefa. Pode ser NULO se o criador for removido.';

-- Fim da Transação
COMMIT;

-- Mensagem de Conclusão (apenas um comentário SQL)
-- SCRIPT DE CRIAÇÃO DE TABELAS EXECUTADO COM SUCESSO.