package com.inventario.steps;

import com.inventario.pages.ClientesPage;
import com.inventario.pages.ListaVentasPage;
import com.inventario.pages.RegistrarVentaPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsultarHistorialVentasStepDefinitions {

    ClientesPage clientesPage;
    RegistrarVentaPage ventaPage;
    ListaVentasPage listaVentasPage;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    // Crea un cliente y una venta para garantizar datos en el historial
    private void createQuickSale() {
        String cedula = generateCedula();
        clientesPage.open();
        clientesPage.waitForPage();
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            cedula, "CLI Hist " + cedula, "hist" + cedula + "@test.com",
            "Bogota", "Calle Hist 1", "3001110000");
        clientesPage.submit();
        clientesPage.getFormSuccessMessage();
        clientesPage.waitForModalToClose();

        ventaPage.open();
        ventaPage.waitForClientList();
        ventaPage.selectFirstClient();
        ventaPage.searchAndAddFirstProduct("a");
        ventaPage.clickConfirm();
        listaVentasPage.waitForPage();
    }

    // ── Background ──────────────────────────────────────────────────────────
    // "el usuario ha iniciado sesion en el modulo de ventas" → RegistrarVentaStepDefinitions

    @And("el usuario navega al historial de ventas")
    public void elUsuarioNavegaAlHistorialDeVentas() {
        listaVentasPage.open();
        listaVentasPage.waitForPage();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe al menos una venta registrada en el historial")
    public void existeAlMenosUnaVentaRegistradaEnElHistorial() {
        createQuickSale();
    }

    @Given("existe una venta disponible para cambio de estado en el historial")
    public void existeUnaVentaDisponibleParaCambioDeEstadoEnElHistorial() {
        // Una venta nueva siempre arranca en "A la espera de reparto" con btn-accion--next
        createQuickSale();
    }

    // ── Acciones: filtros ────────────────────────────────────────────────────

    @When("el usuario filtra las ventas por estado {string}")
    public void elUsuarioFiltraLasVentasPorEstado(String estado) {
        listaVentasPage.filterByEstado(estado);
    }

    @When("el usuario filtra las ventas por un rango de fechas futuro")
    public void elUsuarioFiltraLasVentasPorUnRangoDeFechasFuturo() {
        // Año 2099 garantiza que no hay ventas → "No hay ventas en ese rango de fechas"
        listaVentasPage.filterByFutureDateRange();
    }

    @When("el usuario busca en el historial ventas del cliente {string}")
    public void elUsuarioBuscaEnElHistorialVentasDelCliente(String texto) {
        listaVentasPage.searchByClient(texto);
    }

    // ── Acciones: detalle y cambio de estado ─────────────────────────────────

    @When("el usuario abre el detalle de la primera venta de la lista")
    public void elUsuarioAbreElDetalleDeLaPrimeraVentaDeLaLista() {
        listaVentasPage.clickFirstDetailButton();
    }

    @When("el usuario cambia la venta al siguiente estado disponible")
    public void elUsuarioCambiaLaVentaAlSiguienteEstadoDisponible() {
        // btn-accion--next corresponde a "En reparto" desde "A la espera de reparto"
        listaVentasPage.clickFirstNextStateButton();
    }

    // ── Pendiente ────────────────────────────────────────────────────────────

    @When("el historial no tiene ventas registradas para esta prueba")
    public void elHistorialNoTieneVentasRegistradasParaEstaPrueba() {
        throw new PendingException(
            "Este escenario requiere una base de datos sin ventas registradas.");
    }

    @Then("el sistema muestra en el historial el mensaje {string}")
    public void elSistemaMuestraEnElHistorialElMensaje(String mensajeEsperado) {
        String mensajeActual = listaVentasPage.getEstadoVacioMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje del historial debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra la tabla de ventas con las columnas requeridas")
    public void elSistemaMuestraLaTablaDeVentasConLasColumnasRequeridas() {
        assertThat(listaVentasPage.hasRequiredColumns())
            .as("La tabla de ventas debe mostrar: ID, Fecha, Cliente, Valor total, Estado")
            .isTrue();
    }

    @And("la tabla de ventas contiene al menos una venta")
    public void laTablaDeVentasContieneAlMenosUnaVenta() {
        assertThat(listaVentasPage.getVisibleSaleCount())
            .as("La tabla debe contener al menos una venta")
            .isGreaterThan(0);
    }

    @Then("el sistema muestra el modal con el detalle de la venta")
    public void elSistemaMuestraElModalConElDetalleDeLaVenta() {
        assertThat(listaVentasPage.isDetailModalVisible())
            .as("El modal de detalle de la venta debe estar visible")
            .isTrue();
    }

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
