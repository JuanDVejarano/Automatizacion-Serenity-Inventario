package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/ventas/ventas")
public class ListaVentasPage extends PageObject {

    @FindBy(css = "button.btn-nuevo")
    private WebElementFacade nuevaVentaButton;

    @FindBy(css = "div.alert-success")
    private WebElementFacade successAlert;

    @FindBy(css = "input.search-input")
    private WebElementFacade searchInput;

    // Botón para cancelar una venta (estado "A la espera de reparto" o "En reparto")
    @FindBy(css = "button.btn-accion--cancel")
    private WebElementFacade firstCancelButton;

    public void waitForPage() {
        waitFor(nuevaVentaButton);
    }

    // Espera que aparezca al menos un botón de cancelar (lista cargada con venta cancelable)
    public void waitForCancelableList() {
        waitFor(firstCancelButton);
    }

    public void searchByClientCedula(String cedula) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(cedula);
    }

    public void clickFirstCancelButton() {
        waitFor(firstCancelButton);
        firstCancelButton.click();
    }

    public String getSuccessMessage() {
        waitFor(successAlert);
        return successAlert.getText().trim();
    }
}
