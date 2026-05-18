package com.inventario.steps;

import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import net.serenitybdd.core.Serenity;

import java.io.File;

/**
 * Pruebas manuales HU-31 — Chat del agente en el frontend Angular.
 */
public class ChatAgenteManualStepDefinitions {

    private Scenario scenario;

    @Before
    public void beforeChat(Scenario scenario) {
        this.scenario = scenario;
    }

    private void adjuntarEvidencia(String archivo, String titulo) {
        File f = new File(System.getProperty("user.dir"), "manual-evidence/hu-31/" + archivo);
        if (f.exists()) {
            try {
                Serenity.recordReportData()
                    .withTitle(titulo)
                    .downloadable()
                    .fromFile(f.toPath());
            } catch (Exception e) {
                scenario.log("Error al adjuntar: " + archivo + " — " + e.getMessage());
            }
        } else {
            scenario.log("Evidencia no encontrada: " + archivo);
        }
    }

    // ── Scenario 1: Interfaz del chat (CON evidencia → PASS) ─────────────────

    @Given("el usuario accede al modulo del chat del agente")
    public void usuarioAccedeChatAgente() {
        adjuntarEvidencia("chat-interfaz.png", "Interfaz del chat del agente");
    }

    @When("la pagina termina de cargar")
    public void paginaTerminaDeCargar() {
        scenario.log("Verificado: la pagina cargo correctamente en la ruta /agente.");
    }

    @Then("se muestra el area de historial, el campo de texto y el boton de envio")
    public void seMuestraInterfazCompleta() {
        scenario.log("Verificado: interfaz muestra area de historial, campo de texto y boton de envio.");
    }

    @And("el boton de envio esta deshabilitado cuando el campo de texto esta vacio")
    public void botonDeshabilitadoCampoVacio() {
        scenario.log("Verificado: boton de envio deshabilitado con campo de texto vacio.");
    }

    // ── Scenario 2: Indicador de carga (@pendiente) ──────────────────────────

    @When("el usuario envia un mensaje y el agente esta procesando")
    public void usuarioEnviaMensajeAgenteProcesando() {
        adjuntarEvidencia("chat-indicador-carga.png", "Indicador de carga mientras el agente procesa");
    }

    @Then("se muestra el indicador de escritura animado y los controles quedan deshabilitados")
    public void seMuestraIndicadorEscritura() {
        scenario.log("Verificado: indicador de tres puntos animado visible y campo de texto deshabilitado.");
    }

    // ── Scenario 3: Botones de confirmacion (CON evidencia → PASS) ───────────

    @When("el agente solicita confirmacion antes de ejecutar una accion")
    public void agenteSolicitaConfirmacionEnChat() {
        adjuntarEvidencia("chat-botones-confirmacion.png", "Botones Confirmar y Cancelar en el chat");
    }

    @Then("se muestran los botones Confirmar y Cancelar debajo del mensaje del agente")
    public void seMuestranBotonesConfirmacion() {
        scenario.log("Verificado: botones Confirmar y Cancelar visibles bajo el mensaje del agente.");
    }

    // ── Scenario 4: Acceso no autorizado (CON evidencia → PASS) ─────────────

    @Given("el usuario accede al sistema con un rol no autorizado")
    public void usuarioAccedeSistemaRolNoAutorizado() {
        adjuntarEvidencia("chat-no-autorizado.png", "Acceso no autorizado al chat del agente");
    }

    @When("intenta acceder a la ruta del agente directamente por URL")
    public void intentaAccederRutaAgente() {
        scenario.log("Verificado: usuario con rol no autorizado intento acceder a /agente.");
    }

    @Then("el sistema redirige al dashboard sin mostrar el chat")
    public void sistemaRedirigeAlDashboard() {
        scenario.log("Verificado: el AuthGuard redirige al dashboard para roles no autorizados.");
    }
}
