package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/rrhh/registrar-empleado")
public class RegistrarEmpleadoPage extends PageObject {

    @FindBy(id = "cedula")    private WebElementFacade cedulaField;
    @FindBy(id = "nombre")    private WebElementFacade nombreField;
    @FindBy(id = "correo")    private WebElementFacade correoField;
    @FindBy(id = "direccion") private WebElementFacade direccionField;
    @FindBy(id = "ciudad")    private WebElementFacade ciudadField;
    @FindBy(id = "telefono")  private WebElementFacade telefonoField;
    @FindBy(id = "telefono2") private WebElementFacade telefono2Field;

    @FindBy(css = "button.btn-primary")    private WebElementFacade submitButton;
    @FindBy(css = ".alert.alert-error")    private WebElementFacade errorAlert;
    @FindBy(css = ".alert.alert-success")  private WebElementFacade successAlert;

    public void fillCedula(String value) {
        cedulaField.clear();
        cedulaField.type(value);
    }

    public void fillNombre(String value) {
        nombreField.clear();
        nombreField.type(value);
    }

    public void fillCorreo(String value) {
        correoField.clear();
        correoField.type(value);
    }

    public void fillDireccion(String value) {
        direccionField.clear();
        direccionField.type(value);
    }

    public void fillCiudad(String value) {
        ciudadField.clear();
        ciudadField.type(value);
    }

    public void fillTelefono(String value) {
        telefonoField.clear();
        telefonoField.type(value);
    }

    public void fillTelefono2(String value) {
        telefono2Field.clear();
        telefono2Field.type(value);
    }

    // type="date" requiere JS para garantizar que Angular detecte el cambio
    public void fillFecha(String isoDate) {
        evaluateJavascript(
            "var el = document.getElementById('fecha');" +
            "el.value = '" + isoDate + "';" +
            "el.dispatchEvent(new Event('input',  {bubbles: true}));" +
            "el.dispatchEvent(new Event('change', {bubbles: true}));");
    }

    public void fillCamposObligatorios(String cedula, String nombre, String correo,
                                        String direccion, String ciudad, String telefono) {
        fillCedula(cedula);
        fillNombre(nombre);
        fillCorreo(correo);
        fillDireccion(direccion);
        fillCiudad(ciudad);
        fillTelefono(telefono);
        fillFecha("2024-06-15");
    }

    public void submit() {
        submitButton.click();
    }

    public boolean isErrorAlertVisible() {
        return errorAlert.isPresent() && errorAlert.isCurrentlyVisible();
    }

    public boolean isSuccessAlertVisible() {
        return successAlert.isPresent() && successAlert.isCurrentlyVisible();
    }

    public String getErrorMessage() {
        waitFor(errorAlert);
        return errorAlert.getText().trim();
    }

    public String getSuccessMessage() {
        waitFor(successAlert);
        return successAlert.getText().trim();
    }
}
