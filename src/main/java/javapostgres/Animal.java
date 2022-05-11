package javapostgres;

public class Animal {
    private int id; // valor de la clau primària a ANIMALS
    private String nom;
    private String categoria;

    public Animal(String nom, String categoria) {
        this(-1, nom, categoria); // encara no té identificador
    }

    public Animal(int id, String nom, String categoria) {
        this.id = id;
        this.nom = nom;
        this.categoria = categoria;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public String getCategoria() {
        return categoria;
    }

    public String toString() {
        return "(" + id + ", '" + nom + "', '" + categoria + "')";
    }
}