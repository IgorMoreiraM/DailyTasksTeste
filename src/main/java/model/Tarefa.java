package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Tarefa {
    private long tarefaId;
    private String titulo;
    private String descricao;
    private Timestamp dataCriacao;
    private Date dataVencimento;
    private String prioridade; // Idealmente um Enum
    private String status;     // Idealmente um Enum
    private long listaId;
    private Long responsavelId; // Pode ser nulo
    private Long criadorId;     // Pode ser nulo se o criador for excluído

    public Tarefa() {
    }

    public Tarefa(String titulo, String descricao, Date dataVencimento, String prioridade, String status, long listaId, Long responsavelId, Long criadorId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
        this.prioridade = prioridade;
        this.status = status;
        this.listaId = listaId;
        this.responsavelId = responsavelId;
        this.criadorId = criadorId;
    }

    // Getters e Setters
    public long getTarefaId() {
        return tarefaId;
    }

    public void setTarefaId(long tarefaId) {
        this.tarefaId = tarefaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        // Aqui você poderia validar se a string é uma das prioridades permitidas
        // ou converter de/para Enum se estiver usando Enums.
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        // Validação ou conversão de/para Enum
        this.status = status;
    }

    public long getListaId() {
        return listaId;
    }

    public void setListaId(long listaId) {
        this.listaId = listaId;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public Long getCriadorId() {
        return criadorId;
    }

    public void setCriadorId(Long criadorId) {
        this.criadorId = criadorId;
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "tarefaId=" + tarefaId +
                ", titulo='" + titulo + '\'' +
                ", status='" + status + '\'' +
                ", listaId=" + listaId +
                ", responsavelId=" + responsavelId +
                '}';
    }
}
