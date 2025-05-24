package org.example;

import dao.*;
import model.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    // DAOs
    private static final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private static final EquipeDAO equipeDAO = new EquipeDAO();
    private static final FuncionarioEquipeDAO funcionarioEquipeDAO = new FuncionarioEquipeDAO();
    private static final ListaDAO listaDAO = new ListaDAO();
    private static final TarefaDAO tarefaDAO = new TarefaDAO();

    // Scanner
    private static final Scanner scanner = new Scanner(System.in);

    // Formatter para datas
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    // Usuário logado na sessão atual
    private static Funcionario usuarioLogado = null;

    public static void main(String[] args) {
        System.out.println("--- Bem-vindo ao DailyTasks ---");
        boolean executandoApp = true;

        while (executandoApp) {
            usuarioLogado = null; // Garante que precise de login a cada "sessão" principal

            // Loop de login
            while (usuarioLogado == null && executandoApp) {
                usuarioLogado = tentarLogin(scanner); // Passa o scanner

                if (usuarioLogado == null) {
                    System.out.print("Login falhou. Tentar novamente (s/n)? Ou digite 'sairapp' para encerrar: ");
                    String tentarDeNovo = scanner.nextLine().trim().toLowerCase();
                    if (tentarDeNovo.equals("sairapp")) {
                        executandoApp = false; // Sinaliza para sair do loop principal do app
                    } else if (!tentarDeNovo.equals("s")) {
                        // Se não quer tentar de novo e não digitou 'sairapp', pode-se assumir que quer sair também
                        // ou apenas quebrar este loop interno e deixar o `while(executandoApp)` decidir.
                        // Para clareza, se não for 's' ou 'sairapp', vamos assumir que quer sair do app.
                        executandoApp = false;
                    }
                }
            }

            if (usuarioLogado != null && executandoApp) {
                System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + usuarioLogado.getNome() +
                        " (Tipo: " + usuarioLogado.getTipoUsuario() + ")");
                if ("admin".equalsIgnoreCase(usuarioLogado.getTipoUsuario())) {
                    menuAdmin();
                } else if ("funcionario".equalsIgnoreCase(usuarioLogado.getTipoUsuario())) {
                    menuFuncionario();
                }

            }
        }

        System.out.println("\n--- DailyTasks Encerrado ---");
        scanner.close();
    }

    private static Funcionario tentarLogin(Scanner scannerLocal) {
        System.out.println("\n--- Tela de Login ---");
        System.out.print("Usuário: ");
        String email = scannerLocal.nextLine().trim();
        System.out.print("Senha: ");
        String senhaInput = scannerLocal.nextLine();


        try {
            Funcionario funcionario = funcionarioDAO.buscarPorEmail(email);
            if (funcionario != null) {
                // Comparação de senha em TEXTO PLANO (INSEGURO!)
                if (funcionario.getSenha() != null && funcionario.getSenha().equals(senhaInput)) {
                    return funcionario; // Login bem-sucedido
                } else {
                    System.out.println("Senha incorreta.");
                }
            } else {
                System.out.println("Usuário com email '" + email + "' não encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro de SQL ao tentar fazer login: " + e.getMessage());
            // e.printStackTrace(); // Para debug
        }
        return null; // Falha no login
    }

    // --- MENUS PRINCIPAIS POR TIPO DE USUÁRIO ---
    private static void menuAdmin() {
        int opcao;
        do {
            System.out.println("\n--- Menu Administrador [" + usuarioLogado.getNome() + "] ---");
            System.out.println("1. Gerenciar Funcionários");
            System.out.println("2. Gerenciar Equipes");
            System.out.println("3. Gerenciar Associações Funcionário-Equipe");
            System.out.println("4. Gerenciar Listas de Tarefas");
            System.out.println("5. Gerenciar Tarefas (CRUD completo)");
            System.out.println("6. Atribuir Tarefa a Funcionário");
            System.out.println("0. Logout (Voltar à tela de login)");
            System.out.print("Escolha uma opção: ");
            opcao = lerOpcao(); // lerOpcao já consome a nova linha se bem implementado
            // scanner.nextLine(); // Se lerOpcao não consumir, descomente

            switch (opcao) {
                case 1: gerenciarFuncionariosAdmin(); break;
                case 2: gerenciarEquipesAdmin(); break;
                case 3: gerenciarAssociacoesFuncionarioEquipeAdmin(); break;
                case 4: gerenciarListasAdmin(); break;
                case 5: gerenciarTarefasAdmin(); break;
                case 6: atribuirTarefaAdmin(); break;
                case 0:
                    System.out.println("Fazendo logout...");
                    usuarioLogado = null; // ESSENCIAL para forçar novo login
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0 && usuarioLogado != null); // Loop continua se não for logout e se ainda logado
    }

    private static void menuFuncionario() {
        int opcao;
        do {
            System.out.println("\n--- Menu Funcionário [" + usuarioLogado.getNome() + "] ---");
            System.out.println("1. Ver Minhas Tarefas Atribuídas");
            System.out.println("2. Atualizar Status de uma Minha Tarefa");
            System.out.println("0. Logout (Voltar à tela de login)");
            System.out.print("Escolha uma opção: ");
            opcao = lerOpcao();
            // scanner.nextLine(); // Se lerOpcao não consumir

            switch (opcao) {
                case 1: verMinhasTarefasFuncionario(); break;
                case 2: atualizarStatusTarefaFuncionario(); break;
                case 0:
                    System.out.println("Fazendo logout...");
                    usuarioLogado = null; // ESSENCIAL para forçar novo login
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0 && usuarioLogado != null);
    }

    // --- MÉTODOS AUXILIARES DE LEITURA DE ENTRADA ---
    private static int lerOpcao() {
        int opcao = -1;
        if (scanner.hasNextInt()) {
            opcao = scanner.nextInt();
        } else {
            System.out.println("Entrada inválida. Por favor, digite um número.");
        }
        scanner.nextLine(); // Consome a nova linha deixada pelo nextInt() ou pela entrada inválida
        return opcao;
    }

    private static long lerLong(String prompt) {
        long valor = -1;
        System.out.print(prompt);
        if (scanner.hasNextLong()) {
            valor = scanner.nextLong();
        } else {
            System.out.println("Entrada inválida. Esperado um número longo.");
        }
        scanner.nextLine(); // Consome a nova linha
        return valor;
    }

    private static String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static Date lerData(String prompt, boolean permitirNulo) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD" + (permitirNulo ? " ou enter para nulo" : "") + "): ");
            String dataStr = scanner.nextLine().trim();
            if (dataStr.isEmpty() && permitirNulo) return null;
            if (dataStr.isEmpty() && !permitirNulo) {
                System.out.println("Este campo não pode ser vazio.");
                continue;
            }
            try {
                return Date.valueOf(LocalDate.parse(dataStr, dateFormatter));
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use YYYY-MM-DD.");
            }
        }
    }

    private static Timestamp lerTimestamp(String prompt, boolean permitirNulo) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD HH:MM:SS" + (permitirNulo ? " ou enter para nulo/agora" : "") + "): ");
            String tsStr = scanner.nextLine().trim();
            if (tsStr.isEmpty() && permitirNulo) return new Timestamp(System.currentTimeMillis()); // Ou null se preferir
            if (tsStr.isEmpty() && !permitirNulo) {
                System.out.println("Este campo não pode ser vazio.");
                continue;
            }
            try {
                LocalDateTime ldt = LocalDateTime.parse(tsStr, dateTimeFormatter);
                return Timestamp.valueOf(ldt);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de timestamp inválido. Use YYYY-MM-DD HH:MM:SS.");
            }
        }
    }

    private static boolean lerBooleano(String prompt, String valorPadraoInfo) {
        System.out.print(prompt + " (s/n" + valorPadraoInfo + "): ");
        String resposta = scanner.nextLine().trim().toLowerCase();
        if (resposta.isEmpty() && !valorPadraoInfo.isEmpty()) { // Se enter e tem padrão
            // Lógica para usar o padrão (ex: se valorPadraoInfo indica "padrão: s")
            // Para simplificar, se enter, não muda o valor no update. Aqui, força uma escolha.
            System.out.println("Escolha 's' ou 'n'.");
            return lerBooleano(prompt, valorPadraoInfo); // Repete
        }
        return resposta.equals("s");
    }

    // --- MÉTODOS DE EXIBIÇÃO FORMATADA ---
    private static void exibirFuncionarioFormatado(Funcionario f) {
        if (f == null) { System.out.println("Funcionário nulo ou não encontrado."); return; }
        System.out.println("------------------------------------");
        System.out.println("ID Funcionário: " + f.getFuncionarioId());
        System.out.println("Nome: " + f.getNome());
        System.out.println("Email: " + f.getEmail());
        System.out.println("Cargo: " + (f.getCargo() != null ? f.getCargo() : "N/A"));
        System.out.println("Data Admissão: " + (f.getDataAdmissao() != null ? dateFormatter.format(f.getDataAdmissao().toLocalDate()) : "N/A"));
        System.out.println("Ativo: " + (f.isAtivo() ? "Sim" : "Não"));
        System.out.println("Tipo Usuário: " + f.getTipoUsuario());
        // NÃO exibir senha
        System.out.println("------------------------------------");
    }

    private static void exibirEquipeFormatado(Equipe eq) {
        if (eq == null) { System.out.println("Equipe nula ou não encontrada."); return; }
        System.out.println("------------------------------------");
        System.out.println("ID Equipe: " + eq.getEquipeId());
        System.out.println("Nome Equipe: " + eq.getNomeEquipe());
        System.out.println("Descrição: " + (eq.getDescricaoEquipe() != null ? eq.getDescricaoEquipe() : "N/A"));
        System.out.println("------------------------------------");
    }

    private static void exibirListaFormatado(Lista l) {
        if (l == null) { System.out.println("Lista nula ou não encontrada."); return; }
        System.out.println("------------------------------------");
        System.out.println("ID Lista: " + l.getListaId());
        System.out.println("Nome Lista: " + l.getNomeLista());
        System.out.println("Descrição: " + (l.getDescricaoLista() != null ? l.getDescricaoLista() : "N/A"));
        System.out.println("Data Criação: " + (l.getDataCriacao() != null ? dateTimeFormatter.format(l.getDataCriacao().toLocalDateTime()) : "N/A"));
        if (l.getCriadorId() != null) {
            try {
                Funcionario criador = funcionarioDAO.buscarPorId(l.getCriadorId());
                System.out.println("Criador: " + (criador != null ? criador.getNome() + " (ID: " + l.getCriadorId() + ")" : "ID " + l.getCriadorId() + " (não encontrado)"));
            } catch (SQLException e) { System.out.println("Criador ID: " + l.getCriadorId() + " (Erro ao buscar nome)"); }
        } else { System.out.println("Criador: N/A"); }
        System.out.println("------------------------------------");
    }

    private static void exibirTarefaFormatado(Tarefa t) {
        if (t == null) { System.out.println("Tarefa nula ou não encontrada."); return; }
        System.out.println("------------------------------------");
        System.out.println("ID Tarefa: " + t.getTarefaId());
        System.out.println("Título: " + t.getTitulo());
        System.out.println("Descrição: " + (t.getDescricao() != null && !t.getDescricao().isEmpty() ? t.getDescricao() : "Nenhuma"));
        System.out.println("Status: " + t.getStatus());
        System.out.println("Prioridade: " + t.getPrioridade());
        System.out.println("Data Criação: " + (t.getDataCriacao() != null ? dateTimeFormatter.format(t.getDataCriacao().toLocalDateTime()) : "N/A"));
        System.out.println("Data Vencimento: " + (t.getDataVencimento() != null ? dateFormatter.format(t.getDataVencimento().toLocalDate()) : "N/A"));
        try {
            Lista lista = listaDAO.buscarPorId(t.getListaId());
            System.out.println("Lista: " + (lista != null ? lista.getNomeLista() + " (ID: " + t.getListaId() + ")" : "ID " + t.getListaId() + " (não encontrada)"));
        } catch (SQLException e) { System.out.println("Lista ID: " + t.getListaId() + " (Erro ao buscar nome)"); }
        if (t.getResponsavelId() != null && t.getResponsavelId() > 0) {
            try {
                Funcionario resp = funcionarioDAO.buscarPorId(t.getResponsavelId());
                System.out.println("Responsável: " + (resp != null ? resp.getNome() + " (ID: " + t.getResponsavelId() + ")" : "ID " + t.getResponsavelId() + " (não encontrado)"));
            } catch (SQLException e) { System.out.println("Responsável ID: " + t.getResponsavelId() + " (Erro ao buscar nome)"); }
        } else { System.out.println("Responsável: Ninguém atribuído"); }
        if (t.getCriadorId() != null && t.getCriadorId() > 0) {
            try {
                Funcionario criador = funcionarioDAO.buscarPorId(t.getCriadorId());
                System.out.println("Criador: " + (criador != null ? criador.getNome() + " (ID: " + t.getCriadorId() + ")" : "ID " + t.getCriadorId() + " (não encontrado)"));
            } catch (SQLException e) { System.out.println("Criador ID: " + t.getCriadorId() + " (Erro ao buscar nome)"); }
        } else { System.out.println("Criador: N/A"); }
        System.out.println("------------------------------------");
    }


    // --- FUNCIONALIDADES ESPECÍFICAS DO ADMIN ---
    private static void atribuirTarefaAdmin() {
        System.out.println("\n--- Atribuir Tarefa a Funcionário (Admin) ---");
        try {
            System.out.println("Listas de Tarefas disponíveis:");
            listarTodasListasAdmin(true); // true para modo resumido
            long listaIdParaTarefas = lerLong("Digite o ID da lista para ver as tarefas: ");

            Lista listaSelecionada = listaDAO.buscarPorId(listaIdParaTarefas);
            if(listaSelecionada == null){ System.out.println("Lista com ID " + listaIdParaTarefas + " não encontrada."); return; }

            List<Tarefa> tarefasDaLista = tarefaDAO.buscarPorListaId(listaIdParaTarefas);
            if (tarefasDaLista.isEmpty()) { System.out.println("Nenhuma tarefa encontrada na lista '" + listaSelecionada.getNomeLista() + "'."); return; }

            System.out.println("\nTarefas na lista '" + listaSelecionada.getNomeLista() + "' (ID: "+listaSelecionada.getListaId()+"):");
            tarefasDaLista.forEach(t -> System.out.println("  ID Tarefa: " + t.getTarefaId() + " - Título: " + t.getTitulo() + " (Responsável Atual ID: " + (t.getResponsavelId() != null ? t.getResponsavelId() : "Nenhum") + ")"));
            System.out.println("------------------------------------");

            long tarefaId = lerLong("ID da Tarefa a ser atribuída: ");
            Tarefa tarefa = null;
            for(Tarefa ttemp : tarefasDaLista){ if(ttemp.getTarefaId() == tarefaId) tarefa = ttemp; } // Pega da lista já carregada

            if (tarefa == null) { tarefa = tarefaDAO.buscarPorId(tarefaId); } // Fallback se não estava na lista impressa

            if (tarefa == null) { System.out.println("Tarefa com ID " + tarefaId + " não encontrada."); return; }

            System.out.println("\nTarefa selecionada para atribuição:");
            exibirTarefaFormatado(tarefa);

            System.out.println("Funcionários disponíveis:");
            listarTodosFuncionariosAdmin(true); // true para modo resumido
            Long funcionarioId = lerLong("ID do Funcionário para atribuir a tarefa (ou 0 para remover atribuição): ");

            if (funcionarioId == 0) {
                tarefa.setResponsavelId(null);
                System.out.println("Atribuição removida da tarefa '" + tarefa.getTitulo() + "'.");
            } else {
                Funcionario f = funcionarioDAO.buscarPorId(funcionarioId);
                if (f == null) { System.out.println("Funcionário com ID " + funcionarioId + " não encontrado. Atribuição cancelada."); return; }
                tarefa.setResponsavelId(funcionarioId);
                System.out.println("Tarefa '" + tarefa.getTitulo() + "' atribuída ao funcionário " + f.getNome() + ".");
            }
            tarefaDAO.atualizar(tarefa);
        } catch (SQLException e) { System.err.println("Erro ao atribuir tarefa: " + e.getMessage()); }
    }

    // --- FUNCIONALIDADES ESPECÍFICAS DO FUNCIONÁRIO ---
    private static void verMinhasTarefasFuncionario() {
        System.out.println("\n--- Minhas Tarefas Atribuídas ---");
        try {
            if (usuarioLogado == null) { System.out.println("Erro: Usuário não está logado."); return; }
            List<Tarefa> minhasTarefas = tarefaDAO.buscarPorResponsavel(usuarioLogado.getFuncionarioId());
            if (minhasTarefas.isEmpty()) { System.out.println("Você não tem tarefas atribuídas no momento."); return; }
            minhasTarefas.forEach(Main::exibirTarefaFormatado);
        } catch (SQLException e) { System.err.println("Erro ao buscar suas tarefas: " + e.getMessage()); }
    }

    private static void atualizarStatusTarefaFuncionario() {
        System.out.println("\n--- Atualizar Status de Minha Tarefa ---");
        try {
            if (usuarioLogado == null) { System.out.println("Erro: Usuário não está logado."); return; }
            List<Tarefa> minhasTarefas = tarefaDAO.buscarPorResponsavel(usuarioLogado.getFuncionarioId());
            if (minhasTarefas.isEmpty()) { System.out.println("Você não tem tarefas atribuídas para atualizar."); return; }

            System.out.println("Suas tarefas atribuídas:");
            for (Tarefa t : minhasTarefas) {
                System.out.println("  ID: " + t.getTarefaId() + " | Título: " + t.getTitulo() + " | Status Atual: " + t.getStatus());
            }
            System.out.println("------------------------------------");

            long tarefaId = lerLong("ID da Tarefa que deseja atualizar o status: ");

            Tarefa tarefaParaAtualizar = null;
            for (Tarefa t : minhasTarefas) { if (t.getTarefaId() == tarefaId) { tarefaParaAtualizar = t; break; } }

            if (tarefaParaAtualizar == null) { System.out.println("Tarefa não encontrada ou não pertence a você."); return; }

            System.out.println("\nDetalhes da Tarefa Selecionada:");
            exibirTarefaFormatado(tarefaParaAtualizar);

            String[] statusPermitidos = {"Pendente", "Em Andamento", "Bloqueada", "Concluída", "Aguardando Avaliação"}; // Exemplo
            System.out.println("Status permitidos: " + String.join(", ", statusPermitidos) + ".");
            String novoStatus = lerString("Digite o novo status para a tarefa: ").trim();

            boolean statusValido = false;
            for(String s : statusPermitidos){ if(s.equalsIgnoreCase(novoStatus)){ novoStatus = s; statusValido = true; break; } }

            if (!statusValido || novoStatus.isEmpty()) { System.out.println("Status inválido ou não fornecido. Operação cancelada."); return; }

            tarefaParaAtualizar.setStatus(novoStatus);
            tarefaDAO.atualizar(tarefaParaAtualizar);
            System.out.println("Status da tarefa '" + tarefaParaAtualizar.getTitulo() + "' atualizado para '" + novoStatus + "'.");
        } catch (SQLException e) { System.err.println("Erro ao atualizar status da tarefa: " + e.getMessage()); }
    }

    // --- MÉTODOS DE GERENCIAMENTO (CRUD) PARA ADMIN ---

    // Funcionários (Admin)
    private static void gerenciarFuncionariosAdmin() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Funcionários (Admin) ---");
            System.out.println("1. Adicionar Funcionário");
            System.out.println("2. Listar Todos os Funcionários");
            System.out.println("3. Buscar Funcionário por ID");
            System.out.println("4. Atualizar Funcionário");
            System.out.println("5. Excluir Funcionário");
            System.out.println("0. Voltar ao Menu Admin");
            System.out.print("Escolha uma opção: ");
            opcao = lerOpcao();
            try {
                switch(opcao) {
                    case 1: adicionarFuncionarioAdmin(); break;
                    case 2: listarTodosFuncionariosAdmin(false); break; // false para não resumido
                    case 3: buscarFuncionarioPorIdAdmin(); break;
                    case 4: atualizarFuncionarioAdmin(); break;
                    case 5: excluirFuncionarioAdmin(); break;
                    case 0: break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (SQLException e) { System.err.println("Erro no gerenciamento de funcionários: " + e.getMessage()); }
        } while (opcao != 0);
    }

    private static void adicionarFuncionarioAdmin() throws SQLException {
        System.out.println("\n-- Adicionar Funcionário (Admin) --");
        String nome = lerString("Nome Completo: ");
        String email = lerString("Email (será o login): ");
        String cargo = lerString("Cargo: ");
        Date dataAdmissao = lerData("Data de Admissão", false);
        boolean ativo = lerBooleano("Funcionário Ativo?", "");
        String tipoUsuario;
        do {
            tipoUsuario = lerString("Tipo de Usuário (funcionario/admin): ").toLowerCase();
        } while (!tipoUsuario.equals("funcionario") && !tipoUsuario.equals("admin"));

        // ⚠️ Lembre-se do HASHING de senhas em produção!
        String senha = lerString("Senha para o usuário: ");
        if (senha.isEmpty()) { System.out.println("Senha não pode ser vazia. Operação cancelada."); return; }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setCargo(cargo);
        funcionario.setDataAdmissao(dataAdmissao);
        funcionario.setAtivo(ativo);
        funcionario.setTipoUsuario(tipoUsuario);
        funcionario.setSenha(senha); // A senha deve ser HASHED antes de chamar inserir

        funcionarioDAO.inserir(funcionario);
        System.out.println("Funcionário '" + funcionario.getNome() + "' adicionado com ID: " + funcionario.getFuncionarioId());
        exibirFuncionarioFormatado(funcionario);
    }

    private static void listarTodosFuncionariosAdmin(boolean resumido) throws SQLException { // Adicionado flag resumido
        System.out.println("\n-- Lista de Funcionários (Admin) --");
        List<Funcionario> funcionarios = funcionarioDAO.buscarTodos();
        if (funcionarios.isEmpty()) { System.out.println("Nenhum funcionário cadastrado."); return; }
        for (Funcionario f : funcionarios) {
            if (resumido) {
                System.out.println("  ID: " + f.getFuncionarioId() + " - Nome: " + f.getNome() + " - Email: " + f.getEmail());
            } else {
                exibirFuncionarioFormatado(f);
            }
        }
        if (!resumido) System.out.println("Total: " + funcionarios.size() + " funcionários.");
    }

    private static void buscarFuncionarioPorIdAdmin() throws SQLException {
        long id = lerLong("ID do Funcionário a buscar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Funcionario f = funcionarioDAO.buscarPorId(id);
        exibirFuncionarioFormatado(f); // Método já lida com f nulo
    }

    private static void atualizarFuncionarioAdmin() throws SQLException {
        long id = lerLong("ID do Funcionário a atualizar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Funcionario f = funcionarioDAO.buscarPorId(id);
        if (f == null) { System.out.println("Funcionário com ID " + id + " não encontrado."); return; }

        System.out.println("Atualizando dados para: ");
        exibirFuncionarioFormatado(f);
        System.out.println("Deixe em branco para não alterar o campo (exceto 'Ativo' e 'Tipo').");

        String nome = lerString("Novo Nome (" + f.getNome() + "): ");
        if (!nome.isEmpty()) f.setNome(nome);

        String email = lerString("Novo Email (" + f.getEmail() + "): ");
        if (!email.isEmpty()) f.setEmail(email);

        String cargo = lerString("Novo Cargo (" + (f.getCargo() != null ? f.getCargo() : "N/A") + "): ");
        if (!cargo.isEmpty()) f.setCargo(cargo);

        Date dataAdmissao = lerData("Nova Data Admissão (" + (f.getDataAdmissao() != null ? dateFormatter.format(f.getDataAdmissao().toLocalDate()) : "N/A") + ")", true);
        if (dataAdmissao != null) f.setDataAdmissao(dataAdmissao);

        f.setAtivo(lerBooleano("Funcionário Ativo?", " (Atual: " + (f.isAtivo() ? "s" : "n") + ")"));

        String tipoAtual = f.getTipoUsuario();
        String tipoUsuario = lerString("Novo Tipo (funcionario/admin) ("+tipoAtual+"): ").toLowerCase();
        if (!tipoUsuario.isEmpty() && (tipoUsuario.equals("funcionario") || tipoUsuario.equals("admin"))) {
            f.setTipoUsuario(tipoUsuario);
        } else if (!tipoUsuario.isEmpty()) {
            System.out.println("Tipo inválido. Mantendo '"+tipoAtual+"'.");
        }

        String novaSenha = lerString("Nova Senha (deixe em branco para não alterar): ");
        if (!novaSenha.isEmpty()) {
            // ⚠️ Aplicar HASHING aqui antes de setar!
            f.setSenha(novaSenha);
            System.out.println("AVISO: Senha será alterada. Lembre-se de usar hashing!");
        }

        funcionarioDAO.atualizar(f);
        System.out.println("Funcionário atualizado com sucesso.");
        exibirFuncionarioFormatado(funcionarioDAO.buscarPorId(id));
    }

    private static void excluirFuncionarioAdmin() throws SQLException {
        long id = lerLong("ID do Funcionário a excluir: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Funcionario f = funcionarioDAO.buscarPorId(id);
        if (f == null) { System.out.println("Funcionário com ID " + id + " não encontrado."); return; }

        if (usuarioLogado != null && usuarioLogado.getFuncionarioId() == id) {
            System.out.println("Você não pode excluir a si mesmo.");
            return;
        }
        System.out.println("Você está prestes a excluir:");
        exibirFuncionarioFormatado(f);
        if (lerBooleano("Tem certeza que deseja excluir este funcionário?", " (s/n)")) {
            funcionarioDAO.excluir(id);
            System.out.println("Funcionário excluído com sucesso.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    // Equipes (Admin)
    private static void gerenciarEquipesAdmin() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Equipes (Admin) ---");
            System.out.println("1. Adicionar Equipe");
            System.out.println("2. Listar Todas as Equipes");
            System.out.println("3. Buscar Equipe por ID");
            System.out.println("4. Atualizar Equipe");
            System.out.println("5. Excluir Equipe");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            opcao = lerOpcao();
            try {
                switch(opcao) {
                    case 1: adicionarEquipeAdmin(); break;
                    case 2: listarTodasEquipesAdmin(false); break;
                    case 3: buscarEquipePorIdAdmin(); break;
                    case 4: atualizarEquipeAdmin(); break;
                    case 5: excluirEquipeAdmin(); break;
                    case 0: break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (SQLException e) { System.err.println("Erro no gerenciamento de equipes: " + e.getMessage()); }
        } while (opcao != 0);
    }

    private static void adicionarEquipeAdmin() throws SQLException {
        System.out.println("\n-- Adicionar Equipe (Admin) --");
        String nome = lerString("Nome da Equipe: ");
        if (nome.isEmpty()) { System.out.println("Nome da equipe não pode ser vazio."); return; }
        String descricao = lerString("Descrição da Equipe: ");
        Equipe equipe = new Equipe(nome, descricao);
        equipeDAO.inserir(equipe);
        System.out.println("Equipe '" + equipe.getNomeEquipe() + "' adicionada com ID: " + equipe.getEquipeId());
        exibirEquipeFormatado(equipe);
    }

    private static void listarTodasEquipesAdmin(boolean resumido) throws SQLException {
        System.out.println("\n-- Lista de Equipes (Admin) --");
        List<Equipe> equipes = equipeDAO.buscarTodas();
        if (equipes.isEmpty()) { System.out.println("Nenhuma equipe cadastrada."); return; }
        for (Equipe eq : equipes) {
            if (resumido) {
                System.out.println("  ID: " + eq.getEquipeId() + " - Nome: " + eq.getNomeEquipe());
            } else {
                exibirEquipeFormatado(eq);
            }
        }
        if (!resumido) System.out.println("Total: " + equipes.size() + " equipes.");
    }

    private static void buscarEquipePorIdAdmin() throws SQLException {
        long id = lerLong("ID da Equipe a buscar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Equipe eq = equipeDAO.buscarPorId(id);
        exibirEquipeFormatado(eq);
    }

    private static void atualizarEquipeAdmin() throws SQLException {
        long id = lerLong("ID da Equipe a atualizar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Equipe eq = equipeDAO.buscarPorId(id);
        if (eq == null) { System.out.println("Equipe com ID " + id + " não encontrada."); return; }

        System.out.println("Atualizando dados para: ");
        exibirEquipeFormatado(eq);
        System.out.println("Deixe em branco para não alterar o campo.");

        String nome = lerString("Novo Nome (" + eq.getNomeEquipe() + "): ");
        if (!nome.isEmpty()) eq.setNomeEquipe(nome);

        String descricao = lerString("Nova Descrição (" + (eq.getDescricaoEquipe()!=null ? eq.getDescricaoEquipe() : "N/A") + "): ");
        if (!descricao.isEmpty()) eq.setDescricaoEquipe(descricao);

        equipeDAO.atualizar(eq);
        System.out.println("Equipe atualizada com sucesso.");
        exibirEquipeFormatado(equipeDAO.buscarPorId(id));
    }

    // Na classe Main.java

    private static void excluirEquipeAdmin() throws SQLException {
        long id = lerLong("ID da Equipe a excluir: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Equipe eq = equipeDAO.buscarPorId(id);
        if (eq == null) { System.out.println("Equipe com ID " + id + " não encontrada."); return; }

        System.out.println("Você está prestes a excluir:");
        exibirEquipeFormatado(eq); // Mostra a equipe que será excluída

        // Contar quantos funcionários estão na equipe para um aviso mais informativo
        List<Funcionario> funcionariosNaEquipe = funcionarioEquipeDAO.listarFuncionariosPorEquipe(id);
        if (!funcionariosNaEquipe.isEmpty()) {
            System.out.println("Atenção: Esta equipe possui " + funcionariosNaEquipe.size() + " funcionário(s) associado(s).");
            System.out.println("Ao excluir a equipe, essas associações serão desfeitas automaticamente.");
        }

        if (lerBooleano("Tem certeza que deseja excluir esta equipe?", " (s/n)")) {
            // A linha "funcionarioEquipeDAO.removerTodasAssociacoesDaEquipe(id);" FOI REMOVIDA.
            // O ON DELETE CASCADE no banco de dados cuidará das associações.
            equipeDAO.excluir(id); // Apenas exclui a equipe
            System.out.println("Equipe '" + eq.getNomeEquipe() + "' e suas associações foram excluídas com sucesso.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    // Associações Funcionário-Equipe (Admin)
    private static void gerenciarAssociacoesFuncionarioEquipeAdmin() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Associações Funcionário-Equipe (Admin) ---");
            System.out.println("1. Adicionar Funcionário a Equipe");
            System.out.println("2. Remover Funcionário de Equipe");
            System.out.println("3. Listar Equipes de um Funcionário");
            System.out.println("4. Listar Funcionários de uma Equipe");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            opcao = lerOpcao();
            try {
                switch(opcao){
                    case 1: adicionarFuncionarioAEquipeAdmin(); break;
                    case 2: removerFuncionarioDeEquipeAdmin(); break;
                    case 3: listarEquipesDeFuncionarioAdmin(); break;
                    case 4: listarFuncionariosDeEquipeAdmin(); break;
                    case 0: break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (SQLException e) { System.err.println("Erro nas associações: " + e.getMessage()); }
        } while(opcao != 0);
    }

    private static void adicionarFuncionarioAEquipeAdmin() throws SQLException {
        System.out.println("\n-- Adicionar Funcionário a Equipe --");
        listarTodosFuncionariosAdmin(true);
        long funcId = lerLong("ID do Funcionário: ");
        if (funcId <=0 || funcionarioDAO.buscarPorId(funcId) == null) { System.out.println("Funcionário inválido ou não encontrado."); return;}

        listarTodasEquipesAdmin(true);
        long equipeId = lerLong("ID da Equipe: ");
        if (equipeId <=0 || equipeDAO.buscarPorId(equipeId) == null) { System.out.println("Equipe inválida ou não encontrada."); return;}

        try {
            funcionarioEquipeDAO.adicionarFuncionarioAEquipe(funcId, equipeId);
            System.out.println("Funcionário ID " + funcId + " adicionado à Equipe ID " + equipeId + ".");
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Código de violação de chave única (já associado)
                System.out.println("Este funcionário já está associado a esta equipe.");
            } else {
                throw e; // Relança outras SQLExceptions
            }
        }
    }
    private static void removerFuncionarioDeEquipeAdmin() throws SQLException {
        System.out.println("\n-- Remover Funcionário de Equipe --");
        listarTodosFuncionariosAdmin(true);
        long funcId = lerLong("ID do Funcionário: ");
        if (funcId <=0) { System.out.println("ID de funcionário inválido."); return;}

        System.out.println("Equipes do funcionário ID " + funcId + ":");
        List<Equipe> equipesDoFunc = funcionarioEquipeDAO.listarEquipesPorFuncionario(funcId);
        if(equipesDoFunc.isEmpty()){ System.out.println("Funcionário não está em nenhuma equipe."); return;}
        equipesDoFunc.forEach(eq -> System.out.println("  ID Equipe: " + eq.getEquipeId() + " - Nome: " + eq.getNomeEquipe()));

        long equipeId = lerLong("ID da Equipe para remover o funcionário: ");
        if (equipeId <=0) { System.out.println("ID de equipe inválido."); return;}

        boolean associado = false;
        for(Equipe eq : equipesDoFunc) { if(eq.getEquipeId() == equipeId) associado = true; }
        if(!associado) { System.out.println("Funcionário não está associado a esta equipe específica."); return; }

        funcionarioEquipeDAO.removerFuncionarioDeEquipe(funcId, equipeId);
        System.out.println("Funcionário ID " + funcId + " removido da Equipe ID " + equipeId + ".");
    }
    private static void listarEquipesDeFuncionarioAdmin() throws SQLException {
        listarTodosFuncionariosAdmin(true);
        long funcId = lerLong("ID do Funcionário para listar equipes: ");
        if (funcId <=0) { System.out.println("ID inválido."); return; }
        Funcionario f = funcionarioDAO.buscarPorId(funcId);
        if (f == null) { System.out.println("Funcionário não encontrado."); return; }
        System.out.println("\n-- Equipes do Funcionário: " + f.getNome() + " (ID: "+f.getFuncionarioId()+") --");
        List<Equipe> equipes = funcionarioEquipeDAO.listarEquipesPorFuncionario(funcId);
        if (equipes.isEmpty()) { System.out.println("Funcionário não está em nenhuma equipe."); }
        else { equipes.forEach(Main::exibirEquipeFormatado); }
    }
    private static void listarFuncionariosDeEquipeAdmin() throws SQLException {
        listarTodasEquipesAdmin(true);
        long equipeId = lerLong("ID da Equipe para listar funcionários: ");
        if (equipeId <=0) { System.out.println("ID inválido."); return; }
        Equipe eq = equipeDAO.buscarPorId(equipeId);
        if (eq == null) { System.out.println("Equipe não encontrada."); return; }
        System.out.println("\n-- Funcionários da Equipe: " + eq.getNomeEquipe() + " (ID: "+eq.getEquipeId()+") --");
        List<Funcionario> funcionarios = funcionarioEquipeDAO.listarFuncionariosPorEquipe(equipeId);
        if (funcionarios.isEmpty()) { System.out.println("Nenhum funcionário nesta equipe."); }
        else { funcionarios.forEach(Main::exibirFuncionarioFormatado); }
    }

    // Listas (Admin)
    private static void gerenciarListasAdmin() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Listas de Tarefas (Admin) ---");
            System.out.println("1. Adicionar Lista");
            System.out.println("2. Listar Todas as Listas");
            System.out.println("3. Buscar Lista por ID");
            System.out.println("4. Atualizar Lista");
            System.out.println("5. Excluir Lista");
            System.out.println("6. Listar Listas por Criador");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            opcao = lerOpcao();
            try {
                switch(opcao){
                    case 1: adicionarListaAdmin(); break;
                    case 2: listarTodasListasAdmin(false); break;
                    case 3: buscarListaPorIdAdmin(); break;
                    case 4: atualizarListaAdmin(); break;
                    case 5: excluirListaAdmin(); break;
                    case 6: listarListasPorCriadorAdmin(); break;
                    case 0: break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (SQLException e) { System.err.println("Erro no gerenciamento de listas: " + e.getMessage()); }
        } while(opcao != 0);
    }
    private static void adicionarListaAdmin() throws SQLException {
        System.out.println("\n-- Adicionar Lista (Admin) --");
        String nome = lerString("Nome da Lista: ");
        if(nome.isEmpty()){ System.out.println("Nome da lista não pode ser vazio."); return;}
        String descricao = lerString("Descrição da Lista: ");

        listarTodosFuncionariosAdmin(true);
        Long criadorId = lerLong("ID do Funcionário Criador (ou 0 se admin logado cria, ou enter para N/A): ");
        if (criadorId == 0 && usuarioLogado != null && "admin".equals(usuarioLogado.getTipoUsuario())) {
            criadorId = usuarioLogado.getFuncionarioId();
        } else if (criadorId == 0){
            criadorId = null;
        } else if (criadorId < 0) { // Se digitou enter e lerLong retornou -1 (ou lógica similar)
            criadorId = null;
        }
        if (criadorId != null && funcionarioDAO.buscarPorId(criadorId) == null) {
            System.out.println("Funcionário criador com ID " + criadorId + " não encontrado. Lista não será associada a um criador.");
            criadorId = null;
        }

        Lista lista = new Lista(nome, descricao, criadorId);
        lista.setDataCriacao(new Timestamp(System.currentTimeMillis())); // DAO pode ter default, mas explícito aqui
        listaDAO.inserir(lista);
        System.out.println("Lista '" + lista.getNomeLista() + "' adicionada com ID: " + lista.getListaId());
        exibirListaFormatado(lista);
    }
    private static void listarTodasListasAdmin(boolean resumido) throws SQLException {
        System.out.println("\n-- Lista de Listas de Tarefas (Admin) --");
        List<Lista> listas = listaDAO.buscarTodas();
        if (listas.isEmpty()) { System.out.println("Nenhuma lista cadastrada."); return; }
        for(Lista l : listas){
            if(resumido) {
                System.out.println("  ID: " + l.getListaId() + " - Nome: " + l.getNomeLista());
            } else {
                exibirListaFormatado(l);
            }
        }
        if(!resumido) System.out.println("Total: " + listas.size() + " listas.");
    }
    private static void buscarListaPorIdAdmin() throws SQLException {
        long id = lerLong("ID da Lista a buscar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Lista l = listaDAO.buscarPorId(id);
        exibirListaFormatado(l);
    }
    private static void atualizarListaAdmin() throws SQLException {
        long id = lerLong("ID da Lista a atualizar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Lista l = listaDAO.buscarPorId(id);
        if (l == null) { System.out.println("Lista com ID " + id + " não encontrada."); return; }

        System.out.println("Atualizando dados para: ");
        exibirListaFormatado(l);
        System.out.println("Deixe em branco para não alterar o campo.");

        String nome = lerString("Novo Nome (" + l.getNomeLista() + "): ");
        if (!nome.isEmpty()) l.setNomeLista(nome);

        String descricao = lerString("Nova Descrição (" + (l.getDescricaoLista()!=null?l.getDescricaoLista():"N/A") + "): ");
        if (!descricao.isEmpty()) l.setDescricaoLista(descricao);

        System.out.println("Criador atual ID: " + (l.getCriadorId() != null ? l.getCriadorId() : "Nenhum"));
        listarTodosFuncionariosAdmin(true);
        Long criadorId = lerLong("Novo ID do Criador (0 para N/A, -1 para manter): ");
        if (criadorId == 0) {
            l.setCriadorId(null);
        } else if (criadorId > 0) {
            if(funcionarioDAO.buscarPorId(criadorId) != null) l.setCriadorId(criadorId);
            else System.out.println("Novo criador ID " + criadorId + " não encontrado. Mantendo o anterior.");
        } // Se -1, não faz nada (mantém o atual)

        listaDAO.atualizar(l);
        System.out.println("Lista atualizada com sucesso.");
        exibirListaFormatado(listaDAO.buscarPorId(id));
    }
    private static void excluirListaAdmin() throws SQLException {
        long id = lerLong("ID da Lista a excluir: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Lista l = listaDAO.buscarPorId(id);
        if (l == null) { System.out.println("Lista com ID " + id + " não encontrada."); return; }

        System.out.println("Você está prestes a excluir:");
        exibirListaFormatado(l);
        if (lerBooleano("Tem certeza? (Isso excluirá TODAS as tarefas associadas!)", " (s/n)")) {
            // Tarefas são excluídas por ON DELETE CASCADE na FK lista_id em Tarefa
            listaDAO.excluir(id);
            System.out.println("Lista e suas tarefas associadas foram excluídas.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
    private static void listarListasPorCriadorAdmin() throws SQLException {
        listarTodosFuncionariosAdmin(true);
        long criadorId = lerLong("ID do Funcionário Criador para listar as listas: ");
        if (criadorId <=0) { System.out.println("ID inválido."); return; }
        Funcionario f = funcionarioDAO.buscarPorId(criadorId);
        if (f == null) { System.out.println("Funcionário criador não encontrado."); return; }
        System.out.println("\n-- Listas criadas por: " + f.getNome() + " (ID: "+f.getFuncionarioId()+") --");
        List<Lista> listas = listaDAO.buscarPorCriadorId(criadorId);
        if (listas.isEmpty()) { System.out.println("Nenhuma lista encontrada para este criador."); }
        else { listas.forEach(Main::exibirListaFormatado); }
    }

    // Tarefas (Admin - CRUD completo)
    private static void gerenciarTarefasAdmin() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Tarefas (Admin) ---");
            System.out.println("1. Adicionar Tarefa");
            System.out.println("2. Listar Tarefas de uma Lista");
            System.out.println("3. Buscar Tarefa por ID");
            System.out.println("4. Atualizar Tarefa");
            System.out.println("5. Excluir Tarefa");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            opcao = lerOpcao();
            try {
                switch(opcao){
                    case 1: adicionarTarefaAdmin(); break;
                    case 2: listarTarefasDeListaAdmin(); break;
                    case 3: buscarTarefaPorIdAdmin(); break;
                    case 4: atualizarTarefaAdmin(); break;
                    case 5: excluirTarefaAdmin(); break;
                    case 0: break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (SQLException e) { System.err.println("Erro no gerenciamento de tarefas: " + e.getMessage()); }
        } while(opcao != 0);
    }
    private static void adicionarTarefaAdmin() throws SQLException {
        System.out.println("\n-- Adicionar Tarefa (Admin) --");
        String titulo = lerString("Título da Tarefa: ");
        if (titulo.isEmpty()) { System.out.println("Título não pode ser vazio."); return; }
        String descricao = lerString("Descrição: ");
        Date dataVencimento = lerData("Data de Vencimento", true);

        String[] prioridades = {"Baixa", "Média", "Alta", "Urgente"};
        System.out.println("Prioridades disponíveis: " + String.join(", ", prioridades));
        String prioridade = lerString("Prioridade (Padrão: Média): ");
        if (prioridade.isEmpty()) prioridade = "Média";
        else {
            boolean pValida = false;
            for(String p : prioridades) if(p.equalsIgnoreCase(prioridade)) {prioridade = p; pValida = true; break;}
            if(!pValida) { System.out.println("Prioridade inválida. Usando 'Média'."); prioridade = "Média";}
        }

        String[] statusPermitidos = {"Pendente", "Em Andamento", "Bloqueada", "Concluída", "Aguardando Avaliação", "Cancelada"};
        System.out.println("Status disponíveis: " + String.join(", ", statusPermitidos));
        String status = lerString("Status (Padrão: Pendente): ");
        if (status.isEmpty()) status = "Pendente";
        else {
            boolean sValido = false;
            for(String s : statusPermitidos) if(s.equalsIgnoreCase(status)) {status = s; sValido = true; break;}
            if(!sValido) { System.out.println("Status inválido. Usando 'Pendente'."); status = "Pendente";}
        }

        System.out.println("Listas disponíveis:");
        listarTodasListasAdmin(true);
        long listaId = lerLong("ID da Lista à qual a tarefa pertence: ");
        if (listaId <=0 || listaDAO.buscarPorId(listaId) == null) { System.out.println("Lista inválida ou não encontrada. Operação cancelada."); return;}

        System.out.println("Funcionários disponíveis para atribuição/criação:");
        listarTodosFuncionariosAdmin(true);
        Long responsavelId = lerLong("ID do Funcionário Responsável (ou 0 se nenhum): ");
        if (responsavelId == 0) responsavelId = null;
        else if (responsavelId > 0 && funcionarioDAO.buscarPorId(responsavelId) == null) {
            System.out.println("Funcionário responsável ID " + responsavelId + " não encontrado. Tarefa será não atribuída.");
            responsavelId = null;
        }

        Long criadorId = lerLong("ID do Funcionário Criador (ou 0 se admin logado): ");
        if (criadorId == 0 && usuarioLogado != null) {
            criadorId = usuarioLogado.getFuncionarioId();
        } else if (criadorId > 0 && funcionarioDAO.buscarPorId(criadorId) == null) {
            System.out.println("Funcionário criador ID " + criadorId + " não encontrado. Criador será o admin logado.");
            criadorId = (usuarioLogado != null ? usuarioLogado.getFuncionarioId() : null);
        } else if (criadorId <= 0) {
            criadorId = (usuarioLogado != null ? usuarioLogado.getFuncionarioId() : null);
        }

        Tarefa tarefa = new Tarefa(titulo, descricao, dataVencimento, prioridade, status, listaId, responsavelId, criadorId);
        tarefa.setDataCriacao(new Timestamp(System.currentTimeMillis())); // DAO pode ter default
        tarefaDAO.inserir(tarefa);
        System.out.println("Tarefa '" + tarefa.getTitulo() + "' adicionada com ID: " + tarefa.getTarefaId());
        exibirTarefaFormatado(tarefa);
    }
    private static void listarTarefasDeListaAdmin() throws SQLException {
        System.out.println("Listas disponíveis:");
        listarTodasListasAdmin(true);
        long listaId = lerLong("ID da Lista para ver as tarefas: ");
        if (listaId <=0) { System.out.println("ID de lista inválido."); return; }
        Lista l = listaDAO.buscarPorId(listaId);
        if (l == null) { System.out.println("Lista com ID "+listaId+" não encontrada."); return; }
        System.out.println("\n-- Tarefas da Lista: " + l.getNomeLista() + " (ID: " + l.getListaId() + ") --");
        List<Tarefa> tarefas = tarefaDAO.buscarPorListaId(listaId);
        if (tarefas.isEmpty()) { System.out.println("Nenhuma tarefa nesta lista."); }
        else { tarefas.forEach(Main::exibirTarefaFormatado); }
    }
    private static void buscarTarefaPorIdAdmin() throws SQLException {
        long id = lerLong("ID da Tarefa a buscar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Tarefa t = tarefaDAO.buscarPorId(id);
        exibirTarefaFormatado(t);
    }
    private static void atualizarTarefaAdmin() throws SQLException {
        long id = lerLong("ID da Tarefa a atualizar: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Tarefa t = tarefaDAO.buscarPorId(id);
        if (t == null) { System.out.println("Tarefa com ID " + id + " não encontrada."); return; }

        System.out.println("Atualizando dados para: ");
        exibirTarefaFormatado(t);
        System.out.println("Deixe em branco para não alterar o campo (exceto IDs relacionados).");

        String titulo = lerString("Novo Título (" + t.getTitulo() + "): ");
        if (!titulo.isEmpty()) t.setTitulo(titulo);

        String descricao = lerString("Nova Descrição: ");
        if (!descricao.isEmpty()) t.setDescricao(descricao);

        Date dataVencimento = lerData("Nova Data Vencimento ("+(t.getDataVencimento()!=null?dateFormatter.format(t.getDataVencimento().toLocalDate()):"N/A")+")", true);
        if(dataVencimento != null || lerString("Manter data de vencimento atual? (Deixe em branco para sim, 'n' para limpar): ").equalsIgnoreCase("n")) {
            t.setDataVencimento(dataVencimento); // Se dataVencimento for null aqui, limpa.
        }


        String prioridade = lerString("Nova Prioridade (" + t.getPrioridade() + "): ");
        if (!prioridade.isEmpty()) t.setPrioridade(prioridade); // Adicionar validação

        String status = lerString("Novo Status (" + t.getStatus() + "): ");
        if (!status.isEmpty()) t.setStatus(status); // Adicionar validação

        System.out.println("Lista atual ID: " + t.getListaId());
        listarTodasListasAdmin(true);
        long listaId = lerLong("Novo ID da Lista (0 para manter): ");
        if (listaId > 0) {
            if (listaDAO.buscarPorId(listaId) != null) t.setListaId(listaId);
            else System.out.println("Nova lista ID "+listaId+" não encontrada. Mantendo a atual.");
        }

        System.out.println("Responsável atual ID: " + (t.getResponsavelId() != null ? t.getResponsavelId() : "Nenhum"));
        listarTodosFuncionariosAdmin(true);
        Long responsavelId = lerLong("Novo ID do Responsável (0 para N/A, -1 para manter): ");
        if (responsavelId == 0) {
            t.setResponsavelId(null);
        } else if (responsavelId > 0) {
            if(funcionarioDAO.buscarPorId(responsavelId) != null) t.setResponsavelId(responsavelId);
            else System.out.println("Novo responsável ID "+responsavelId+" não encontrado. Mantendo o atual.");
        } // Se -1, não faz nada.

        // Criador geralmente não muda, mas poderia ser adicionado.

        tarefaDAO.atualizar(t);
        System.out.println("Tarefa atualizada com sucesso.");
        exibirTarefaFormatado(tarefaDAO.buscarPorId(id));
    }
    private static void excluirTarefaAdmin() throws SQLException {
        long id = lerLong("ID da Tarefa a excluir: ");
        if (id <=0) { System.out.println("ID inválido."); return; }
        Tarefa t = tarefaDAO.buscarPorId(id);
        if (t == null) { System.out.println("Tarefa com ID " + id + " não encontrada."); return; }

        System.out.println("Você está prestes a excluir:");
        exibirTarefaFormatado(t);
        if (lerBooleano("Tem certeza?", " (s/n)")) {
            tarefaDAO.excluir(id);
            System.out.println("Tarefa excluída com sucesso.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}