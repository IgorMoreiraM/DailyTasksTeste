package dao;

import model.Tarefa;
import org.example.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    // Método auxiliar para construir o objeto Tarefa a partir do ResultSet
    private Tarefa extrairTarefaDoResultSet(ResultSet rs) throws SQLException {
        Tarefa tarefa = new Tarefa();
        tarefa.setTarefaId(rs.getLong("tarefa_id"));
        tarefa.setTitulo(rs.getString("titulo"));
        tarefa.setDescricao(rs.getString("descricao"));
        tarefa.setDataCriacao(rs.getTimestamp("data_criacao"));
        tarefa.setDataVencimento(rs.getDate("data_vencimento"));
        tarefa.setPrioridade(rs.getString("prioridade"));
        tarefa.setStatus(rs.getString("status"));

        // lista_id é INT NOT NULL no banco e long (primitivo) no Java.
        // rs.getLong() lida bem com a conversão de INT para long.
        tarefa.setListaId(rs.getLong("lista_id"));

        // CORREÇÃO PARA responsavel_id (INT no banco, Long no Java)
        long tempResponsavelId = rs.getLong("responsavel_id");
        if (rs.wasNull()) { // Verifica se o valor lido era SQL NULL
            tarefa.setResponsavelId(null);
        } else {
            tarefa.setResponsavelId(tempResponsavelId); // Autoboxing de long para Long
        }

        // CORREÇÃO PARA criador_id (INT no banco, Long no Java)
        long tempCriadorId = rs.getLong("criador_id");
        if (rs.wasNull()) { // Verifica se o valor lido era SQL NULL
            tarefa.setCriadorId(null);
        } else {
            tarefa.setCriadorId(tempCriadorId); // Autoboxing de long para Long
        }

        return tarefa;
    }

    public void inserir(Tarefa tarefa) throws SQLException {
        String sql = "INSERT INTO Tarefa (titulo, descricao, data_criacao, data_vencimento, prioridade, status, lista_id, responsavel_id, criador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, tarefa.getTitulo());
            pstmt.setString(2, tarefa.getDescricao());

            if (tarefa.getDataCriacao() != null) {
                pstmt.setTimestamp(3, tarefa.getDataCriacao());
            } else {
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }

            pstmt.setDate(4, tarefa.getDataVencimento());
            pstmt.setString(5, tarefa.getPrioridade());
            pstmt.setString(6, tarefa.getStatus());
            pstmt.setLong(7, tarefa.getListaId()); // lista_id é long no objeto Tarefa

            if (tarefa.getResponsavelId() != null) {
                pstmt.setLong(8, tarefa.getResponsavelId());
            } else {
                pstmt.setNull(8, Types.INTEGER); // responsavel_id é INT no banco
            }
            if (tarefa.getCriadorId() != null) {
                pstmt.setLong(9, tarefa.getCriadorId());
            } else {
                pstmt.setNull(9, Types.INTEGER); // criador_id é INT no banco
            }
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                tarefa.setTarefaId(generatedKeys.getLong(1));
            }
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public Tarefa buscarPorId(long id) throws SQLException {
        String sql = "SELECT * FROM Tarefa WHERE tarefa_id = ?";
        Tarefa tarefa = null;
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tarefa = extrairTarefaDoResultSet(rs); // Usa o método auxiliar
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return tarefa;
    }

    public List<Tarefa> buscarPorListaId(long listaId) throws SQLException {
        String sql = "SELECT * FROM Tarefa WHERE lista_id = ? ORDER BY data_vencimento NULLS LAST, prioridade";
        List<Tarefa> tarefas = new ArrayList<>();
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, listaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tarefas.add(extrairTarefaDoResultSet(rs)); // Usa o método auxiliar
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return tarefas;
    }

    public List<Tarefa> buscarPorResponsavel(long responsavelId) throws SQLException {
        String sql = "SELECT * FROM Tarefa WHERE responsavel_id = ? ORDER BY data_vencimento NULLS LAST, prioridade";
        List<Tarefa> tarefas = new ArrayList<>();
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, responsavelId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tarefas.add(extrairTarefaDoResultSet(rs)); // Usa o método auxiliar
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return tarefas;
    }


    public void atualizar(Tarefa tarefa) throws SQLException {
        String sql = "UPDATE Tarefa SET titulo = ?, descricao = ?, data_criacao = ?, data_vencimento = ?, prioridade = ?, " +
                "status = ?, lista_id = ?, responsavel_id = ?, criador_id = ? WHERE tarefa_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setString(1, tarefa.getTitulo());
            pstmt.setString(2, tarefa.getDescricao());
            pstmt.setTimestamp(3, tarefa.getDataCriacao());
            pstmt.setDate(4, tarefa.getDataVencimento());
            pstmt.setString(5, tarefa.getPrioridade());
            pstmt.setString(6, tarefa.getStatus());
            pstmt.setLong(7, tarefa.getListaId());
            if (tarefa.getResponsavelId() != null) {
                pstmt.setLong(8, tarefa.getResponsavelId());
            } else {
                pstmt.setNull(8, Types.INTEGER); // responsavel_id é INT
            }
            if (tarefa.getCriadorId() != null) {
                pstmt.setLong(9, tarefa.getCriadorId());
            } else {
                pstmt.setNull(9, Types.INTEGER); // criador_id é INT
            }
            pstmt.setLong(10, tarefa.getTarefaId());
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public void excluir(long id) throws SQLException {
        String sql = "DELETE FROM Tarefa WHERE tarefa_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }
}