package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;

@DefaultUrl("http://localhost:4200/caja/historial")
public class HistorialCajaPage extends PageObject {

    private static final String GUARD =
        "var _el=document.querySelector('app-historial-caja');" +
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

    // ── Conteos ──────────────────────────────────────────────────────────────

    public int getTotalCount() {
        Object n = evaluateJavascript(GUARD + "return _c.movimientos().length;");
        return n instanceof Long ? ((Long) n).intValue() : 0;
    }

    public int getFilteredCount() {
        Object n = evaluateJavascript(GUARD + "return _c.movimientosFiltrados().length;");
        return n instanceof Long ? ((Long) n).intValue() : 0;
    }

    // ── Filtros ──────────────────────────────────────────────────────────────

    public void setFiltroTipoNonExistent() {
        // ID 9999 nunca existe → filtrado vacío
        evaluateJavascript(GUARD + "_c.filtroTipo.set(9999);");
    }

    public void setFiltroOperacion(String operacion) {
        evaluateJavascript(GUARD + "_c.filtroOperacion.set('" + operacion + "');");
    }

    public void setFiltroFechaInicio(String fecha) {
        evaluateJavascript(GUARD + "_c.filtroFechaInicio.set('" + fecha + "');");
    }

    public void setFiltroFechaFin(String fecha) {
        evaluateJavascript(GUARD + "_c.filtroFechaFin.set('" + fecha + "');");
    }

    public void limpiarFiltros() {
        evaluateJavascript(GUARD + "_c.limpiarFiltros();");
    }

    // ── Mensaje de lista ─────────────────────────────────────────────────────

    public String getMensajeLista() {
        Object msg = evaluateJavascript(GUARD + "return _c.mensajeLista();");
        return msg instanceof String ? (String) msg : "";
    }

    // ── Selección de movimientos con referencia ──────────────────────────────

    public boolean hasMovimientoWithVenta() {
        Object found = evaluateJavascript(
            GUARD + "return _c.movimientos().some(function(m){return m.fkVenta!=null;});");
        return Boolean.TRUE.equals(found);
    }

    public boolean hasMovimientoWithOrden() {
        Object found = evaluateJavascript(
            GUARD + "return _c.movimientos().some(function(m){return m.fkOrdenCompra!=null;});");
        return Boolean.TRUE.equals(found);
    }

    public void selectFirstMovimientoWithVenta() {
        evaluateJavascript(
            GUARD +
            "var m=_c.movimientos().find(function(m){return m.fkVenta!=null;});" +
            "if(m){ _c.seleccionar(m); }");
        waitForDetalle();
    }

    public void selectFirstMovimientoWithOrden() {
        evaluateJavascript(
            GUARD +
            "var m=_c.movimientos().find(function(m){return m.fkOrdenCompra!=null;});" +
            "if(m){ _c.seleccionar(m); }");
        waitForDetalle();
    }

    private void waitForDetalle() {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object loaded = evaluateJavascript(
                    GUARD + "return _c.detalle()!==null && !_c.cargandoDetalle();");
                if (Boolean.TRUE.equals(loaded)) return;
            } catch (Exception ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    public String getDetalleTipo() {
        Object tipo = evaluateJavascript(GUARD + "return _c.detalle()?.tipo ?? '';");
        return tipo instanceof String ? (String) tipo : "";
    }

    public boolean isDetalleVentaVisible() {
        Object ok = evaluateJavascript(
            GUARD + "var d=_c.detalle(); return d!==null && d.tipo==='venta' && d.detalle!==null;");
        return Boolean.TRUE.equals(ok);
    }

    public boolean isDetalleOrdenVisible() {
        Object ok = evaluateJavascript(
            GUARD + "var d=_c.detalle(); return d!==null && d.tipo==='orden_compra' && d.detalle!==null;");
        return Boolean.TRUE.equals(ok);
    }

    // ── Simulación lista vacía ───────────────────────────────────────────────

    public void simulateEmptyHistory() {
        evaluateJavascript(
            GUARD +
            "_c.movimientos.set([]);" +
            "_c.cargando.set(false);");
    }
}
