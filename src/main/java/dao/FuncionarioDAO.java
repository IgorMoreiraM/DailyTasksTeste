package dao;

import model.Funcionario;
import org.example.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void inserir(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO Funcionario (nome, email, cargo, data_admissao, ativo, tipo_usuario, senha) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getEmail());
            pstmt.setString(3, funcionario.getCargo());
            pstmt.setDate(4, funcionario.getDataAdmissao());
            pstmt.setBoolean(5, funcionario.isAtivo());
            pstmt.setString(6, funcionario.getTipoUsuario());
            pstmt.setString(7, funcionario.getSenha()); // ARMAZENAR HASH DA SENHA AQUI
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                funcionario.setFuncionarioId(generatedKeys.getLong(1));
            }
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    private Funcionario extrairFuncionarioDoResultSet(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setFuncionarioId(rs.getLong("funcionario_id"));
        funcionario.setNome(rs.getString("nome"));
        funcionario.setEmail(rs.getString("email"));
        funcionario.setCargo(rs.getString("cargo"));
        funcionario.setDataAdmissao(rs.getDate("data_admissao"));
        funcionario.setAtivo(rs.getBoolean("ativo"));
        funcionario.setTipoUsuario(rs.getString("tipo_usuario"));
        funcionario.setSenha(rs.getString("senha")); // Carrega a senha (ou hash)
        return funcionario;
    }

    public Funcionario buscarPorId(long id) throws SQLException {
        String sql = "SELECT * FROM Funcionario WHERE funcionario_id = ?";
        Funcionario funcionario = null;
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                funcionario = extrairFuncionarioDoResultSet(rs);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return funcionario;
    }

    public Funcionario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Funcionario WHERE email = ?";
        Funcionario funcionario = null;
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                funcionario = extrairFuncionarioDoResultSet(rs);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return funcionario;
    }

    public List<Funcionario> buscarTodos() throws SQLException {
        String sql = "SELECT * FROM Funcionario ORDER BY nome";
        List<Funcionario> funcionarios = new ArrayList<>();
        Connection conexao = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            stmt = conexao.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                funcionarios.add(extrairFuncionarioDoResultSet(rs));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return funcionarios;
    }

    public void atualizar(Funcionario funcionario) throws SQLException {
        // ATENÇÃO: Se a senha for atualizada, ela também deve ser HASHED antes de salvar.
        String sql = "UPDATE Funcionario SET nome = ?, email = ?, cargo = ?, data_admissao = ?, ativo = ?, tipo_usuario = ?, senha = ? WHERE funcionario_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getEmail());
            pstmt.setString(3, funcionario.getCargo());
            pstmt.setDate(4, funcionario.getDataAdmissao());
            pstmt.setBoolean(5, funcionario.isAtivo());
            pstmt.setString(6, funcionario.getTipoUsuario());
            pstmt.setString(7, funcionario.getSenha()); // ATUALIZAR COM HASH DA NOVA SENHA
            pstmt.setLong(8, funcionario.getFuncionarioId());
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public void excluir(long id) throws SQLException {
        String sql = "DELETE FROM Funcionario WHERE funcionario_id = ?";
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