/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.core.modelo;
import java.util.Objects;
/**
 *
 * @author carlospetit
 */
public abstract class ItemPrueba {
    protected int id;
    protected String enunciado;
    protected NivelBloom nivelBloom;
    protected int tiempoEstimado; // en segundos
    protected int anioUso;
    protected Object solucionEsperada;
    protected Object respuestaUsuario;

    public ItemPrueba(int id, String enunciado, NivelBloom nivelBloom, int tiempoEstimado, int anioUso, Object solucionEsperada) {
        this.id = id;
        this.enunciado = Objects.requireNonNull(enunciado, "El enunciado no puede ser nulo");
        this.nivelBloom = Objects.requireNonNull(nivelBloom, "El nivel de Bloom no puede ser nulo");
        this.tiempoEstimado = tiempoEstimado;
        this.anioUso = anioUso;
        this.solucionEsperada = solucionEsperada;
    }

    public int getId() {
        return id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public NivelBloom getNivelBloom() {
        return nivelBloom;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public int getAnioUso() {
        return anioUso;
    }

    public Object getSolucionEsperada() {
        return solucionEsperada;
    }

    public Object getRespuestaUsuario() {
        return respuestaUsuario;
    }

    public void setRespuestaUsuario(Object respuestaUsuario) {
        this.respuestaUsuario = respuestaUsuario;
    }

    public abstract boolean esRespuestaCorrecta();
    public abstract String getTipoItem();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPrueba that = (ItemPrueba) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Enunciado: '" + enunciado + '\'' + ", Nivel: " + nivelBloom;
    }
}