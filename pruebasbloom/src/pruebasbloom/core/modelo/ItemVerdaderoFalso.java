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
public class ItemVerdaderoFalso extends ItemPrueba {

    public ItemVerdaderoFalso(int id, String enunciado, NivelBloom nivelBloom, int tiempoEstimado, int anioUso, boolean solucionCorrecta) {
        super(id, enunciado, nivelBloom, tiempoEstimado, anioUso, solucionCorrecta);
    }

    public boolean getSolucionCorrectaVF() {
        return (Boolean) solucionEsperada;
    }

    @Override
    public boolean esRespuestaCorrecta() {
        if (respuestaUsuario == null || !(respuestaUsuario instanceof Boolean)) {
            return false;
        }
        return Objects.equals(respuestaUsuario, solucionEsperada);
    }

    @Override
    public String getTipoItem() {
        return "Verdadero/Falso";
    }
}