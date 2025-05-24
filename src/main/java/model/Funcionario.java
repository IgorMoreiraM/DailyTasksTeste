package model;

import java.sql.Date;

public class Funcionario {
    private long funcionarioId;
    private String nome;
    private String email; // Usado como login para funcionários
    private String cargo;
    private Date dataAdmissao;
    private boolean ativo;
    private String tipoUsuario; // "funcionario" ou "admin"
    private String senha;

    public Funcionario() {
    }

    // Construtor com campos (sem ID, tipoUsuario e senha para inserção inicial)
    public Funcionario(String nome, String email, String cargo, Date dataAdmissao, boolean ativo) {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.ativo = ativo;
        this.tipoUsuario = "funcionario"; // Padrão
    }

    // Construtor completo para DAO
    public Funcionario(long funcionarioId, String nome, String email, String cargo, Date dataAdmissao, boolean ativo, String tipoUsuario, String senha) {
        this.funcionarioId = funcionarioId;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.ativo = ativo;
        this.tipoUsuario = tipoUsuario;
        this.senha = senha;
    }


    // Getters e Setters
    public long getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(long funcionarioId) { this.funcionarioId = funcionarioId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public Date getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(Date dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; } // Lembre-se do HASHING!

    @Override
    public String toString() {
        return "Funcionario{" +
                "funcionarioId=" + funcionarioId +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cargo='" + cargo + '\'' +
                ", dataAdmissao=" + dataAdmissao +
                ", ativo=" + ativo +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}
