package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/ventas/registrar")
public class RegistrarVentaPage extends PageObject {

    // ── Paso 1: selección de cliente ─────────────────────────────────────────

    @FindBy(css = "input.search-input")
    private WebElementFacade clientSearchInput;

    @FindBy(css = "li.cliente-item")
    private WebElementFacade firstClientItem;

    // ── Paso 2: búsqueda de productos y carrito ──────────────────────────────

    // Primer input (type text) = búsqueda por nombre; el segundo (type number) = código
    @FindBy(css = "input.input-field[type='text']")
    private WebElementFacade productNameInput;

    // Primer btn-search corresponde al buscador por nombre
    @FindBy(css = "button.btn-search")
    private WebElementFacade searchButton;

    @FindBy(css = "button.btn-agregar")
    private WebElementFacade firstAddButton;

    @FindBy(css = "button.btn-confirmar")
    private WebElementFacade confirmButton;

    // Errores de búsqueda / stock insuficiente
    @FindBy(css = "p.error-hint")
    private WebElementFacade errorHint;

    // Error de carrito vacío al intentar confirmar
    @FindBy(css = "div.alert-error")
    private WebElementFacade generalError;

    // ── Paso 1 ───────────────────────────────────────────────────────────────

    // Espera que la lista de clientes cargue del backend (li.cliente-item aparece)
    public void waitForClientList() {
        waitFor(firstClientItem);
    }

    // Hace clic en el primer cliente visible via JS — el click() de Serenity no
    // dispara el binding Angular (click)="seleccionarCliente(c)" en este elemento
    public void selectFirstClient() {
        waitFor(firstClientItem); // espera que la lista cargue del backend
        evaluateJavascript("document.querySelector('li.cliente-item').click()");
        // Tras seleccionar, paso cambia a 2 → esperar btn-confirmar como señal
        waitFor(confirmButton);
    }

    // ── Paso 2 ───────────────────────────────────────────────────────────────

    public void searchAndAddFirstProduct(String searchTerm) {
        waitFor(productNameInput);
        productNameInput.clear();
        productNameInput.type(searchTerm);
        searchButton.click();
        // Esperar que aparezca al menos un resultado
        waitFor(firstAddButton);
        firstAddButton.click();
    }

    public void clickConfirm() {
        confirmButton.click();
    }

    // El botón tiene [disabled]="guardando() || items().length === 0"
    // El estado disabled ES la respuesta del sistema cuando el carrito está vacío
    public boolean isConfirmButtonDisabled() {
        return Boolean.TRUE.equals(
            evaluateJavascript(
                "return document.querySelector('button.btn-confirmar').disabled;"));
    }

    // Simula agregar un producto con stock=0 vía ng.getComponent().
    // Dispara "Stock insuficiente" sin necesitar un producto real sin stock.
    public void simulateAddOutOfStockProduct() {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-venta'));" +
            "comp.agregarAlCarrito({id:99999,nombre:'Sin Stock',precio:100,stock:0,fkTipo:1});");
    }

    public String getErrorHintMessage() {
        waitFor(errorHint);
        return errorHint.getText().trim();
    }

    public String getGeneralErrorMessage() {
        waitFor(generalError);
        return generalError.getText().trim();
    }
}
