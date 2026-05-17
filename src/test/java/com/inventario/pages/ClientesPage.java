package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/ventas/clientes")
public class ClientesPage extends PageObject {

    @FindBy(css = "button.btn-nuevo")
    private WebElementFacade nuevoClienteButton;

    @FindBy(css = "input.search-input")
    private WebElementFacade searchInput;

    @FindBy(css = "button.btn-editar")
    private WebElementFacade firstEditButton;

    @FindBy(css = ".estado-vacio p")
    private WebElementFacade estadoVacioMessage;

    @FindBy(css = "button.btn-guardar")
    private WebElementFacade submitButton;

    @FindBy(css = ".modal-alert.modal-alert--error")
    private WebElementFacade modalErrorAlert;

    @FindBy(css = ".modal-alert.modal-alert--success")
    private WebElementFacade modalSuccessAlert;

    // ── Navegación ───────────────────────────────────────────────────────────

    public void waitForPage() {
        waitFor(nuevoClienteButton);
    }

    public void clickNuevoCliente() {
        waitFor(nuevoClienteButton);
        nuevoClienteButton.click();
        waitFor(submitButton); // espera que el modal esté listo
    }

    // ── Campos del modal — todos por JS para garantizar que Angular
    //    recibe el evento 'input' y actualiza el signal correspondiente ───────

    private void fillFieldByName(String name, String value) {
        evaluateJavascript(
            "var el = document.querySelector(\"input[name='" + name + "']\");" +
            "if(el){ el.value='" + value + "'; el.dispatchEvent(new Event('input',{bubbles:true})); }");
    }

    public void fillCedula(String value)    { fillFieldByName("fCedula",    value); }
    public void fillNombre(String value)    { fillFieldByName("fNombre",    value); }
    public void fillCorreo(String value)    { fillFieldByName("fCorreo",    value); }
    public void fillCiudad(String value)    { fillFieldByName("fCiudad",    value); }
    public void fillDireccion(String value) { fillFieldByName("fDireccion", value); }
    public void fillTelefono(String value)  { fillFieldByName("fTelefono",  value); }
    public void fillTelefono2(String value) { fillFieldByName("fTelefono2", value); }

    public void fillCamposObligatorios(String cedula, String nombre, String correo,
                                        String ciudad, String direccion, String telefono) {
        fillCedula(cedula);
        fillNombre(nombre);
        fillCorreo(correo);
        fillCiudad(ciudad);
        fillDireccion(direccion);
        fillTelefono(telefono);
    }

    public void submit() {
        submitButton.click();
    }

    // ── Resultados ───────────────────────────────────────────────────────────

    public String getFormSuccessMessage() {
        waitFor(modalSuccessAlert);
        return modalSuccessAlert.getText().trim();
    }

    public String getFormErrorMessage() {
        waitFor(modalErrorAlert);
        return modalErrorAlert.getText().trim();
    }

    // ── HU-12: listado y búsqueda ────────────────────────────────────────────

    public void waitForList() {
        waitFor(firstEditButton);
    }

    public void searchByText(String text) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(text);
    }

    public int getVisibleRowCount() {
        Object count = evaluateJavascript(
            "return document.querySelectorAll('table.tabla tbody tr').length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    public boolean hasRequiredColumns() {
        return Boolean.TRUE.equals(evaluateJavascript(
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'').toLowerCase();}" +
            "var ths = Array.from(document.querySelectorAll('table.tabla thead th'));" +
            "var texts = ths.map(function(th){return norm(th.textContent.trim());});" +
            "var required = ['cedula','nombre','correo','ciudad','telefono'];" +
            "return required.every(function(r){return texts.some(function(t){return t.includes(r);});});"));
    }

    public String getEstadoVacioMessage() {
        waitFor(estadoVacioMessage);
        return estadoVacioMessage.getText().trim();
    }

    // ── Edición: buscar + abrir modal ────────────────────────────────────────

    public void searchClient(String cedula) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(cedula);
    }

    public void clickFirstEditButton() {
        waitFor(firstEditButton);
        firstEditButton.click();
        waitFor(submitButton); // espera que el modal de edición esté abierto
    }

    // Campos del modal de edición — mismo patrón que el modal de registro (name="editXxx")
    private void setEditFieldByName(String name, String value) {
        evaluateJavascript(
            "var el = document.querySelector(\"input[name='" + name + "']\");" +
            "if(el){ el.value='" + value + "'; el.dispatchEvent(new Event('input',{bubbles:true})); }");
    }

    private void clearEditFieldByName(String name) {
        evaluateJavascript(
            "var el = document.querySelector(\"input[name='" + name + "']\");" +
            "if(el){ el.value=''; el.dispatchEvent(new Event('input',{bubbles:true})); el.dispatchEvent(new Event('change',{bubbles:true})); }");
    }

    public void setEditNombre(String value)  { setEditFieldByName("editNombre",    value); }
    public void setEditCorreo(String value)  { setEditFieldByName("editCorreo",    value); }
    public void setEditCedula(String value)  { setEditFieldByName("editCedula",    value); }

    public void clearEditRequiredFields() {
        clearEditFieldByName("editNombre");
        clearEditFieldByName("editCorreo");
        clearEditFieldByName("editCiudad");
        clearEditFieldByName("editDireccion");
        clearEditFieldByName("editTelefono");
    }

    // esAdmin() en clientes usa el mismo computed() lazy — mismo truco que HU-05
    public boolean isEditCedulaDisabled() {
        return Boolean.TRUE.equals(
            evaluateJavascript(
                "return document.querySelector(\"input[name='editCedula']\").disabled;"));
    }

    public String getEditSuccessMessage() {
        waitFor(modalSuccessAlert);
        return modalSuccessAlert.getText().trim();
    }

    public String getEditErrorMessage() {
        waitFor(modalErrorAlert);
        return modalErrorAlert.getText().trim();
    }

    // Espera a que el modal se cierre (Angular lo cierra a los 1500ms tras el éxito)
    public void waitForModalToClose() {
        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            Object open = evaluateJavascript(
                "return document.querySelector('.modal-overlay') !== null;");
            if (!Boolean.TRUE.equals(open)) return;
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }
}
