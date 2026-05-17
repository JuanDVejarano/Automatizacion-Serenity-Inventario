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

    @FindBy(css = "button.btn-accion--cancel")
    private WebElementFacade firstCancelButton;

    @FindBy(css = "button.btn-accion--detail")
    private WebElementFacade firstDetailButton;

    // Botón que avanza al SIGUIENTE estado ("En reparto" desde "A la espera de reparto")
    @FindBy(css = "button.btn-accion--next")
    private WebElementFacade firstNextButton;

    @FindBy(css = ".estado-vacio p")
    private WebElementFacade estadoVacioMessage;

    @FindBy(css = ".modal-overlay .modal")
    private WebElementFacade detailModal;

    // ── Navegación y esperas ─────────────────────────────────────────────────

    public void waitForPage() {
        waitFor(nuevaVentaButton);
    }

    public void waitForCancelableList() {
        waitFor(firstCancelButton);
    }

    // Espera que la lista tenga ventas (btn-accion--detail aparece por cada venta)
    public void waitForListWithSales() {
        waitFor(firstDetailButton);
    }

    // ── HU-13: cancelar la primera venta cancelable ──────────────────────────

    public void searchByClientCedula(String cedula) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(cedula);
    }

    public void clickFirstCancelButton() {
        waitFor(firstCancelButton);
        // JS click para asegurar que Angular dispara cambiarEstado(v, 'Cancelada')
        evaluateJavascript("document.querySelector('button.btn-accion--cancel').click()");
    }

    // ── HU-15: columnas, filtros y detalle ───────────────────────────────────

    public boolean hasRequiredColumns() {
        return Boolean.TRUE.equals(evaluateJavascript(
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'').toLowerCase();}" +
            "var ths = Array.from(document.querySelectorAll('table.tabla thead th'));" +
            "var texts = ths.map(function(th){return norm(th.textContent.trim());});" +
            "var required = ['id','fecha','cliente','valor','estado'];" +
            "return required.every(function(r){return texts.some(function(t){return t.includes(r);});});"));
    }

    public int getVisibleSaleCount() {
        Object count = evaluateJavascript(
            "return document.querySelectorAll('table.tabla tbody tr').length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    // Filtra por estado — el select usa [value]="e.nombre" (string, no ngValue)
    public void filterByEstado(String estado) {
        evaluateJavascript(
            "var el = document.getElementById('filtroEstado');" +
            "el.value = '" + estado + "';" +
            "el.dispatchEvent(new Event('change', {bubbles:true}));");
        waitFor(nuevaVentaButton); // page still valid
        evaluateJavascript("document.querySelector('button.btn-aplicar').click()");
        waitFor(nuevaVentaButton);
    }

    // Filtra por un rango de fechas en el año 2099 — garantiza "sin resultados"
    public void filterByFutureDateRange() {
        evaluateJavascript(
            "var fi = document.getElementById('fechaInicio');" +
            "fi.value='2099-01-01'; fi.dispatchEvent(new Event('input',{bubbles:true}));" +
            "fi.dispatchEvent(new Event('change',{bubbles:true}));");
        evaluateJavascript(
            "var ff = document.getElementById('fechaFin');" +
            "ff.value='2099-12-31'; ff.dispatchEvent(new Event('input',{bubbles:true}));" +
            "ff.dispatchEvent(new Event('change',{bubbles:true}));");
        evaluateJavascript("document.querySelector('button.btn-aplicar').click()");
        waitFor(nuevaVentaButton);
    }

    public void searchByClient(String text) {
        waitFor(searchInput);
        searchInput.clear();
        searchInput.type(text);
    }

    public void clickFirstDetailButton() {
        waitFor(firstDetailButton);
        evaluateJavascript("document.querySelector('button.btn-accion--detail').click()");
    }

    public boolean isDetailModalVisible() {
        return detailModal.isPresent() && detailModal.isCurrentlyVisible();
    }

    // Cambia al próximo estado disponible (En reparto / Completada según el estado actual)
    public void clickFirstNextStateButton() {
        waitFor(firstNextButton);
        evaluateJavascript("document.querySelector('button.btn-accion--next').click()");
    }

    public String getEstadoVacioMessage() {
        waitFor(estadoVacioMessage);
        return estadoVacioMessage.getText().trim();
    }

    public String getSuccessMessage() {
        waitFor(successAlert);
        return successAlert.getText().trim();
    }
}
