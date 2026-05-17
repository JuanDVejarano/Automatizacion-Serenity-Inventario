package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/produccion/registrar-producto")
public class RegistrarProductoPage extends PageObject {

    @FindBy(id = "nombre")          private WebElementFacade nombreField;
    @FindBy(id = "caracteristicas") private WebElementFacade caracteristicasField;

    @FindBy(css = "button.btn-primary")   private WebElementFacade submitButton;
    @FindBy(css = ".alert.alert-error")   private WebElementFacade errorAlert;
    @FindBy(css = ".alert.alert-success") private WebElementFacade successAlert;
    @FindBy(css = ".alert.alert-warning") private WebElementFacade warningAlert;

    // Espera a que el formulario cargue y los tipos de producto estén disponibles
    public void waitForFormToLoad() {
        waitFor(submitButton);
        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            Object loaded = evaluateJavascript(
                "var comp = ng.getComponent(document.querySelector('app-registrar-producto'));" +
                "return comp !== null && !comp.cargandoTipos();");
            if (Boolean.TRUE.equals(loaded)) return;
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    public void fillNombre(String nombre) {
        nombreField.clear();
        nombreField.type(nombre);
    }

    // #fkTipo usa [ngValue]="tipo.id" (número) → ng.getComponent() como en crear-usuario
    public void selectFirstTipo() {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-producto'));" +
            "var tipos = comp.tiposProducto();" +
            "if(tipos.length > 0){ comp.fkTipo.set(tipos[0].id); }");
    }

    // #precio: JS + dispatchEvent para que Angular actualice el signal vía (ngModelChange)
    public void fillPrecio(String value) {
        evaluateJavascript(
            "var el = document.getElementById('precio');" +
            "el.value='" + value + "';" +
            "el.dispatchEvent(new Event('input',{bubbles:true}));");
    }

    public void fillCaracteristicas(String value) {
        caracteristicasField.clear();
        caracteristicasField.type(value);
    }

    public void submit() {
        submitButton.click();
    }

    // Simula que no hay tipos disponibles → muestra la advertencia y deshabilita el botón
    public void simulateNoTipos() {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-producto'));" +
            "comp.tiposProducto.set([]);" +
            "comp.cargandoTipos.set(false);");
    }

    public boolean isWarningVisible() {
        return warningAlert.isPresent() && warningAlert.isCurrentlyVisible();
    }

    public boolean isSubmitButtonDisabled() {
        return Boolean.TRUE.equals(
            evaluateJavascript("return document.querySelector('button.btn-primary').disabled;"));
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
