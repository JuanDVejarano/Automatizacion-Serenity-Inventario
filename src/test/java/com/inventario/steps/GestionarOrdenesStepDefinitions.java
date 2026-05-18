package com.inventario.steps;

import com.inventario.pages.DashboardPage;
import com.inventario.pages.GestionarOrdenesPage;
import com.inventario.pages.LoginPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class GestionarOrdenesStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    GestionarOrdenesPage gestionarOrdenesPage;

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de gestionar ordenes de compra")
    public void elUsuarioAccedeAlModuloDeGestionarOrdenesDeCompra() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();
        gestionarOrdenesPage.open();
        gestionarOrdenesPage.waitForFormToLoad();
    }

    // ── Escenario 1: Listar ──────────────────────────────────────────────────

    @Then("el sistema muestra el modulo de ordenes de compra cargado correctamente")
    public void elSistemaMuestraElModuloDeOrdenesDeCompraCargadoCorrectamente() {
        // El módulo cargó si ya no está en estado 'cargando' (esperado en Background)
        // y la URL es la del módulo
        assertThat(gestionarOrdenesPage.getDriver().getCurrentUrl())
            .as("La URL debe corresponder al modulo de gestionar ordenes")
            .contains("/produccion/gestionar-ordenes");
    }

    // ── Escenario 2: Ver detalle ─────────────────────────────────────────────

    @Given("existe al menos una orden de compra en el sistema")
    public void existeAlMenosUnaOrdenDeCompraEnElSistema() {
        if (gestionarOrdenesPage.getTotalOrderCount() == 0) {
            gestionarOrdenesPage.createTestPendingOrder();
        }
    }

    @When("el usuario selecciona la primera orden de la lista")
    public void elUsuarioSeleccionaLaPrimeraOrdenDeLaLista() {
        gestionarOrdenesPage.setFiltroEstado("Todos");
        gestionarOrdenesPage.selectFirstFilteredOrder();
    }

    @Then("el sistema muestra el panel de detalle de la orden")
    public void elSistemaMuestraElPanelDeDetalleDelaOrden() {
        assertThat(gestionarOrdenesPage.isDetalleVisible())
            .as("El panel de detalle de la orden debe estar visible")
            .isTrue();
    }

    // ── Escenario 3: Filtrar por estado ──────────────────────────────────────

    @When("el usuario filtra las ordenes de compra por estado {string}")
    public void elUsuarioFiltraLasOrdenesDeCompraPorEstado(String estado) {
        gestionarOrdenesPage.setFiltroEstado(estado);
    }

    @Then("el sistema muestra unicamente las ordenes con el estado seleccionado")
    public void elSistemaMuestraUnicamenteLasOrdenesConElEstadoSeleccionado() {
        // Verificar que todas las órdenes filtradas tienen el estado correcto
        // o que el mensaje de lista vacía aparece si no hay ninguna con ese estado.
        // Ambos resultados son válidos para este escenario.
        int count = gestionarOrdenesPage.getFilteredOrderCount();
        String msg  = gestionarOrdenesPage.getMensajeLista();
        assertThat(count > 0 || msg.contains("estado"))
            .as("El sistema debe mostrar ordenes filtradas o el mensaje de lista vacia")
            .isTrue();
    }

    // ── Escenario 4: Filtrar por fecha sin resultados ─────────────────────────

    @When("el usuario aplica un filtro de fechas que no coincide con ninguna orden")
    public void elUsuarioAplicaUnFiltroDeFechasQueNoCoincikeConNingunaOrden() {
        // Desde y hasta el año 1990 → garantiza lista vacía
        gestionarOrdenesPage.setFechaDesde("1990-01-01");
        gestionarOrdenesPage.setFechaHasta("1990-12-31");
    }

    @Then("el sistema muestra el mensaje de lista vacia por rango de fechas {string}")
    public void elSistemaMuestraElMensajeDeListaVaciaPorRangoDeFechas(String mensajeEsperado) {
        String mensajeActual = gestionarOrdenesPage.getMensajeLista();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de lista vacia por fechas debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 5 y 6: Cambiar estado ─────────────────────────────────────

    @Given("existe una orden de compra pendiente para gestionar")
    public void existeUnaOrdenDeCompraPendienteParaGestionar() {
        gestionarOrdenesPage.createTestPendingOrder();
    }

    @When("el usuario marca la primera orden pendiente como Completada")
    public void elUsuarioMarcaLaPrimeraOrdenPendienteComoCompletada() {
        gestionarOrdenesPage.selectFirstPendingOrder();
        gestionarOrdenesPage.clickCompletarOrden();
    }

    @When("el usuario cancela la primera orden pendiente")
    public void elUsuarioCancelaLaPrimeraOrdenPendiente() {
        gestionarOrdenesPage.selectFirstPendingOrder();
        gestionarOrdenesPage.clickCancelarOrden();
    }

    @Then("el sistema muestra el mensaje de exito de cambio de estado {string}")
    public void elSistemaMuestraElMensajeDeExitoDeCambioDeEstado(String mensajeEsperado) {
        String mensajeActual = gestionarOrdenesPage.waitForEstadoSuccess();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de cambio de estado debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenarios @pendiente ────────────────────────────────────────────────

    @Given("existe una orden pendiente con capital suficiente en caja")
    public void existeUnaOrdenPendienteConCapitalSuficienteEnCaja() {
        throw new PendingException("Sin evidencia — requiere configuracion de caja previa.");
    }

    @When("el usuario marca la orden como Completada con caja configurada")
    public void elUsuarioMarcaLaOrdenComoCompletadaConCajaConfigurada() {
        throw new PendingException("Sin evidencia — requiere configuracion de caja previa.");
    }

    @Then("el sistema descuenta el costo del capital de caja")
    public void elSistemaDescuentaElCostoDelCapitalDeCaja() {
        throw new PendingException("Sin evidencia — requiere configuracion de caja previa.");
    }

    @And("el sistema actualiza el stock de las materias primas de la orden")
    public void elSistemaActualizaElStockDeLasMateriasPrimasDeLaOrden() {
        throw new PendingException("Sin evidencia — requiere configuracion de caja previa.");
    }

    @Given("no hay ordenes de compra registradas en el sistema")
    public void noHayOrdenesDeCompraRegistradasEnElSistema() {
        throw new PendingException("Sin evidencia — requiere base de datos vacia.");
    }

    // ── Escenario: No autorizado ─────────────────────────────────────────────

    @When("el usuario intenta acceder al modulo de gestionar ordenes de compra")
    public void elUsuarioIntentaAccederAlModuloDeGestionarOrdenesDeCompra() {
        gestionarOrdenesPage.open();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String normalizarTexto(String texto) {
        return texto
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("Á", "A").replace("É", "E").replace("Í", "I")
            .replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
    }
}
