package dao;

import model.Equipe;
import model.Funcionario;
import org.example.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioEquipeDAO {

    public void adicionarFuncionarioAEquipe(long funcionarioId, long equipeId) throws SQLException {
        String sql = "INSERT INTO Funcionario_Equipe (funcionario_id, equipe_id) VALUES (?, ?)";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, funcionarioId);
            pstmt.setLong(2, equipeId);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public void removerFuncionarioDeEquipe(long funcionarioId, long equipeId) throws SQLException {
        String sql = "DELETE FROM Funcionario_Equipe WHERE funcionario_id = ? AND equipe_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, funcionarioId);
            pstmt.setLong(2, equipeId);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public List<Equipe> listarEquipesPorFuncionario(long funcionarioId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        // SQL para buscar equipes associadas a um funcionário
        // (SELECT e.* FROM Equipe e JOIN Funcionario_Equipe fe ON e.equipe_id = fe.equipe_id WHERE fe.funcionario_id = ?)
        String sql = "SELECT e.equipe_id, e.nome_equipe, e.descricao_equipe " +
                "FROM Equipe e " +
                "JOIN Funcionario_Equipe fe ON e.equipe_id = fe.equipe_id " +
                "WHERE fe.funcionario_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, funcionarioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setEquipeId(rs.getLong("equipe_id"));
                equipe.setNomeEquipe(rs.getString("nome_equipe"));
                equipe.setDescricaoEquipe(rs.getString("descricao_equipe"));
                equipes.add(equipe);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return equipes;
    }

    public List<Funcionario> listarFuncionariosPorEquipe(long equipeId) throws SQLException {
        List<Funcionario> funcionarios = new ArrayList<>();
        // SQL para buscar funcionários associados a uma equipe
        // (SELECT f.* FROM Funcionario f JOIN Funcionario_Equipe fe ON f.funcionario_id = fe.funcionario_id WHERE fe.equipe_id = ?)
        String sql = "SELECT f.funcionario_id, f.nome, f.email, f.cargo, f.data_admissao, f.ativo " +
                "FROM Funcionario f " +
                "JOIN Funcionario_Equipe fe ON f.funcionario_id = fe.funcionario_id " +
                "WHERE fe.equipe_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, equipeId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Funcionario funcionario = new Funcionario();
                funcionario.setFuncionarioId(rs.getLong("funcionario_id"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setEmail(rs.getString("email"));
                funcionario.setCargo(rs.getString("cargo"));
                funcionario.setDataAdmissao(rs.getDate("data_admissao"));
                funcionario.setAtivo(rs.getBoolean("ativo"));
                funcionarios.add(funcionario);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return funcionarios;
    }
}