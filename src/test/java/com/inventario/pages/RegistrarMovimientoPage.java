package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;

@DefaultUrl("http://localhost:4200/caja/movimiento")
public class RegistrarMovimientoPage extends PageObject {

    private static final String GUARD =
        "var _el=document.querySelector('app-registrar-movimiento');" +
        "if(!_el)return null;" +
        "var _c=ng.getComponent(_el);" +
        "if(!_c)return null;";

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

    // ── Interacción con el formulario reactivo ───────────────────────────────
    // El form usa ReactiveFormsModule (formControlName), no ngModel.
    // patchValue() actualiza los controles y Angular detecta el cambio.

    public void selectTipoByName(String nombre) {
        // Busca el tipo por nombre normalizado (sin tildes) para compatibilidad con JS
        evaluateJavascript(
            GUARD +
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'');}" +
            "var target=norm('" + nombre + "');" +
            "var tipo=_c.tipos().find(function(t){return norm(t.nombre)===target;});" +
            "if(tipo){ _c.form.patchValue({fkTipoMovimiento: tipo.id}); }");
    }

    public void fillValor(double valor) {
        evaluateJavascript(GUARD + "_c.form.patchValue({valor: " + valor + "});");
    }

    public void submit() {
        evaluateJavascript(GUARD + "_c.submit();");
    }

    // ── Consultas de tipos disponibles ──────────────────────────────────────

    public int getTipoCount() {
        Object count = evaluateJavascript(GUARD + "return _c.tipos().length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    public boolean hasTipoByName(String nombre) {
        Object found = evaluateJavascript(
            GUARD +
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'');}" +
            "var target=norm('" + nombre + "');" +
            "return !!_c.tipos().find(function(t){return norm(t.nombre)===target;});");
        return Boolean.TRUE.equals(found);
    }

    // Simula el filtrado de tipos que vería un usuario Tesorería (excluye admin-only)
    public void simulateTesoreriaFilter() {
        evaluateJavascript(
            GUARD +
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'');}" +
            "var adminTipos=['Inyeccion de capital','Deduccion de saldo'];" +
            "var filtrados=_c.tipos().filter(function(t){" +
            "  return !adminTipos.includes(norm(t.nombre));" +
            "});" +
            "_c.tipos.set(filtrados);");
    }

    // ── Mensajes ─────────────────────────────────────────────────────────────

    public String waitForExito() {
        return waitForSignal("exito");
    }

    public String waitForErrorMsg() {
        return waitForSignal("errorMsg");
    }

    public String waitForValorError() {
        return waitForSignal("valorError");
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
