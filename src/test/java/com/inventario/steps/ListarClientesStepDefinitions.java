package com.inventario.steps;

import com.inventario.pages.ClientesPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ListarClientesStepDefinitions {

    ClientesPage clientesPage;

    private String searchCedula;
    private String searchNombre;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    private void createClient(String cedula, String nombre) {
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            cedula, nombre, "lista" + cedula + "@serenity.test",
            "Bogota", "Calle Lista 1", "3001112222");
        clientesPage.submit();
        clientesPage.getFormSuccessMessage();
        clientesPage.waitForModalToClose();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe al menos un cliente registrado en el sistema")
    public void existeAlMenosUnClienteRegistradoEnElSistema() {
        searchCedula = generateCedula();
        createClient(searchCedula, "Cliente Lista " + searchCedula);
    }

    @Given("existe un cliente registrado para busqueda en la lista de clientes")
    public void existeUnClienteRegistradoParaBusquedaEnLaListaDeClientes() {
        searchCedula = generateCedula();
        searchNombre  = "BusqCli " + searchCedula;
        createClient(searchCedula, searchNombre);
    }

    // ── Navegación ───────────────────────────────────────────────────────────

    @When("el usuario navega al modulo de listado de clientes")
    public void elUsuarioNavegaAlModuloDeListadoDeClientes() {
        clientesPage.open();
        clientesPage.waitForPage();
    }

    // ── Búsqueda ─────────────────────────────────────────────────────────────

    @When("el usuario busca el cliente por nombre en la lista de clientes")
    public void elUsuarioBuscaElClientePorNombreEnLaListaDeClientes() {
        clientesPage.searchByText(searchNombre);
    }

    @When("el usuario busca el cliente por cedula en la lista de clientes")
    public void elUsuarioBuscaElClientePorCedulaEnLaListaDeClientes() {
        clientesPage.searchByText(searchCedula);
    }

    @And("el usuario busca por {string} en la lista de clientes")
    public void elUsuarioBuscaPorEnLaListaDeClientes(String termino) {
        clientesPage.searchByText(termino);
    }

    // ── Simulación lista vacía (@pendiente) ──────────────────────────────────

    @And("se simula que no hay clientes en el sistema")
    public void seSimulaQueNoHayClientesEnElSistema() {
        throw new PendingException(
            "Este escenario requiere una base de datos sin clientes registrados.");
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    @Then("el sistema muestra la tabla de clientes con las columnas requeridas")
    public void elSistemaMuestraLaTablaDeClientesConLasColumnasRequeridas() {
        assertThat(clientesPage.hasRequiredColumns())
            .as("La tabla debe mostrar las columnas: cedula/NIT, nombre, correo, ciudad, telefono")
            .isTrue();
    }

    @And("la tabla contiene al menos un cliente registrado")
    public void laTablaContieneAlMenosUnClienteRegistrado() {
        assertThat(clientesPage.getVisibleRowCount())
            .as("La tabla debe tener al menos un cliente")
            .isGreaterThan(0);
    }

    @Then("el sistema muestra el cliente en los resultados de busqueda de clientes")
    public void elSistemaMuestraElClienteEnLosResultadosDeBusquedaDeClientes() {
        assertThat(clientesPage.getVisibleRowCount())
            .as("Debe aparecer al menos un cliente en los resultados")
            .isGreaterThan(0);
    }

    @Then("el sistema muestra exactamente un cliente en la tabla de resultados de clientes")
    public void elSistemaMuestraExactamenteUnClienteEnLaTablaDeResultadosDeClientes() {
        assertThat(clientesPage.getVisibleRowCount())
            .as("La busqueda por cedula debe mostrar exactamente un cliente")
            .isEqualTo(1);
    }

    @Then("el sistema muestra en la lista de clientes el mensaje {string}")
    public void elSistemaMuestraEnLaListaDeClientesElMensaje(String mensajeEsperado) {
        String mensajeActual = clientesPage.getEstadoVacioMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de lista vacia o sin resultados debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
