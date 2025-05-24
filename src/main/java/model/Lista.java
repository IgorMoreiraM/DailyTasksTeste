package model;

import java.sql.Timestamp;

public class Lista {
    private long listaId;
    private String nomeLista;
    private String descricaoLista;
    private Timestamp dataCriacao;
    private Long criadorId;

    public Lista() {
    }

    public Lista(String nomeLista, String descricaoLista, Long criadorId) {
        this.nomeLista = nomeLista;
        this.descricaoLista = descricaoLista;
        this.criadorId = criadorId;
    }

    // Getters e Setters
    public long getListaId() {
        return listaId;
    }

    public void setListaId(long listaId) {
        this.listaId = listaId;
    }

    public String getNomeLista() {
        return nomeLista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public String getDescricaoLista() {
        return descricaoLista;
    }

    public void setDescricaoLista(String descricaoLista) {
        this.descricaoLista = descricaoLista;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getCriadorId() {
        return criadorId;
    }

    public void setCriadorId(Long criadorId) {
        this.criadorId = criadorId;
    }

    @Override
    public String toString() {
        return "Lista{" +
                "listaId=" + listaId +
                ", nomeLista='" + nomeLista + '\'' +
                ", descricaoLista='" + descricaoLista + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", criadorId=" + criadorId +
                '}';
    }
}
