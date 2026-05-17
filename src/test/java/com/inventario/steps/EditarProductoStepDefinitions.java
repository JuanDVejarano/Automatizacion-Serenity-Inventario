package com.inventario.steps;

import com.inventario.pages.EditarProductoPage;
import com.inventario.pages.RegistrarProductoPage;
import com.inventario.pages.TipoProductoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class EditarProductoStepDefinitions {

    TipoProductoPage tipoProductoPage;
    RegistrarProductoPage registrarProductoPage;
    EditarProductoPage editarProductoPage;

    // ── Background: garantiza que haya un producto para editar ───────────────

    @Given("existe al menos un producto disponible para editar")
    public void existeAlMenosUnProductoDisponibleParaEditar() {
        // 1. Crear un tipo de producto
        tipoProductoPage.open();
        tipoProductoPage.waitForFormToLoad();
        tipoProductoPage.fillNombre("Tipo Edit " + Math.abs(System.nanoTime() % 999_999L));
        tipoProductoPage.submit();
        tipoProductoPage.getSuccessMessage();

        // 2. Crear un producto con ese tipo
        registrarProductoPage.open();
        registrarProductoPage.waitForFormToLoad();
        registrarProductoPage.fillNombre("Prod Edit " + Math.abs(System.nanoTime() % 999_999L));
        registrarProductoPage.selectFirstTipo();
        registrarProductoPage.fillPrecio("12000");
        registrarProductoPage.submit();
        registrarProductoPage.getSuccessMessage();

        // 3. Navegar al módulo de edición y esperar carga
        editarProductoPage.open();
        editarProductoPage.waitForFormToLoad();

        // 4. Seleccionar el primer producto del dropdown para que el formulario de edición
        //    (@if productoEditando()) sea visible y esté listo para los siguientes pasos
        editarProductoPage.selectFirstProductForEdit();
    }

    // ── Editar información ───────────────────────────────────────────────────

    @When("el usuario selecciona el primer producto para editar su informacion")
    public void elUsuarioSeleccionaElPrimerProductoParaEditarSuInformacion() {
        editarProductoPage.selectFirstProductForEdit();
    }

    @And("el usuario modifica el nombre del producto")
    public void elUsuarioModificaElNombreDelProducto() {
        editarProductoPage.setEditNombre("Nombre Editado " + Math.abs(System.nanoTime() % 99_999L));
    }

    @And("el usuario establece un precio invalido de cero")
    public void elUsuarioEstableceUnPrecioInvalidoDeCero() {
        editarProductoPage.setEditPrecio(0);
    }

    @And("el usuario borra el nombre del producto")
    public void elUsuarioBorraElNombreDelProducto() {
        editarProductoPage.clearEditNombre();
    }

    @And("el usuario guarda los cambios del producto")
    public void elUsuarioGuardaLosCambiosDelProducto() {
        editarProductoPage.saveEdicion();
    }

    // ── Actualizar stock ─────────────────────────────────────────────────────

    @When("el usuario selecciona el primer producto para actualizar su stock")
    public void elUsuarioSeleccionaElPrimerProductoParaActualizarSuStock() {
        editarProductoPage.selectFirstProductForStock();
    }

    @And("el usuario ingresa una cantidad valida de unidades al stock")
    public void elUsuarioIngresaUnaCantidadValidaDeUnidadesAlStock() {
        editarProductoPage.setCantidad(10);
    }

    @And("el usuario ingresa una cantidad invalida de cero unidades")
    public void elUsuarioIngresaUnaCantidadInvalidaDeCeroUnidades() {
        editarProductoPage.setCantidad(0);
    }

    @And("el usuario confirma la actualizacion de stock")
    public void elUsuarioConfirmaLaActualizacionDeStock() {
        editarProductoPage.saveStock();
    }

    // ── Barcode ──────────────────────────────────────────────────────────────

    @When("el sistema identifica el primer producto por su codigo de barras")
    public void elSistemaIdentificaElPrimerProductoPorSuCodigoDeBarras() {
        // identificarProducto() es private en TS pero accesible en JS runtime.
        // En headless Chrome la cámara no funciona; testeamos la lógica de identificación directamente.
        editarProductoPage.identifyFirstProductByBarcode();
    }

    @When("el usuario escanea un codigo de barras inexistente en el sistema")
    public void elUsuarioEscaneaUnCodigoDeBarrasInexistenteEnElSistema() {
        editarProductoPage.identifyInvalidBarcode();
    }

    // ── Acceso no autorizado ─────────────────────────────────────────────────

    @When("el usuario intenta acceder al modulo de edicion de productos")
    public void elUsuarioIntentaAccederAlModuloDeEdicionDeProductos() {
        editarProductoPage.open();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("el sistema muestra el mensaje de exito de edicion de producto {string}")
    public void elSistemaMuestraElMensajeDeExitoDeEdicionDeProducto(String mensajeEsperado) {
        String mensajeActual = editarProductoPage.waitForEditSuccess();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de edicion debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de edicion de producto {string}")
    public void elSistemaMuestraElMensajeDeErrorDeEdicionDeProducto(String mensajeEsperado) {
        String mensajeActual = editarProductoPage.waitForEditError();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de edicion debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de exito de stock {string}")
    public void elSistemaMuestraElMensajeDeExitoDeStock(String mensajeEsperado) {
        String mensajeActual = editarProductoPage.waitForStockSuccess();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de stock debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de error de stock {string}")
    public void elSistemaMuestraElMensajeDeErrorDeStock(String mensajeEsperado) {
        String mensajeActual = editarProductoPage.waitForStockError();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de stock debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    @Then("el sistema muestra el mensaje de barcode no encontrado {string}")
    public void elSistemaMuestraElMensajeDeBarcodeNoEncontrado(String mensajeEsperado) {
        String mensajeActual = editarProductoPage.waitForBarcodeError();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de barcode no encontrado debe coincidir")
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
