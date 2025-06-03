/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.core.modelo;
import java.util.List;
import java.util.Objects;
/**
 *
 * @author carlospetit
 */


public class ItemOpcionMultiple extends ItemPrueba {
    private List<String> opciones;

    public ItemOpcionMultiple(int id, String enunciado, NivelBloom nivelBloom, int tiempoEstimado, int anioUso, List<String> opciones, int indiceOpcionCorrecta) {
        super(id, enunciado, nivelBloom, tiempoEstimado, anioUso, indiceOpcionCorrecta);
        this.opciones = Objects.requireNonNull(opciones, "La lista de opciones no puede ser nula");
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public int getIndiceOpcionCorrecta() {
        return (Integer) solucionEsperada;
    }

    @Override
    public boolean esRespuestaCorrecta() {
        if (respuestaUsuario == null || !(respuestaUsuario instanceof Integer)) {
            return false;
        }
        return Objects.equals(respuestaUsuario, solucionEsperada);
    }

    @Override
    public String getTipoItem() {
        return "Opción Múltiple";
    }
}