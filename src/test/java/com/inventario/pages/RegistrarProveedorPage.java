package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:4200/produccion/registrar-proveedor")
public class RegistrarProveedorPage extends PageObject {

    @FindBy(id = "nombre")     private WebElementFacade nombreField;
    @FindBy(id = "direccion")  private WebElementFacade direccionField;
    @FindBy(id = "direccion2") private WebElementFacade direccion2Field;
    @FindBy(id = "telefono")   private WebElementFacade telefonoField;
    @FindBy(id = "telefono2")  private WebElementFacade telefono2Field;
    @FindBy(id = "correo")     private WebElementFacade correoField;

    @FindBy(css = "button.btn-primary")   private WebElementFacade submitButton;
    @FindBy(css = ".alert.alert-error")   private WebElementFacade errorAlert;
    @FindBy(css = ".alert.alert-success") private WebElementFacade successAlert;

    public void waitForFormToLoad() {
        waitFor(submitButton);
    }

    // nit es signal<number|null> — se setea via ng.getComponent() para evitar
    // problemas con el input[type=number] y la conversión (ngModelChange)
    public void fillNit(long nit) {
        evaluateJavascript(
            "var _c=ng.getComponent(document.querySelector('app-registrar-proveedor'));" +
            "if(_c) _c.nit.set(" + nit + ");");
    }

    public void fillNombre(String nombre) {
        nombreField.clear();
        nombreField.type(nombre);
    }

    public void fillDireccion(String direccion) {
        direccionField.clear();
        direccionField.type(direccion);
    }

    public void fillDireccion2(String direccion2) {
        direccion2Field.clear();
        direccion2Field.type(direccion2);
    }

    public void fillTelefono(String telefono) {
        telefonoField.clear();
        telefonoField.type(telefono);
    }

    public void fillTelefono2(String telefono2) {
        telefono2Field.clear();
        telefono2Field.type(telefono2);
    }

    public void fillCorreo(String correo) {
        correoField.clear();
        correoField.type(correo);
    }

    public void submit() {
        submitButton.click();
    }

    public String getSuccessMessage() {
        waitFor(successAlert);
        return successAlert.getText().trim();
    }

    public String getErrorMessage() {
        waitFor(errorAlert);
        return errorAlert.getText().trim();
    }
}
