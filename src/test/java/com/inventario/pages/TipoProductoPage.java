package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/produccion/registrar-tipo-producto")
public class TipoProductoPage extends PageObject {

    @FindBy(id = "nombre")      private WebElementFacade nombreField;
    @FindBy(id = "descripcion") private WebElementFacade descripcionField;

    @FindBy(css = "button.btn-primary")   private WebElementFacade submitButton;
    @FindBy(css = ".alert.alert-error")   private WebElementFacade errorAlert;
    @FindBy(css = ".alert.alert-success") private WebElementFacade successAlert;

    // La página no tiene spinner de carga — el formulario está siempre visible
    public void waitForFormToLoad() {
        waitFor(submitButton);
    }

    public void fillNombre(String nombre) {
        nombreField.clear();
        nombreField.type(nombre);
    }

    public void fillDescripcion(String descripcion) {
        descripcionField.clear();
        descripcionField.type(descripcion);
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
