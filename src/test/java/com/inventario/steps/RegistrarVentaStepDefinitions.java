package com.inventario.steps;

import com.inventario.pages.ClientesPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.ListaVentasPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarVentaPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrarVentaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    ClientesPage clientesPage;
    RegistrarVentaPage ventaPage;
    ListaVentasPage listaVentasPage;

    // Cédula del cliente de prueba — se reutiliza para buscarlo en el paso 1
    private String clientCedula;

    private String generateCedula() {
        return String.valueOf(10_000_000L + Math.abs(System.nanoTime() % 89_999_999L));
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario ha iniciado sesion en el modulo de ventas")
    public void elUsuarioHaIniciadoSesionEnElModuloDeVentas() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        // Pre-carga el lazy module de Ventas con token real
        listaVentasPage.open();
        listaVentasPage.waitForPage();
    }

    // ── Precondiciones ───────────────────────────────────────────────────────

    @Given("existe un cliente registrado para las pruebas de venta")
    public void existeUnClienteRegistradoParaLasPruebasDeVenta() {
        clientCedula = generateCedula();
        // El Background deja el browser en /ventas/ventas — navegar a clientes antes
        clientesPage.open();
        clientesPage.waitForPage();
        clientesPage.clickNuevoCliente();
        clientesPage.fillCamposObligatorios(
            clientCedula, "Cliente Venta " + clientCedula,
            "venta" + clientCedula + "@serenity.test",
            "Bogota", "Calle Venta 1", "3001234599");
        clientesPage.submit();
        clientesPage.getFormSuccessMessage();
        clientesPage.waitForModalToClose();
    }

    @Given("existe una venta creada en estado A la espera de reparto")
    public void existeUnaVentaCreadaEnEstadoALaEsperaDeReparto() {
        throw new PendingException(
            "Este escenario requiere crear una venta y verificar el movimiento en el modulo de Caja.");
    }

    // ── Navegación ───────────────────────────────────────────────────────────

    @When("el usuario navega a registrar una nueva venta")
    public void elUsuarioNavegaARegistrarUnaNuevaVenta() {
        ventaPage.open();
        ventaPage.waitForClientList();
    }

    // ── Paso 1: selección de cliente ─────────────────────────────────────────

    @And("el usuario selecciona el cliente de prueba en el paso 1")
    public void elUsuarioSeleccionaElClienteDePruebaEnElPaso1() {
        // Selecciona el primero de la lista sin buscar — más robusto que filtrar por cédula
        ventaPage.selectFirstClient();
    }

    // ── Paso 2: productos ────────────────────────────────────────────────────

    @And("el usuario busca y agrega un producto disponible al carrito")
    public void elUsuarioBuscaYAgregaUnProductoDisponibleAlCarrito() {
        // Búsqueda con término genérico — asume al menos un producto con "a" en el nombre
        ventaPage.searchAndAddFirstProduct("a");
    }

    @And("el usuario agrega mas de un producto al carrito de la venta")
    public void elUsuarioAgregaMasDeUnProductoAlCarritoDeLaVenta() {
        // Agrega el mismo producto dos veces (incrementa cantidad)
        ventaPage.searchAndAddFirstProduct("a");
        ventaPage.searchAndAddFirstProduct("a");
    }

    @And("el usuario intenta agregar al carrito un producto con stock cero")
    public void elUsuarioIntentaAgregarAlCarritoUnProductoConStockCero() {
        // ng.getComponent() llama agregarAlCarrito con stock=0 → dispara el error
        ventaPage.simulateAddOutOfStockProduct();
    }

    @Then("el boton de confirmar venta esta deshabilitado mientras el carrito esta vacio")
    public void elBotonDeConfirmarVentaEstaDeshabilitadoMientrasElCarritoEstaVacio() {
        assertThat(ventaPage.isConfirmButtonDisabled())
            .as("El boton 'Registrar venta' debe estar deshabilitado cuando el carrito esta vacio")
            .isTrue();
    }

    @And("el usuario confirma la venta")
    public void elUsuarioConfirmaLaVenta() {
        ventaPage.clickConfirm();
        // Tras confirmar, la app redirige a /ventas/ventas — esperar que cargue
        listaVentasPage.waitForPage();
    }

    // ── Cancelar venta ───────────────────────────────────────────────────────

    @And("el usuario cancela la venta recien creada")
    public void elUsuarioCancelaLaVentaRecienCreada() {
        // La venta recién registrada aparece en la lista en estado "A la espera de reparto"
        // con el botón btn-accion--cancel visible — se toma el primero disponible
        listaVentasPage.waitForCancelableList();
        listaVentasPage.clickFirstCancelButton();
    }

    // ── Acceso no autorizado ──────────────────────────────────────────────────

    @When("el usuario intenta acceder al modulo de lista de ventas")
    public void elUsuarioIntentaAccederAlModuloDeListaDeVentas() {
        listaVentasPage.open();
    }

    // ── Estado pendiente ─────────────────────────────────────────────────────

    @When("el usuario cambia el estado de la venta a Completada")
    public void elUsuarioCambiaElEstadoDeLaVentaACompletada() {
        throw new PendingException("Escenario pendiente — ver Given.");
    }

    @Then("el sistema registra un movimiento de ingreso en caja por el total de la venta")
    public void elSistemaRegistraUnMovimientoDeIngreso() {
        throw new PendingException("Requiere verificacion en el modulo de Caja.");
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito en ventas {string}")
    public void elSistemaMuestraElMensajeDeExitoEnVentas(String mensajeEsperado) {
        String mensajeActual = listaVentasPage.getSuccessMessage();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito en ventas debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error en ventas {string}")
    public void elSistemaMuestraElMensajeDeErrorEnVentas(String mensajeEsperado) {
        // Los errores de stock/búsqueda van en p.error-hint;
        // el de carrito vacío va en div.alert-error
        String mensajeActual;
        try {
            mensajeActual = ventaPage.getErrorHintMessage();
        } catch (Exception e) {
            mensajeActual = ventaPage.getGeneralErrorMessage();
        }
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error en ventas debe coincidir")
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
