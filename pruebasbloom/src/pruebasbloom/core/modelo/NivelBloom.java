/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.core.modelo;

/**
 *
 * @author carlospetit
 */
public enum NivelBloom {
    RECORDAR,
    ENTENDER,
    APLICAR,
    ANALIZAR,
    EVALUAR,
    CREAR;

    /**
     * 
     * 
     *
     * @param texto
     * @return El NivelBloom .
     * @throws IllegalArgumentException
     */
    public static NivelBloom fromString(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto para NivelBloom no puede ser nulo o vacío.");
        }
        for (NivelBloom nivel : NivelBloom.values()) {
            if (nivel.name().equalsIgnoreCase(texto.trim())) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Nivel Bloom no reconocido: " + texto);
    }
}
