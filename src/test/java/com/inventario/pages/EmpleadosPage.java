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
}
