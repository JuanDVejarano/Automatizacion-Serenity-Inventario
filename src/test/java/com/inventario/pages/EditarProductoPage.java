package com.inventario.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/produccion/editar-producto")
public class EditarProductoPage extends PageObject {

    // Verifica que app-editar-producto esté en el DOM antes de llamar ng.getComponent().
    // Sin este guard, ng.getComponent(null) lanza "Expecting instance of DOM Element"
    // durante el lazy-load de Angular cuando el componente aún no se ha renderizado.
    private static final String GUARD =
        "var _el=document.querySelector('app-editar-producto');" +
        "if(!_el)return null;" +
        "var _c=ng.getComponent(_el);" +
        "if(!_c)return null;";

    // ── Espera de carga ──────────────────────────────────────────────────────

    public void waitForFormToLoad() {
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object loaded = evaluateJavascript(GUARD + "return !_c.cargando();");
                if (Boolean.TRUE.equals(loaded)) return;
            } catch (Exception ignored) { /* componente aún no montado */ }
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    // ── Helper: primer producto disponible ───────────────────────────────────

    private int getFirstProductId() {
        Object id = evaluateJavascript(
            GUARD +
            "var prods=_c.productos();" +
            "return prods.length>0?prods[0].id:null;");
        return id instanceof Long ? ((Long) id).intValue() : -1;
    }

    // ── Panel: Editar información ────────────────────────────────────────────

    public void selectFirstProductForEdit() {
        int id = getFirstProductId();
        if (id < 0) throw new IllegalStateException("No hay productos disponibles para editar");
        evaluateJavascript(GUARD + "_c.onEditProductoChange(" + id + ");");
    }

    public void setEditNombre(String nombre) {
        evaluateJavascript(GUARD + "_c.editNombre.set('" + nombre + "');");
    }

    public void clearEditNombre() {
        evaluateJavascript(GUARD + "_c.editNombre.set('');");
    }

    public void setEditPrecio(double precio) {
        evaluateJavascript(GUARD + "_c.editPrecio.set(" + precio + ");");
    }

    public void saveEdicion() {
        evaluateJavascript(GUARD + "_c.guardarEdicion();");
    }

    // ── Panel: Actualizar stock ──────────────────────────────────────────────

    public void selectFirstProductForStock() {
        int id = getFirstProductId();
        if (id < 0) throw new IllegalStateException("No hay productos disponibles para stock");
        evaluateJavascript(GUARD + "_c.onStockProductoChange(" + id + ");");
    }

    public void setCantidad(int cantidad) {
        evaluateJavascript(GUARD + "_c.cantidad.set(" + cantidad + ");");
    }

    public void saveStock() {
        evaluateJavascript(GUARD + "_c.actualizarStock();");
    }

    // ── Barcode: identificar sin cámara (private en TS, público en JS runtime) ──

    public void identifyFirstProductByBarcode() {
        int id = getFirstProductId();
        if (id < 0) throw new IllegalStateException("No hay productos para identificar por barcode");
        evaluateJavascript(GUARD + "_c.identificarProducto('" + id + "');");
    }

    public void identifyInvalidBarcode() {
        evaluateJavascript(GUARD + "_c.identificarProducto('99999999');");
    }

    // ── Leer mensajes (evita conflictos de dos .alert-error en la misma página) ─

    public String waitForEditSuccess()  { return waitForSignal("editSuccess");  }
    public String waitForEditError()    { return waitForSignal("editError");    }
    public String waitForStockSuccess() { return waitForSignal("stockSuccess"); }
    public String waitForStockError()   { return waitForSignal("stockError");   }
    public String waitForBarcodeError() { return waitForSignal("errorEscaneo"); }

    private String waitForSignal(String signalName) {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object msg = evaluateJavascript(GUARD + "return _c." + signalName + "();");
                if (msg instanceof String && !((String) msg).isEmpty()) return (String) msg;
            } catch (Exception ignored) { /* ignorar si el componente no está montado */ }
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
        return "";
    }
}
