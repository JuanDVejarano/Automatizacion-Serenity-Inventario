package com.inventario.steps;

import com.inventario.pages.AsociarProveedorMateriaPrimaPage;
import com.inventario.pages.DashboardPage;
import com.inventario.pages.LoginPage;
import com.inventario.pages.RegistrarMateriaPrimaPage;
import com.inventario.pages.RegistrarProveedorPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class AsociarProveedorMateriaPrimaStepDefinitions {

    LoginPage loginPage;
    DashboardPage dashboardPage;
    RegistrarProveedorPage registrarProveedorPage;
    RegistrarMateriaPrimaPage registrarMateriaPrimaPage;
    AsociarProveedorMateriaPrimaPage asociarProveedorPage;

    private int countBeforeDeletion;

    private long   generateNit()    { return Math.abs(System.nanoTime() % 9_999_999_999L); }
    private String generateNombre() { return "Prov " + Math.abs(System.nanoTime() % 999_999L); }
    private String generateCorreo() { return "prov" + Math.abs(System.nanoTime() % 999_999L) + "@test.com"; }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("el usuario accede al modulo de asociar proveedor a materia prima")
    public void elUsuarioAccedeAlModuloDeAsociarProveedorAMateriaPrima() {
        loginPage.open();
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin1412");
        loginPage.clickLoginButton();
        dashboardPage.waitForLogoutButton();

        // Crear un proveedor
        registrarProveedorPage.open();
        registrarProveedorPage.waitForFormToLoad();
        registrarProveedorPage.fillNit(generateNit());
        registrarProveedorPage.fillNombre(generateNombre());
        registrarProveedorPage.fillDireccion("Calle 10 # 20-30");
        registrarProveedorPage.fillTelefono("3001234567");
        registrarProveedorPage.fillCorreo(generateCorreo());
        registrarProveedorPage.submit();
        registrarProveedorPage.getSuccessMessage();

        // Crear dos materias primas (necesario para el escenario de múltiples)
        registrarMateriaPrimaPage.open();
        registrarMateriaPrimaPage.waitForFormToLoad();
        registrarMateriaPrimaPage.fillNombre("MP X " + Math.abs(System.nanoTime() % 999_999L));
        registrarMateriaPrimaPage.submit();
        registrarMateriaPrimaPage.getSuccessMessage();

        registrarMateriaPrimaPage.fillNombre("MP Y " + Math.abs(System.nanoTime() % 999_999L));
        registrarMateriaPrimaPage.submit();
        registrarMateriaPrimaPage.getSuccessMessage();

        // Navegar al módulo
        asociarProveedorPage.open();
        asociarProveedorPage.waitForFormToLoad();
    }

    // ── Given: asociacion preexistente ──────────────────────────────────────

    @Given("existe al menos una materia prima asociada al proveedor seleccionado")
    public void existeAlMenosUnaMateriaPrimaAsociadaAlProveedorSeleccionado() {
        asociarProveedorPage.selectFirstProveedor();
        asociarProveedorPage.selectFirstMateriaDisponible();
        asociarProveedorPage.fillNuevoCosto(5000);
        asociarProveedorPage.asociar();
        asociarProveedorPage.waitForAssociationCount(1);
    }

    // ── Escenario 1: Asociación exitosa ─────────────────────────────────────

    @When("el usuario selecciona el primer proveedor y asocia la primera materia prima con costo 5000")
    public void elUsuarioSeleccionaElPrimerProveedorYAsociaLaPrimeraMateriaPrimaConCosto5000() {
        asociarProveedorPage.selectFirstProveedor();
        asociarProveedorPage.selectFirstMateriaDisponible();
        asociarProveedorPage.fillNuevoCosto(5000);
        asociarProveedorPage.asociar();
    }

    @Then("la materia prima queda registrada en la lista de asociaciones del proveedor")
    public void laMateriaPrimaQuedaRegistradaEnLaListaDeAsociacionesDelProveedor() {
        asociarProveedorPage.waitForAssociationCount(1);
        assertThat(asociarProveedorPage.getAssociationCount())
            .as("Debe haber al menos una materia prima asociada al proveedor")
            .isGreaterThanOrEqualTo(1);
    }

    // ── Escenario 2: Múltiples materias ─────────────────────────────────────

    @When("el usuario selecciona el primer proveedor y asocia la primera materia prima con costo 3000")
    public void elUsuarioSeleccionaElPrimerProveedorYAsociaLaPrimeraMateriaPrimaConCosto3000() {
        asociarProveedorPage.selectFirstProveedor();
        asociarProveedorPage.selectFirstMateriaDisponible();
        asociarProveedorPage.fillNuevoCosto(3000);
        asociarProveedorPage.asociar();
        asociarProveedorPage.waitForAssociationCount(1);
    }

    @And("el usuario asocia la segunda materia prima disponible al proveedor con costo 2000")
    public void elUsuarioAsociaLaSegundaMateriaPrimaDisponibleAlProveedorConCosto2000() {
        asociarProveedorPage.selectFirstMateriaDisponible();
        asociarProveedorPage.fillNuevoCosto(2000);
        asociarProveedorPage.asociar();
    }

    @Then("el sistema muestra al menos 2 materias primas asociadas al proveedor")
    public void elSistemaMuestraAlMenos2MateriasPrimasAsociadasAlProveedor() {
        asociarProveedorPage.waitForAssociationCount(2);
        assertThat(asociarProveedorPage.getAssociationCount())
            .as("Deben haber al menos 2 materias primas asociadas al proveedor")
            .isGreaterThanOrEqualTo(2);
    }

    // ── Escenario 3: Asociación duplicada ────────────────────────────────────

    @When("el usuario intenta asociar la misma materia prima al mismo proveedor nuevamente")
    public void elUsuarioIntentaAsociarLaMismaMateriaPrimaAlMismoProveedorNuevamente() {
        asociarProveedorPage.forceAssociateDuplicate();
    }

    @Then("el sistema muestra el mensaje de error de asociacion proveedor {string}")
    public void elSistemaMuestraElMensajeDeErrorDeAsociacionProveedor(String mensajeEsperado) {
        String mensajeActual = asociarProveedorPage.waitForErrorMsg();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de error de asociacion proveedor debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 4: Costo inválido ──────────────────────────────────────────

    @When("el usuario selecciona el primer proveedor y una materia prima con costo invalido de cero")
    public void elUsuarioSeleccionaElPrimerProveedorYUnaMateriaPrimaConCostoInvalidoDeCero() {
        asociarProveedorPage.selectFirstProveedor();
        asociarProveedorPage.asociarConCostoInvalido();
    }

    // ── Escenario 5: Editar costo ────────────────────────────────────────────

    @When("el usuario edita el costo de la primera asociacion del proveedor a 9500")
    public void elUsuarioEditaElCostoDeLaPrimeraAsociacionDelProveedorA9500() {
        asociarProveedorPage.editFirstAssociation(9500);
        asociarProveedorPage.saveFirstAssociationEdit();
    }

    @Then("el sistema muestra el mensaje de exito de edicion de asociacion proveedor {string}")
    public void elSistemaMuestraElMensajeDeExitoDeEdicionDeAsociacionProveedor(String mensajeEsperado) {
        String mensajeActual = asociarProveedorPage.waitForEditSuccess();
        assertThat(normalizarTexto(mensajeActual))
            .as("El mensaje de exito de edicion de asociacion proveedor debe coincidir")
            .containsIgnoringCase(normalizarTexto(mensajeEsperado));
    }

    // ── Escenario 6: Eliminar asociación ────────────────────────────────────

    @When("el usuario elimina la primera asociacion del proveedor a materia prima")
    public void elUsuarioEliminaLaPrimeraAsociacionDelProveedorAMateriaPrima() {
        countBeforeDeletion = asociarProveedorPage.getAssociationCount();
        asociarProveedorPage.requestDeleteFirstAssociation();
        asociarProveedorPage.confirmDeletion();
    }

    @Then("la asociacion queda eliminada de la lista del proveedor")
    public void laAsociacionQuedaEliminadaDeLaListaDelProveedor() {
        asociarProveedorPage.waitForAssociationCountBelow(countBeforeDeletion);
        assertThat(asociarProveedorPage.getAssociationCount())
            .as("La lista debe tener una asociacion menos que antes de eliminar")
            .isEqualTo(countBeforeDeletion - 1);
    }

    // ── Escenario 7: Sin materias primas ─────────────────────────────────────

    @When("el usuario selecciona el primer proveedor y no hay materias primas en el sistema")
    public void elUsuarioSeleccionaElPrimerProveedorYNoHayMateriasPrimasEnElSistema() {
        asociarProveedorPage.selectFirstProveedor();
        asociarProveedorPage.simulateNoMaterias();
    }

    @Then("el sistema muestra la advertencia de que no hay materias primas disponibles para el proveedor")
    public void elSistemaMuestraLaAdvertenciaDeQueNoHayMateriasPrimasDisponiblesParaElProveedor() {
        assertThat(asociarProveedorPage.isWarningVisible())
            .as("La advertencia de sin materias primas debe ser visible")
            .isTrue();
    }

    // ── Escenario 8: Acceso no autorizado ────────────────────────────────────

    @When("el usuario intenta acceder al modulo de asociar proveedor a materia prima")
    public void elUsuarioIntentaAccederAlModuloDeAsociarProveedorAMateriaPrima() {
        asociarProveedorPage.open();
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
