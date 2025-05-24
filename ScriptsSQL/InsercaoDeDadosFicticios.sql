-- Script de Inserção de Dados Fictícios para o Projeto DailyTasks
-- Banco de Dados: PostgreSQL

-- Início da Transação
BEGIN;


-- Reiniciar as sequências para que os IDs comecem de 1 (ajuste os nomes das sequências se necessário)
-- O nome padrão da sequência é geralmente <tabela>_<coluna_id>_seq
ALTER SEQUENCE funcionario_funcionario_id_seq RESTART WITH 1;
ALTER SEQUENCE equipe_equipe_id_seq RESTART WITH 1;
ALTER SEQUENCE lista_lista_id_seq RESTART WITH 1;
ALTER SEQUENCE tarefa_tarefa_id_seq RESTART WITH 1;


-- 1. Inserir Funcionários
INSERT INTO Funcionario (nome, email, cargo, data_admissao, ativo) VALUES
('João Silva', 'joao.silva@example.com', 'Desenvolvedor Pleno', '2023-01-15', TRUE),
('Maria Oliveira', 'maria.oliveira@example.com', 'Gerente de Projetos', '2022-05-20', TRUE),
('Carlos Pereira', 'carlos.pereira@example.com', 'Analista de QA', '2023-07-01', TRUE),
('Ana Costa', 'ana.costa@example.com', 'Desenvolvedora Júnior', '2024-02-10', TRUE),
('Pedro Martins', 'pedro.martins@example.com', 'UX Designer', '2022-11-05', FALSE);
-- IDs esperados: 1, 2, 3, 4, 5

-- 2. Inserir Equipes
INSERT INTO Equipe (nome_equipe, descricao_equipe) VALUES
('Equipe Águia', 'Desenvolvimento de novos produtos e features core.'),
('Equipe Leão', 'Manutenção de sistemas legados e suporte N3.'),
('Equipe Tigre', 'Qualidade de Software, testes automatizados e manuais.');
-- IDs esperados: 1, 2, 3

-- 3. Associar Funcionários às Equipes (Funcionario_Equipe)
-- João Silva (ID 1) na Equipe Águia (ID 1)
INSERT INTO Funcionario_Equipe (funcionario_id, equipe_id) VALUES (1, 1);
-- Maria Oliveira (ID 2) na Equipe Águia (ID 1)
INSERT INTO Funcionario_Equipe (funcionario_id, equipe_id) VALUES (2, 1);
-- Maria Oliveira (ID 2) na Equipe Leão (ID 2)
INSERT INTO Funcionario_Equipe (funcionario_id, equipe_id) VALUES (2, 2);
-- Carlos Pereira (ID 3) na Equipe Tigre (ID 3)
INSERT INTO Funcionario_Equipe (funcionario_id, equipe_id) VALUES (3, 3);
-- Ana Costa (ID 4) na Equipe Águia (ID 1)
INSERT INTO Funcionario_Equipe (funcionario_id, equipe_id) VALUES (4, 1);

-- 4. Inserir Listas de Tarefas
-- Maria Oliveira (ID 2) será a criadora das primeiras listas
INSERT INTO Lista (nome_lista, descricao_lista, data_criacao, criador_id) VALUES
('Backend API v1.0', 'Desenvolvimento dos endpoints da API principal', CURRENT_TIMESTAMP - INTERVAL '10 days', 2),
('Frontend App v1.0', 'Desenvolvimento da interface do usuário para o novo app', CURRENT_TIMESTAMP - INTERVAL '8 days', 2);
-- IDs esperados: 1, 2

-- Carlos Pereira (ID 3) será o criador da lista de testes
INSERT INTO Lista (nome_lista, descricao_lista, data_criacao, criador_id) VALUES
('Testes Funcionais API v1.0', 'Planejamento e execução de testes funcionais para a API', CURRENT_TIMESTAMP - INTERVAL '5 days', 3);
-- ID esperado: 3

