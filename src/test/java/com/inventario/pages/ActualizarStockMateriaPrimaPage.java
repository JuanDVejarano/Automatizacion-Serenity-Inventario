package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:4200/produccion/actualizar-stock-materia-prima")
public class ActualizarStockMateriaPrimaPage extends PageObject {

    private static final String GUARD =
        "var _el=document.querySelector('app-actualizar-stock-materia-prima');" +
        "if(!_el)return null;" +
        "var _c=ng.getComponent(_el);" +
        "if(!_c)return null;";

    @FindBy(css = ".alert.alert-error")   private WebElementFacade errorAlert;
    @FindBy(css = ".alert.alert-success") private WebElementFacade successAlert;

    // ── Espera de carga ─────────────────────────────────────────────────────

    public void waitForFormToLoad() {
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object loaded = evaluateJavascript(GUARD + "return !_c.cargando();");
                if (Boolean.TRUE.equals(loaded)) return;
            } catch (Exception ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    // ── Selección de materia prima ───────────────────────────────────────────
    // materiaSelector usa [ngValue]="m.id" (número) → onMateriaChange vía ng.getComponent

    public void selectFirstMateria() {
        evaluateJavascript(
            GUARD +
            "var mats=_c.materias();" +
            "if(mats.length>0){ _c.onMateriaChange(mats[0].id); }");
    }

    // ── Operación y cantidad ─────────────────────────────────────────────────

    public void setOperacion(String operacion) {
        // operacion usa value="aumentar"|"reducir" (string, no [ngValue])
        evaluateJavascript(GUARD + "_c.operacion.set('" + operacion + "');");
    }

    public void fillCantidad(int cantidad) {
        evaluateJavascript(GUARD + "_c.cantidad.set(" + cantidad + ");");
    }

    public void actualizar() {
        evaluateJavascript(GUARD + "_c.actualizar();");
    }

    public int getCurrentStock() {
        Object stock = evaluateJavascript(GUARD + "var sel=_c.materiaSel(); return sel ? sel.stock : 0;");
        return stock instanceof Long ? ((Long) stock).intValue() : 0;
    }

    // ── Mensajes ─────────────────────────────────────────────────────────────
    // successMsg no se borra tras el éxito → waitFor(DOM) funciona directamente

    public String getSuccessMessage() {
        waitFor(successAlert);
        return successAlert.getText().trim();
    }

    public String getErrorMessage() {
        return waitForSignal("errorMsg");
    }

    private String waitForSignal(String signal) {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object msg = evaluateJavascript(GUARD + "return _c." + signal + "();");
                if (msg instanceof String && !((String) msg).isEmpty()) return (String) msg;
            } catch (Exception ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
        return "";
    }
}
