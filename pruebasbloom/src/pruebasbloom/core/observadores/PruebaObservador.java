/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.core.observadores;
import pruebasbloom.core.modelo.ItemPrueba;
import pruebasbloom.core.modelo.NivelBloom;
import java.util.Map;

/**
 *
 * @author carlospetit
 */
public interface PruebaObservador {

    /**
     * Se llama cuando se completa el proceso de carga de una prueba.
     *
     * @param totalItems El número total de ítems cargados en la prueba.
     * @param tiempoEstimadoTotal El tiempo total estimado para la prueba (en segundos).
     * @param exito true si la carga fue exitosa, false si hubo algún error.
     * @param mensajeError Un mensaje descriptivo en caso de error, o un mensaje de éxito.
     */
    void onPruebaCargada(int totalItems, int tiempoEstimadoTotal, boolean exito, String mensajeError);

    /**
     * Se llama cuando el ítem actual cambia durante la aplicación de la prueba.
     *
     * @param item El nuevo ítem actual que se debe mostrar.
     * @param indiceActual El índice del ítem actual (basado en 0).
     * @param totalItems El número total de ítems en la prueba.
     * @param respuestaPrevia La respuesta que el usuario pudo haber guardado previamente para este ítem.
     */
    void onItemCambiado(ItemPrueba item, int indiceActual, int totalItems, Object respuestaPrevia);

    /**
     * Se llama cuando los resultados de la prueba han sido calculados y están listos para mostrarse.
     *
     * @param correctasPorNivel Un mapa donde la clave es el NivelBloom y el valor es el porcentaje de respuestas correctas (0.0 a 100.0).
     * @param correctasPorTipo Un mapa donde la clave es el tipo de ítem (String) y el valor es el porcentaje de respuestas correctas (0.0 a 100.0).
     */
    void onResultadosCalculados(Map<NivelBloom, Double> correctasPorNivel, Map<String, Double> correctasPorTipo);

    /**
     * Se llama cuando el ítem actual cambia durante el modo de revisión detallada de respuestas.
     *
     * @param item El ítem actual que se está revisando.
     * @param indiceActual El índice del ítem actual (basado en 0).
     * @param totalItems El número total de ítems en la prueba.
     * @param respuestaUsuario La respuesta que el usuario dio para este ítem.
     * @param esRespuestaCorrecta true si la respuesta del usuario fue correcta, false en caso contrario.
     */
    void onItemRevisionCambiado(ItemPrueba item, int indiceActual, int totalItems, Object respuestaUsuario, boolean esRespuestaCorrecta);
}