package com.inventario.steps;

import com.inventario.pages.AsociarMateriaPrimaPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarMateriaPrimaPage;
import com.inventario.pages.RegistrarProductoPage;
import com.inventario.pages.TipoProductoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class AsociarMateriaPrimaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    TipoProductoPage tipoProductoPage;
    RegistrarProductoPage registrarProductoPage;
    RegistrarMateriaPrimaPage registrarMateriaPrimaPage;
    AsociarMateriaPrimaPage asociarMateriaPrimaPage;

    private int countBeforeDeletion;

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de asociar materia prima")
    public void elUsuarioAccedeAlModuloDeAsociarMateriaPrima() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();

        // Crear tipo y producto
        tipoProductoPage.open();
        tipoProductoPage.waitForFormToLoad();
        tipoProductoPage.fillNombre("Tipo Asoc " + Math.abs(System.nanoTime() % 999_999L));
        tipoProductoPage.submit();
        tipoProductoPage.getSuccessMessage();

        registrarProductoPage.open();
        registrarProductoPage.waitForFormToLoad();
        registrarProductoPage.fillNombre("Prod Asoc " + Math.abs(System.nanoTime() % 999_999L));
        registrarProductoPage.selectFirstTipo();
        registrarProductoPage.fillPrecio("10000");
        registrarProductoPage.submit();
        registrarProductoPage.getSuccessMessage();

        // Crear dos materias primas (necesario para el escenario de múltiples)
        registrarMateriaPrimaPage.open();
        registrarMateriaPrimaPage.waitForFormToLoad();
        registrarMateriaPrimaPage.fillNombre("MP A " + Math.abs(System.nanoTime() % 999_999L));
        registrarMateriaPrimaPage.submit();
        registrarMateriaPrimaPage.getSuccessMessage();

        registrarMateriaPrimaPage.fillNombre("MP B " + Math.abs(System.nanoTime() % 999_999L));
        registrarMateriaPrimaPage.submit();
        registrarMateriaPrimaPage.getSuccessMessage();

        // Navegar al módulo de asociar materia prima
        asociarMateriaPrimaPage.open();
        asociarMateriaPrimaPage.waitForFormToLoad();
    }

    // ── Given: asociacion preexistente ──────────────────────────────────────

    @Given("existe al menos una materia prima asociada al producto seleccionado")
    public void existeAlMenosUnaMateriaPrimaAsociadaAlProductoSeleccionado() {
        asociarMateriaPrimaPage.selectFirstProducto();
        asociarMateriaPrimaPage.selectFirstMateriaDisponible();
        asociarMateriaPrimaPage.fillNuevaCantidad(5);
        asociarMateriaPrimaPage.asociar();
        asociarMateriaPrimaPage.waitForAssociationCount(1);
    }

    // ── Escenario 1: Asociación exitosa ─────────────────────────────────────

    @When("el usuario selecciona el primer producto y asocia la primera materia prima con cantidad 5")
    public void elUsuarioSeleccionaElPrimerProductoYAsociaLaPrimeraMateriaPrimaConCantidad5() {
        asociarMateriaPrimaPage.selectFirstProducto();
        asociarMateriaPrimaPage.selectFirstMateriaDisponible();
        asociarMateriaPrimaPage.fillNuevaCantidad(5);
        asociarMateriaPrimaPage.asociar();
    }

    @Then("la materia prima queda registrada en la lista de asociaciones del producto")
    public void laMateriaPrimaQuedaRegistradaEnLaListaDeAsociacionesDelProducto() {
        asociarMateriaPrimaPage.waitForAssociationCount(1);
        assertThat(asociarMateriaPrimaPage.getAssociationCount())
            .as("Debe haber al menos una materia prima asociada")
            .isGreaterThanOrEqualTo(1);
    }

    // ── Escenario 2: Múltiples materias ─────────────────────────────────────

    @When("el usuario selecciona el primer producto y asocia la primera materia prima con cantidad 3")
    public void elUsuarioSeleccionaElPrimerProductoYAsociaLaPrimeraMateriaPrimaConCantidad3() {
        asociarMateriaPrimaPage.selectFirstProducto();
        asociarMateriaPrimaPage.selectFirstMateriaDisponible();
        asociarMateriaPrimaPage.fillNuevaCantidad(3);
        asociarMateriaPrimaPage.asociar();
        asociarMateriaPrimaPage.waitForAssociationCount(1);
    }

    @And("el usuario asocia la segunda materia prima disponible con cantidad 2")
    public void elUsuarioAsociaLaSegundaMateriaPrimaDisponibleConCantidad2() {
        // Tras la primera asociación, materiasDisponibles()[0] es la segunda materia
        asociarMateriaPrimaPage.selectFirstMateriaDisponible();
        asociarMateriaPrimaPage.fillNuevaCantidad(2);
        asociarMateriaPrimaPage.asociar();
    }

    @Then("el sistema muestra 2 materias primas asociadas al producto")
    public void elSistemaMuestra2MateriasPrimasAsociadasAlProducto() {
        asociarMateriaPrimaPage.waitForAssociationCount(2);
        assertThat(asociarMateriaPrimaPage.getAssociationCount())
            .as("Deben haber al menos 2 materias primas asociadas")
            .isGreaterThanOrEqualTo(2);
    }

    // ── Escenario 3: Materia ya asociada (409) ───────────────────────────────

    @When("el usuario intenta asociar la misma materia prima al mismo producto nuevamente")
    public void elUsuarioIntentaAsociarLaMismaMateriaPrimaAlMismoProductoNuevamente() {
        // Bypasea el filtro de materiasDisponibles y fuerza la duplicación → 409
        asociarMateriaPrimaPage.forceAssociateDuplicate();
    }

    @Then("el sistema muestra el mensaje de error de asociacion {string}")
    public void elSistemaMuestraElMensajeDeErrorDeAsociacion(String mensajeEsperado) {
        String mensajeActual = asociarMateriaPrimaPage.waitForErrorMsg();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de asociacion debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 4: Cantidad inválida ───────────────────────────────────────

    @When("el usuario selecciona el primer producto y una materia prima con cantidad invalida de cero")
    public void elUsuarioSeleccionaElPrimerProductoYUnaMateriaPrimaConCantidadInvalidaDeCero() {
        asociarMateriaPrimaPage.selectFirstProducto();
        asociarMateriaPrimaPage.asociarConCantidadInvalida();
    }

    // ── Escenario 5: Editar cantidad ─────────────────────────────────────────

    @When("el usuario edita la cantidad de la primera materia prima asociada a 10")
    public void elUsuarioEditaLaCantidadDeLaPrimeraMateriaPrimaAsociadaA10() {
        asociarMateriaPrimaPage.editFirstAssociation(10);
        asociarMateriaPrimaPage.saveFirstAssociationEdit();
    }

    @Then("el sistema muestra el mensaje de exito de edicion de asociacion {string}")
    public void elSistemaMuestraElMensajeDeExitoDeEdicionDeAsociacion(String mensajeEsperado) {
        String mensajeActual = asociarMateriaPrimaPage.waitForEditSuccess();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de edicion debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 6: Eliminar asociación ────────────────────────────────────

    @When("el usuario elimina la primera materia prima asociada al producto")
    public void elUsuarioEliminaLaPrimeraMateriaPrimaAsociadaAlProducto() {
        countBeforeDeletion = asociarMateriaPrimaPage.getAssociationCount();
        asociarMateriaPrimaPage.requestDeleteFirstAssociation();
        asociarMateriaPrimaPage.confirmDeletion();
    }

    @Then("la asociacion queda eliminada de la lista del producto")
    public void laAsociacionQuedaEliminadaDeLaListaDelProducto() {
        asociarMateriaPrimaPage.waitForAssociationCountBelow(countBeforeDeletion);
        assertThat(asociarMateriaPrimaPage.getAssociationCount())
            .as("La lista debe tener una asociacion menos que antes de eliminar")
            .isEqualTo(countBeforeDeletion - 1);
    }

    // ── Escenario 7: Sin materias primas registradas ─────────────────────────

    @When("el usuario selecciona el primer producto y no hay materias primas en el sistema")
    public void elUsuarioSeleccionaElPrimerProductoYNoHayMateriasPrimasEnElSistema() {
        asociarMateriaPrimaPage.selectFirstProducto();
        asociarMateriaPrimaPage.simulateNoMaterias();
    }

    @Then("el sistema muestra la advertencia de que no hay materias primas disponibles")
    public void elSistemaMuestraLaAdvertenciaDeQueNoHayMateriasPrimasDisponibles() {
        assertThat(asociarMateriaPrimaPage.isWarningVisible())
            .as("La advertencia de sin materias primas debe ser visible")
            .isTrue();
    }

    // ── Escenario 8: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de asociar materia prima a producto")
    public void elUsuarioIntentaAccederAlModuloDeAsociarMateriaPrimaAProducto() {
        asociarMateriaPrimaPage.open();
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
