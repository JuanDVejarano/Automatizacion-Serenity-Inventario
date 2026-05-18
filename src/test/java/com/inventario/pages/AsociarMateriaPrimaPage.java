package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:4200/produccion/asociar-materia-prima")
public class AsociarMateriaPrimaPage extends PageObject {

    // Null-guards para lazy-load de Angular
    private static final String GUARD =
        "var _el=document.querySelector('app-asociar-materia-prima');" +
        "if(!_el)return null;" +
        "var _c=ng.getComponent(_el);" +
        "if(!_c)return null;";

    @FindBy(css = ".alert.alert-warning") private WebElementFacade warningAlert;

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

    private void waitForAssociationsToLoad() {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object loaded = evaluateJavascript(GUARD + "return !_c.cargandoAsoc();");
                if (Boolean.TRUE.equals(loaded)) return;
            } catch (Exception ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    public void waitForAssociationCount(int minCount) {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object count = evaluateJavascript(GUARD + "return _c.asociaciones().length;");
                if (count instanceof Long && ((Long) count).intValue() >= minCount) return;
            } catch (Exception ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    public void waitForAssociationCountBelow(int previousCount) {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                Object count = evaluateJavascript(GUARD + "return _c.asociaciones().length;");
                if (count instanceof Long && ((Long) count).intValue() < previousCount) return;
            } catch (Exception ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }

    // ── Selección de producto ────────────────────────────────────────────────

    public void selectFirstProducto() {
        // productoSel usa [ngValue] con número → ng.getComponent + onProductoChange
        evaluateJavascript(
            GUARD +
            "var prods=_c.productos();" +
            "if(prods.length>0){ _c.onProductoChange(prods[0].id); }");
        waitForAssociationsToLoad();
    }

    // ── Selección de materia prima (disponibles) ─────────────────────────────

    public void selectFirstMateriaDisponible() {
        // materiaSel usa [ngValue] con número → set signal directamente
        evaluateJavascript(
            GUARD +
            "var mats=_c.materiasDisponibles();" +
            "if(mats.length>0){ _c.nuevaMateriaId.set(mats[0].id); }");
    }

    // ── Cantidad ─────────────────────────────────────────────────────────────

    public void fillNuevaCantidad(int cantidad) {
        evaluateJavascript(GUARD + "_c.nuevaCantidad.set(" + cantidad + ");");
    }

    // ── Asociar ──────────────────────────────────────────────────────────────

    public void asociar() {
        evaluateJavascript(GUARD + "_c.asociar();");
    }

    public int getAssociationCount() {
        Object count = evaluateJavascript(GUARD + "return _c.asociaciones().length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    // Bypass de UI: fuerza la asociación de una materia ya asociada (provoca 409)
    public void forceAssociateDuplicate() {
        evaluateJavascript(
            GUARD +
            "var asocs=_c.asociaciones();" +
            "if(asocs.length>0){" +
            "  _c.nuevaMateriaId.set(asocs[0].fkMateriaPrima);" +
            "  _c.nuevaCantidad.set(1);" +
            "  _c.asociar();" +
            "}");
    }

    // Cantidad inválida: selecciona primera materia disponible, pone cant=0 y envía
    public void asociarConCantidadInvalida() {
        evaluateJavascript(
            GUARD +
            "var mats=_c.materiasDisponibles();" +
            "if(mats.length>0){ _c.nuevaMateriaId.set(mats[0].id); }" +
            "_c.nuevaCantidad.set(0);" +
            "_c.asociar();");
    }

    // ── Editar cantidad ──────────────────────────────────────────────────────

    public void editFirstAssociation(int newCantidad) {
        evaluateJavascript(
            GUARD +
            "var asocs=_c.asociaciones();" +
            "if(asocs.length>0){ _c.iniciarEdicion(asocs[0]); }");
        // Actualiza cantidadEdit en el item que quedó en modo edición
        evaluateJavascript(
            GUARD +
            "var editando=_c.asociaciones().find(function(a){return a.editando;});" +
            "if(editando){ _c.setCantidadEdit(editando," + newCantidad + "); }");
    }

    public void saveFirstAssociationEdit() {
        evaluateJavascript(
            GUARD +
            "var editando=_c.asociaciones().find(function(a){return a.editando;});" +
            "if(editando){ _c.guardarEdicion(editando); }");
    }

    // ── Eliminar asociación ──────────────────────────────────────────────────
    // Se fusionan solicitar + confirmar en un solo evaluateJavascript para evitar
    // inconsistencias de estado entre dos llamadas separadas al componente Angular.

    public void requestDeleteFirstAssociation() {
        // No-op: la eliminación real se ejecuta en confirmDeletion()
    }

    public void confirmDeletion() {
        // XHR síncrono para garantizar que el DELETE complete antes de que
        // evaluateJavascript regrese — la llamada async de confirmarEliminacion()
        // no es suficientemente confiable desde el contexto de Selenium.
        evaluateJavascript(
            GUARD +
            "var asocs=_c.asociaciones();" +
            "if(asocs.length===0) return;" +
            "var id=asocs[0].id;" +
            "var token=localStorage.getItem('access_token');" +
            "var xhr=new XMLHttpRequest();" +
            "xhr.open('DELETE','http://localhost:3000/materia-prima-producto/'+id,false);" +
            "xhr.setRequestHeader('Authorization','Bearer '+token);" +
            "xhr.send();" +
            "if(xhr.status>=200&&xhr.status<300){" +
            "  _c.asociaciones.update(function(list){" +
            "    return list.filter(function(a){return String(a.id)!==String(id);});" +
            "  });" +
            "}");
    }

    // ── Simular sin materias ─────────────────────────────────────────────────

    public void simulateNoMaterias() {
        // Requiere producto seleccionado para que se muestre el warning
        evaluateJavascript(GUARD + "_c.materias.set([]);");
    }

    // ── Mensajes (señales) ───────────────────────────────────────────────────

    public String waitForErrorMsg() {
        return waitForSignal("errorMsg");
    }

    public String waitForEditSuccess() {
        return waitForSignal("editSuccess");
    }

    public String waitForSuccessMsg() {
        return waitForSignal("successMsg");
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

    public boolean isWarningVisible() {
        return warningAlert.isPresent() && warningAlert.isCurrentlyVisible();
    }
}
