package com.inventario.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:4200/produccion/asociar-proveedor-materia-prima")
public class AsociarProveedorMateriaPrimaPage extends PageObject {

    private static final String GUARD =
        "var _el=document.querySelector('app-asociar-proveedor-materia-prima');" +
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

    // ── Selección de proveedor ───────────────────────────────────────────────
    // proveedorSel usa [ngValue]="p.nit" (BigInt) → onProveedorChange recibe el
    // valor y lo convierte a string internamente

    public void selectFirstProveedor() {
        evaluateJavascript(
            GUARD +
            "var provs=_c.proveedores();" +
            "if(provs.length>0){ _c.onProveedorChange(provs[0].nit); }");
        waitForAssociationsToLoad();
    }

    // ── Selección de materia prima disponible ────────────────────────────────

    public void selectFirstMateriaDisponible() {
        evaluateJavascript(
            GUARD +
            "var mats=_c.materiasDisponibles();" +
            "if(mats.length>0){ _c.nuevaMateriaId.set(mats[0].id); }");
    }

    // ── Costo ────────────────────────────────────────────────────────────────

    public void fillNuevoCosto(double costo) {
        evaluateJavascript(GUARD + "_c.nuevoCosto.set(" + costo + ");");
    }

    // ── Asociar ──────────────────────────────────────────────────────────────

    public void asociar() {
        evaluateJavascript(GUARD + "_c.asociar();");
    }

    public int getAssociationCount() {
        Object count = evaluateJavascript(GUARD + "return _c.asociaciones().length;");
        return count instanceof Long ? ((Long) count).intValue() : 0;
    }

    // Fuerza asociación duplicada (bypasea materiasDisponibles) → provoca 409
    public void forceAssociateDuplicate() {
        evaluateJavascript(
            GUARD +
            "var asocs=_c.asociaciones();" +
            "if(asocs.length>0){" +
            "  _c.nuevaMateriaId.set(asocs[0].fkMateriaPrima);" +
            "  _c.nuevoCosto.set(1000);" +
            "  _c.asociar();" +
            "}");
    }

    // Costo inválido: selecciona primera materia disponible, pone costo=0 y envía
    public void asociarConCostoInvalido() {
        evaluateJavascript(
            GUARD +
            "var mats=_c.materiasDisponibles();" +
            "if(mats.length>0){ _c.nuevaMateriaId.set(mats[0].id); }" +
            "_c.nuevoCosto.set(0);" +
            "_c.asociar();");
    }

    // ── Editar costo ─────────────────────────────────────────────────────────

    public void editFirstAssociation(double newCosto) {
        evaluateJavascript(
            GUARD +
            "var asocs=_c.asociaciones();" +
            "if(asocs.length>0){ _c.iniciarEdicion(asocs[0]); }");
        evaluateJavascript(
            GUARD +
            "var editando=_c.asociaciones().find(function(a){return a.editando;});" +
            "if(editando){ _c.setCostoEdit(editando," + newCosto + "); }");
    }

    public void saveFirstAssociationEdit() {
        evaluateJavascript(
            GUARD +
            "var editando=_c.asociaciones().find(function(a){return a.editando;});" +
            "if(editando){ _c.guardarEdicion(editando); }");
    }

    // ── Eliminar asociación ──────────────────────────────────────────────────

    public void requestDeleteFirstAssociation() {
        evaluateJavascript(
            GUARD +
            "var asocs=_c.asociaciones();" +
            "if(asocs.length>0){ _c.solicitarConfirmacion(asocs[0].id); }");
    }

    public void confirmDeletion() {
        evaluateJavascript(
            GUARD +
            "var id=_c.confirmandoId();" +
            "if(id!==null){ _c.confirmarEliminacion(id); }");
    }

    // ── Simular sin materias ─────────────────────────────────────────────────

    public void simulateNoMaterias() {
        evaluateJavascript(GUARD + "_c.materias.set([]);");
    }

    // ── Mensajes ─────────────────────────────────────────────────────────────

    public String waitForErrorMsg()    { return waitForSignal("errorMsg"); }
    public String waitForEditSuccess() { return waitForSignal("editSuccess"); }

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