-- João Silva (ID 1) como criador
INSERT INTO Lista (nome_lista, descricao_lista, data_criacao, criador_id) VALUES
('Documentação Técnica', 'Criação e revisão da documentação técnica do projeto', CURRENT_TIMESTAMP - INTERVAL '3 days', 1);
-- ID esperado: 4


-- 5. Inserir Tarefas
-- Tarefas para a Lista 'Backend API v1.0' (ID 1)
INSERT INTO Tarefa (titulo, descricao, data_criacao, data_vencimento, prioridade, status, lista_id, responsavel_id, criador_id) VALUES
('Implementar endpoint de autenticação', 'Usar JWT para autenticação de usuários. Incluir refresh token.', CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_DATE + INTERVAL '7 days', 'Alta', 'Em Andamento', 1, 1, 2),
('Refatorar módulo de pagamento', 'Otimizar performance e legibilidade do código do gateway de pagamento.', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_DATE + INTERVAL '14 days', 'Urgente', 'Pendente', 1, 1, 2),
('Modelagem do banco de dados para nova feature X', 'Definir esquema para a feature X e preparar migrations.', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_DATE + INTERVAL '5 days', 'Média', 'Pendente', 1, 4, 2);
-- IDs esperados: 1, 2, 3

-- Tarefas para a Lista 'Frontend App v1.0' (ID 2)
INSERT INTO Tarefa (titulo, descricao, data_criacao, data_vencimento, prioridade, status, lista_id, responsavel_id, criador_id) VALUES
('Desenvolver tela de login', 'Tela de login responsiva para web e mobile, com validação de campos.', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_DATE + INTERVAL '10 days', 'Alta', 'Pendente', 2, 4, 2),
('Criar componente de dashboard principal', 'Componente reutilizável para exibir métricas chave.', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_DATE + INTERVAL '12 days', 'Média', 'Pendente', 2, 4, 2);
-- IDs esperados: 4, 5

-- Tarefas para a Lista 'Testes Funcionais API v1.0' (ID 3)
INSERT INTO Tarefa (titulo, descricao, data_criacao, data_vencimento, prioridade, status, lista_id, responsavel_id, criador_id) VALUES
('Criar casos de teste para CRUD de usuários', 'Cobrir todos os cenários de criação, leitura, atualização e deleção.', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_DATE + INTERVAL '5 days', 'Média', 'Pendente', 3, 3, 3),
('Testar integração com gateway de pagamento', 'Realizar testes end-to-end da funcionalidade de pagamento na API.', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_DATE + INTERVAL '12 days', 'Alta', 'Bloqueada', 3, 3, 3),
('Executar testes de regressão v0.9', 'Garantir que as últimas alterações não quebraram funcionalidades existentes.', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_DATE + INTERVAL '2 days', 'Alta', 'Em Andamento', 3, 3, 3);
-- IDs esperados: 6, 7, 8

-- Tarefa para a Lista 'Documentação Técnica' (ID 4)
INSERT INTO Tarefa (titulo, descricao, data_criacao, data_vencimento, prioridade, status, lista_id, responsavel_id, criador_id) VALUES
('Revisar documentação da API de Produtos', 'Verificar clareza, exemplos e completude da documentação existente.', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_DATE + INTERVAL '3 days', 'Média', 'Concluída', 4, 1, 1);
-- ID esperado: 9

-- Tarefa sem responsável definido (demonstração de responsavel_id NULL)
INSERT INTO Tarefa (titulo, descricao, data_criacao, data_vencimento, prioridade, status, lista_id, responsavel_id, criador_id) VALUES
('Definir escopo da v2 da API', 'Brainstorm e documentação inicial para a próxima versão da API.', CURRENT_TIMESTAMP, CURRENT_DATE + INTERVAL '30 days', 'Baixa', 'Pendente', 1, NULL, 2);
-- ID esperado: 10

-- Fim da Transação
COMMIT;

-- Mensagem de Conclusão (apenas um comentário SQL)
-- SCRIPT DE INSERÇÃO DE DADOS FICTÍCIOS EXECUTADO COM SUCESSO.