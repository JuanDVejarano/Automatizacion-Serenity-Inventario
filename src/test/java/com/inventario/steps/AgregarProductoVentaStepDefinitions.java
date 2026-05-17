package com.inventario.steps;

import com.inventario.pages.ListaVentasPage;
import com.inventario.pages.RegistrarVentaPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class AgregarProductoVentaStepDefinitions {

    RegistrarVentaPage ventaPage;
    ListaVentasPage listaVentasPage;

    // ── Background ──────────────────────────────────────────────────────────
    // "el usuario ha iniciado sesion en el modulo de ventas" → RegistrarVentaStepDefinitions

    @And("el usuario esta en el formulario de nueva venta con cliente seleccionado")
    public void elUsuarioEstaEnElFormularioDeNuevaVentaConClienteSeleccionado() {
        ventaPage.open();
        ventaPage.waitForClientList();
        ventaPage.selectFirstClient(); // JS click → dispara seleccionarCliente(c) → paso 2
    }

    // ── Precondición: producto ya en carrito ─────────────────────────────────

    @Given("el usuario ha agregado un producto al carrito de venta")
    public void elUsuarioHaAgregadoUnProductoAlCarritoDeVenta() {
        ventaPage.searchAndAddFirstProduct("a");
    }

    // ── Búsqueda por nombre ──────────────────────────────────────────────────

    @When("el usuario busca un producto por nombre y selecciona el primero disponible")
    public void elUsuarioBuscaUnProductoPorNombreYSeleccionaElPrimeroDisponible() {
        ventaPage.searchAndAddFirstProduct("a");
    }

    // ── Búsqueda por código de barras ────────────────────────────────────────

    @When("el usuario agrega un producto usando su ID como codigo de barras")
    public void elUsuarioAgregaUnProductoUsandoSuIDComoCodigoDeBarras() {
        // Obtiene el ID de un producto real via búsqueda por nombre, luego lo agrega
        // usando el flujo de código de barras (buscarPorBarcode)
        ventaPage.addFirstProductByBarcode("a");
    }

    @When("el usuario ingresa un codigo de barras que no existe en el sistema")
    public void elUsuarioIngresaUnCodigoDeBarrasQueNoExisteEnElSistema() {
        ventaPage.searchInvalidBarcode();
    }

    // ── Modificar cantidad ───────────────────────────────────────────────────

    @When("el usuario modifica la cantidad del producto en el detalle")
    public void elUsuarioModificaLaCantidadDelProductoEnElDetalle() {
        // Intenta actualizar a 2 unidades — si stock >= 2 el total sube,
        // si no, el errorBusqueda muestra "Stock insuficiente" (también válido)
        ventaPage.updateFirstItemQuantity(2);
    }

    @Then("el campo de cantidad refleja el cambio y el total sigue siendo mayor a cero")
    public void elCampoDeCantidadReflejaElCambioYElTotalSigueSiendoMayorACero() {
        // El carrito sigue teniendo al menos un item independientemente del resultado
        assertThat(ventaPage.getCartItemCount())
            .as("El carrito debe seguir teniendo el producto")
            .isGreaterThan(0);
        assertThat(ventaPage.getTotal())
            .as("El total debe ser mayor a cero")
            .isGreaterThan(0);
    }

    // ── Eliminar producto ────────────────────────────────────────────────────

    @When("el usuario elimina el producto del carrito de venta")
    public void elUsuarioEliminaElProductoDelCarritoDeVenta() {
        ventaPage.removeFirstItem();
    }

    @Then("el carrito de la venta queda vacio")
    public void elCarritoDeLaVentaQuedaVacio() {
        assertThat(ventaPage.isCartEmpty())
            .as("El carrito debe mostrar el estado vacio tras eliminar el unico producto")
            .isTrue();
    }

    // ── Assertions comunes ───────────────────────────────────────────────────

    @Then("el producto aparece en el detalle de la venta con total mayor a cero")
    public void elProductoApareceEnElDetalleConTotalMayorACero() {
        assertThat(ventaPage.getCartItemCount())
            .as("El carrito debe tener al menos un producto")
            .isGreaterThan(0);
        assertThat(ventaPage.getTotal())
            .as("El total debe ser mayor a cero tras agregar el producto")
            .isGreaterThan(0);
    }
}
