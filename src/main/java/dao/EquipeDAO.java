package dao;

import model.Equipe;
import org.example.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    public void inserir(Equipe equipe) throws SQLException {
        String sql = "INSERT INTO Equipe (nome_equipe, descricao_equipe) VALUES (?, ?)";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, equipe.getNomeEquipe());
            pstmt.setString(2, equipe.getDescricaoEquipe());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                equipe.setEquipeId(generatedKeys.getLong(1));
            }
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public Equipe buscarPorId(long id) throws SQLException {
        String sql = "SELECT * FROM Equipe WHERE equipe_id = ?";
        Equipe equipe = null;
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                equipe = new Equipe();
                equipe.setEquipeId(rs.getLong("equipe_id"));
                equipe.setNomeEquipe(rs.getString("nome_equipe"));
                equipe.setDescricaoEquipe(rs.getString("descricao_equipe"));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return equipe;
    }

    public List<Equipe> buscarTodas() throws SQLException {
        String sql = "SELECT * FROM Equipe ORDER BY nome_equipe";
        List<Equipe> equipes = new ArrayList<>();
        Connection conexao = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            stmt = conexao.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setEquipeId(rs.getLong("equipe_id"));
                equipe.setNomeEquipe(rs.getString("nome_equipe"));
                equipe.setDescricaoEquipe(rs.getString("descricao_equipe"));
                equipes.add(equipe);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return equipes;
    }

    public void atualizar(Equipe equipe) throws SQLException {
        String sql = "UPDATE Equipe SET nome_equipe = ?, descricao_equipe = ? WHERE equipe_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setString(1, equipe.getNomeEquipe());
            pstmt.setString(2, equipe.getDescricaoEquipe());
            pstmt.setLong(3, equipe.getEquipeId());
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public void excluir(long id) throws SQLException {
        String sql = "DELETE FROM Equipe WHERE equipe_id = ?";
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
