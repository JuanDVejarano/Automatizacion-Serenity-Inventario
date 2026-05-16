package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/rrhh/empleados")
public class EmpleadosPage extends PageObject {

    // ── Lista ────────────────────────────────────────────────────────────────

    @FindBy(css = "input.search-input")
    private WebElementFacade searchInput;

    @FindBy(css = "button.btn-editar")
    private WebElementFacade firstEditButton;

    // ── Modal — campos (sin ID, usar name) ──────────────────────────────────

    @FindBy(css = "input[name='editCedula']")
    private WebElementFacade editCedulaField;

    @FindBy(css = "input[name='editNombre']")
    private WebElementFacade editNombreField;

    @FindBy(css = "input[name='editCorreo']")
    private WebElementFacade editCorreoField;

    @FindBy(css = "input[name='editDireccion']")
    private WebElementFacade editDireccionField;

    @FindBy(css = "input[name='editCiudad']")
    private WebElementFacade editCiudadField;

    @FindBy(css = "input[name='editTelefono']")
    private WebElementFacade editTelefonoField;

    @FindBy(css = "button.btn-guardar")
    private WebElementFacade saveButton;

    @FindBy(css = ".modal-alert.modal-alert--error")
    private WebElementFacade modalErrorAlert;

    @FindBy(css = ".modal-alert.modal-alert--success")
    private WebElementFacade modalSuccessAlert;

    // ── Lista: acciones ──────────────────────────────────────────────────────

    public void waitForList() {
        waitFor(firstEditButton);
    }

    public void searchEmployee(String cedula) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(cedula);
    }

    public void clickFirstEditButton() {
        waitFor(firstEditButton);
        firstEditButton.click();
    }

    // ── Modal: acciones ──────────────────────────────────────────────────────

    public void waitForModal() {
        waitFor(saveButton);
    }

    public void setEditNombre(String nombre) {
        editNombreField.clear();
        editNombreField.type(nombre);
    }

    public void setEditCedula(String cedula) {
        editCedulaField.clear();
        editCedulaField.type(cedula);
    }

    public void setEditCorreo(String correo) {
        editCorreoField.clear();
        editCorreoField.type(correo);
    }

    public void clearRequiredFields() {
        // clear() de Selenium no dispara el evento 'input' que Angular necesita
        // para actualizar los signals — se usa JS en todos los campos
        clearFieldByName("editNombre");
        clearFieldByName("editCorreo");
        clearFieldByName("editCiudad");
        clearFieldByName("editDireccion");
        clearFieldByName("editTelefono");
        clearFieldByName("editFecha");
    }

    private void clearFieldByName(String name) {
        evaluateJavascript(
            "var el = document.querySelector(\"input[name='" + name + "']\");" +
            "if(el){" +
            "  el.value='';" +
            "  el.dispatchEvent(new Event('input',  {bubbles:true}));" +
            "  el.dispatchEvent(new Event('change', {bubbles:true}));" +
            "}");
    }

    public void clickSave() {
        saveButton.click();
    }

    // ── Modal: verificaciones ────────────────────────────────────────────────

    // Usa JS directo para leer .disabled → más fiable que getAttribute con Angular
    public boolean isCedulaFieldDisabled() {
        return Boolean.TRUE.equals(
            evaluateJavascript(
                "return document.querySelector(\"input[name='editCedula']\").disabled;"));
    }

    public String getModalSuccessMessage() {
        waitFor(modalSuccessAlert);
        return modalSuccessAlert.getText().trim();
    }

    public String getModalErrorMessage() {
        waitFor(modalErrorAlert);
        return modalErrorAlert.getText().trim();
    }

    // ── HU-06: listado y búsqueda ────────────────────────────────────────────

    @FindBy(css = ".estado-vacio p")
    private WebElementFacade estadoVacioMessage;

    public void searchByText(String text) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(text);
    }

    // Selecciona el filtro de estado via JS para garantizar que Angular detecte el cambio
    public void filterByEstado(String value) {
        evaluateJavascript(
            "var el = document.querySelector('select.select-estado');" +
            "el.value = '" + value + "';" +
            "el.dispatchEvent(new Event('change', {bubbles:true}));");
    }

    public int getVisibleRowCount() {
        Object count = evaluateJavascript(
            "return document.querySelectorAll('table.tabla tbody tr').length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    // Verifica que las columnas requeridas existan (normaliza tildes en JS)
    public boolean hasRequiredColumns() {
        return Boolean.TRUE.equals(evaluateJavascript(
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'').toLowerCase();}" +
            "var ths = Array.from(document.querySelectorAll('table.tabla thead th'));" +
            "var texts = ths.map(function(th){return norm(th.textContent.trim());});" +
            "var required = ['cedula','nombre','correo','ciudad','telefono','fecha','estado'];" +
            "return required.every(function(r){return texts.some(function(t){return t.includes(r);});});"));
    }

    // Verifica que todos los badges visibles son 'badge--activo'
    public boolean allVisibleBadgesAreActive() {
        return Boolean.TRUE.equals(evaluateJavascript(
            "var badges = document.querySelectorAll('table.tabla tbody tr .badge');" +
            "return badges.length > 0 && " +
            "Array.from(badges).every(function(b){return b.classList.contains('badge--activo');});"));
    }

    // Simula lista vacía usando la API de Angular ng.getComponent (disponible en modo dev)
    public void simulateEmptyList() {
        evaluateJavascript(
            "var el = document.querySelector('app-empleados');" +
            "var comp = ng.getComponent(el);" +
            "comp.empleados.set([]);" +
            "comp.cargando.set(false);" +
            "ng.markDirty(comp);");
    }

    public String getEstadoVacioMessage() {
        waitFor(estadoVacioMessage);
        return estadoVacioMessage.getText().trim();
    }
}
