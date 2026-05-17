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

    // ── HU-14: detalle de venta ──────────────────────────────────────────────

    // Busca por nombre, obtiene el ID del primer resultado y lo agrega via barcode.
    // Necesario para testear la ruta de barcode sin conocer IDs de productos a priori.
    public void addFirstProductByBarcode(String searchTerm) {
        // Paso 1: búsqueda por nombre para obtener un ID de producto real
        waitFor(productNameInput);
        productNameInput.clear();
        productNameInput.type(searchTerm);
        searchButton.click();
        waitFor(firstAddButton);

        // Paso 2: leer el ID del primer resultado
        Object id = evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-venta'));" +
            "var r = comp.productosResultados();" +
            "return r.length > 0 ? r[0].id : null;");

        if (!(id instanceof Long) || (Long) id <= 0) {
            throw new IllegalStateException("No hay productos disponibles para la busqueda: " + searchTerm);
        }
        int productId = ((Long) id).intValue();

        // Paso 3: limpiar resultados de nombre y buscar por "código de barras" (ID)
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-venta'));" +
            "comp.productosResultados.set([]);" +
            "comp.busquedaProducto.set('');" +
            "comp.barcodeInput.set('" + productId + "');" +
            "comp.buscarPorBarcode();");

        // Esperar que el producto aparezca en el carrito
        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            Object count = evaluateJavascript(
                "return document.querySelectorAll('.carrito-table tbody tr').length;");
            if (count instanceof Long && (Long) count > 0) return;
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    public void searchInvalidBarcode() {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-venta'));" +
            "comp.barcodeInput.set('999999999');" +
            "comp.buscarPorBarcode();");
    }

    public double getTotal() {
        Object t = evaluateJavascript(
            "return ng.getComponent(document.querySelector('app-registrar-venta')).total();");
        if (t instanceof Long) return ((Long) t).doubleValue();
        if (t instanceof Double) return (Double) t;
        return 0;
    }

    public int getCartItemCount() {
        Object count = evaluateJavascript(
            "return document.querySelectorAll('.carrito-table tbody tr').length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    // Modifica la cantidad del primer item del carrito via el método del componente
    public void updateFirstItemQuantity(int newQty) {
        evaluateJavascript(
            "ng.getComponent(document.querySelector('app-registrar-venta'))" +
            ".actualizarCantidad(0, " + newQty + ");");
    }

    public int getFirstItemQuantity() {
        Object qty = evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-registrar-venta'));" +
            "var items = comp.items();" +
            "return items.length > 0 ? items[0].cantidad : 0;");
        return qty instanceof Long ? ((Long) qty).intValue() : 0;
    }

    public void removeFirstItem() {
        evaluateJavascript(
            "ng.getComponent(document.querySelector('app-registrar-venta'))" +
            ".eliminarItem(0);");
    }

    public boolean isCartEmpty() {
        return Boolean.TRUE.equals(
            evaluateJavascript("return document.querySelector('.carrito-vacio') !== null;"));
    }
}
