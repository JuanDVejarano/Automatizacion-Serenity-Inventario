package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:4200/caja/saldo")
public class SaldoCajaPage extends PageObject {

    private static final String GUARD =
        "var _el=document.querySelector('app-saldo-caja');" +
        "if(!_el)return null;" +
        "var _c=ng.getComponent(_el);" +
        "if(!_c)return null;";

    @FindBy(css = ".saldo-card")       private WebElementFacade saldoCard;
    @FindBy(css = ".alert.alert-warning") private WebElementFacade warningAlert;
    @FindBy(css = ".no-caja p")        private WebElementFacade noCajaMessage;

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

    // ── Consultas de estado ──────────────────────────────────────────────────

    public boolean isSaldoCardVisible() {
        return saldoCard.isPresent() && saldoCard.isCurrentlyVisible();
    }

    public double getCapital() {
        Object val = evaluateJavascript(GUARD + "return _c.capital();");
        if (val instanceof Long)   return ((Long) val).doubleValue();
        if (val instanceof Double) return (Double) val;
        return 0.0;
    }

    public boolean isSinCapitalAlertVisible() {
        return warningAlert.isPresent() && warningAlert.isCurrentlyVisible();
    }

    public boolean isNoCajaVisible() {
        return noCajaMessage.isPresent() && noCajaMessage.isCurrentlyVisible();
    }

    public String getNoCajaMessage() {
        waitFor(noCajaMessage);
        return noCajaMessage.getText().trim();
    }

    // ── Simulaciones ─────────────────────────────────────────────────────────
    // Permiten testear las ramas de UI sin alterar el estado real de la BD

    public void simulateSaldoCero() {
        evaluateJavascript(
            GUARD +
            "var c=_c.caja();" +
            "if(c){ _c.caja.set(Object.assign({},c,{capital:'0'})); }");
    }

    public void simulateNoCaja() {
        evaluateJavascript(
            GUARD +
            "_c.caja.set(null);" +
            "_c.cargando.set(false);");
    }
}
