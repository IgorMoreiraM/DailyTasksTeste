package model;

public class Equipe {
    private long equipeId;
    private String nomeEquipe;
    private String descricaoEquipe;

    public Equipe() {
    }

    public Equipe(String nomeEquipe, String descricaoEquipe) {
        this.nomeEquipe = nomeEquipe;
        this.descricaoEquipe = descricaoEquipe;
    }

    // Getters e Setters
    public long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(long equipeId) {
        this.equipeId = equipeId;
    }

    public String getNomeEquipe() {
        return nomeEquipe;
    }

    public void setNomeEquipe(String nomeEquipe) {
        this.nomeEquipe = nomeEquipe;
    }

    public String getDescricaoEquipe() {
        return descricaoEquipe;
    }

    public void setDescricaoEquipe(String descricaoEquipe) {
        this.descricaoEquipe = descricaoEquipe;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "equipeId=" + equipeId +
                ", nomeEquipe='" + nomeEquipe + '\'' +
                ", descricaoEquipe='" + descricaoEquipe + '\'' +
                '}';
    }
}