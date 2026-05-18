package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:4200/produccion/gestionar-ordenes")
public class GestionarOrdenesPage extends PageObject {

    private static final String GUARD =
        "var _el=document.querySelector('app-gestionar-ordenes');" +
        "if(!_el)return null;" +
        "var _c=ng.getComponent(_el);" +
        "if(!_c)return null;";

    @FindBy(css = ".empty-state") private WebElementFacade emptyState;

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

    private void waitForDetallLoaded() {
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

    // ── Creación de orden de prueba via XHR síncrono ─────────────────────────
    // POST /ordenes-compra con los campos mínimos requeridos (fkEstado=1=Pendiente)

    public void createTestPendingOrder() {
        evaluateJavascript(
            "var token=localStorage.getItem('access_token');" +
            "var xhr=new XMLHttpRequest();" +
            "xhr.open('POST','http://localhost:3000/ordenes-compra',false);" +
            "xhr.setRequestHeader('Authorization','Bearer '+token);" +
            "xhr.setRequestHeader('Content-Type','application/json');" +
            "xhr.send(JSON.stringify({fkEstado:1,costoTotal:1000,fkUsuario:1}));");
        // Recargar la página para que ngOnInit vuelva a obtener la lista
        open();
        waitForFormToLoad();
    }

    // ── Conteos ──────────────────────────────────────────────────────────────

    public int getTotalOrderCount() {
        Object count = evaluateJavascript(GUARD + "return _c.ordenes().length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    public int getFilteredOrderCount() {
        Object count = evaluateJavascript(GUARD + "return _c.ordenesFiltradas().length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    // ── Filtros ──────────────────────────────────────────────────────────────

    public void setFiltroEstado(String estado) {
        evaluateJavascript(GUARD + "_c.filtroEstado.set('" + estado + "');");
    }

    public void setFechaHasta(String fecha) {
        evaluateJavascript(GUARD + "_c.fechaHasta.set('" + fecha + "');");
    }

    public void setFechaDesde(String fecha) {
        evaluateJavascript(GUARD + "_c.fechaDesde.set('" + fecha + "');");
    }

    // ── Selección de orden ───────────────────────────────────────────────────

    public void selectFirstFilteredOrder() {
        evaluateJavascript(
            GUARD +
            "var list=_c.ordenesFiltradas();" +
            "if(list.length>0){ _c.seleccionar(list[0]); }");
        waitForDetallLoaded();
    }

    public void selectFirstPendingOrder() {
        evaluateJavascript(GUARD + "_c.filtroEstado.set('Pendiente');");
        // Pequeña espera para que el computed recalcule
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        evaluateJavascript(
            GUARD +
            "var list=_c.ordenesFiltradas();" +
            "if(list.length>0){ _c.seleccionar(list[0]); }");
        waitForDetallLoaded();
    }

    // ── Cambio de estado ─────────────────────────────────────────────────────

    public void clickCompletarOrden() {
        evaluateJavascript(GUARD + "_c.cambiarEstado('Completada');");
    }

    public void clickCancelarOrden() {
        evaluateJavascript(GUARD + "_c.cambiarEstado('Cancelada');");
    }

    // ── Mensajes y estado ────────────────────────────────────────────────────

    public String waitForEstadoSuccess() {
        return waitForSignal("estadoSuccess");
    }

    public String getMensajeLista() {
        Object msg = evaluateJavascript(GUARD + "return _c.mensajeLista();");
        return msg instanceof String ? (String) msg : "";
    }

    public boolean isDetalleVisible() {
        Object vis = evaluateJavascript(GUARD + "return _c.detalle()!==null;");
        return Boolean.TRUE.equals(vis);
    }

    private String waitForSignal(String signal) {
        long deadline = System.currentTimeMillis() + 10_000;
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
