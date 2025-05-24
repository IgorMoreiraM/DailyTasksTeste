package dao;

import model.Lista;
import org.example.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListaDAO {

    // Método auxiliar para construir o objeto Lista a partir do ResultSet
    private Lista extrairListaDoResultSet(ResultSet rs) throws SQLException {
        Lista lista = new Lista();
        lista.setListaId(rs.getLong("lista_id"));
        lista.setNomeLista(rs.getString("nome_lista"));
        lista.setDescricaoLista(rs.getString("descricao_lista"));
        lista.setDataCriacao(rs.getTimestamp("data_criacao"));

        // CORREÇÃO APLICADA AQUI:
        long tempCriadorId = rs.getLong("criador_id"); // Lê o valor como primitivo long
        if (rs.wasNull()) { // Verifica se o último valor lido (criador_id) era SQL NULL
            lista.setCriadorId(null); // Define como null no objeto Java
        } else {
            lista.setCriadorId(tempCriadorId); // Autoboxing de long para Long
        }
        return lista;
    }

    public void inserir(Lista lista) throws SQLException {
        String sql = "INSERT INTO Lista (nome_lista, descricao_lista, criador_id, data_criacao) VALUES (?, ?, ?, ?)";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, lista.getNomeLista());
            pstmt.setString(2, lista.getDescricaoLista());
            if (lista.getCriadorId() != null) {
                pstmt.setLong(3, lista.getCriadorId());
            } else {
                pstmt.setNull(3, Types.INTEGER); // criador_id é INT no banco
            }

            if (lista.getDataCriacao() != null) {
                pstmt.setTimestamp(4, lista.getDataCriacao());
            } else {
                // Se o banco tiver DEFAULT CURRENT_TIMESTAMP e você quiser usá-lo,
                // pode-se ajustar o SQL ou não setar este parâmetro.
                // Para consistência, vamos setar um valor se não houver.
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }

            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                lista.setListaId(generatedKeys.getLong(1));
            }
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public Lista buscarPorId(long id) throws SQLException {
        String sql = "SELECT * FROM Lista WHERE lista_id = ?";
        Lista lista = null;
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                lista = extrairListaDoResultSet(rs); // Usa o método auxiliar
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return lista;
    }

    public List<Lista> buscarTodas() throws SQLException {
        String sql = "SELECT * FROM Lista ORDER BY nome_lista";
        List<Lista> listas = new ArrayList<>();
        Connection conexao = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            stmt = conexao.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                listas.add(extrairListaDoResultSet(rs)); // Usa o método auxiliar
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return listas;
    }

    public List<Lista> buscarPorCriadorId(long criadorId) throws SQLException {
        String sql = "SELECT * FROM Lista WHERE criador_id = ? ORDER BY nome_lista";
        List<Lista> listas = new ArrayList<>();
        Connection conexao = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, criadorId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                listas.add(extrairListaDoResultSet(rs)); // Usa o método auxiliar
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
        return listas;
    }

    public void atualizar(Lista lista) throws SQLException {
        String sql = "UPDATE Lista SET nome_lista = ?, descricao_lista = ?, criador_id = ?, data_criacao = ? WHERE lista_id = ?";
        Connection conexao = null;
        PreparedStatement pstmt = null;
        try {
            conexao = DatabaseConnector.conectar();
            pstmt = conexao.prepareStatement(sql);
            pstmt.setString(1, lista.getNomeLista());
            pstmt.setString(2, lista.getDescricaoLista());
            if (lista.getCriadorId() != null) {
                pstmt.setLong(3, lista.getCriadorId());
            } else {
                pstmt.setNull(3, Types.INTEGER); // criador_id é INT
            }
            pstmt.setTimestamp(4, lista.getDataCriacao()); // Assumindo que data_criacao pode ser atualizada
            pstmt.setLong(5, lista.getListaId());
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.desconectar(conexao);
        }
    }

    public void excluir(long id) throws SQLException {
        String sql = "DELETE FROM Lista WHERE lista_id = ?";
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