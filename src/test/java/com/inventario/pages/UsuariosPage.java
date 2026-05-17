package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;


@DefaultUrl("http://localhost:4200/rrhh/usuarios")
public class UsuariosPage extends PageObject {

    @FindBy(css = "button.btn-editar")
    private WebElementFacade firstEditButton;

    @FindBy(css = "button.btn-guardar")
    private WebElementFacade saveButton;

    @FindBy(css = ".modal-alert.modal-alert--error")
    private WebElementFacade modalErrorAlert;

    @FindBy(css = ".modal-alert.modal-alert--success")
    private WebElementFacade modalSuccessAlert;

    public void waitForList() {
        waitFor(firstEditButton);
    }

    public void clickFirstUserEditButton() {
        waitFor(firstEditButton);
        firstEditButton.click();
    }

    // Hace clic en el botón editar de la fila que contiene ese username exacto.
    // Evita tocar el admin u otros usuarios que puedan aparecer antes en la tabla.
    public void clickEditButtonForUser(String username) {
        waitFor(firstEditButton); // espera que la lista esté cargada
        evaluateJavascript(
            "var rows = Array.from(document.querySelectorAll('table.tabla tbody tr'));" +
            "var row = rows.find(function(r){" +
            "  var cell = r.querySelector('.cell-usuario');" +
            "  return cell && cell.textContent.trim().includes('" + username + "');" +
            "});" +
            "if(row){ var btn = row.querySelector('button.btn-editar'); if(btn) btn.click(); }");
    }

    public void waitForModal() {
        waitFor(saveButton);
    }

    // [ngValue]="rol.idRol" → mismo problema que crear-usuario → ng.getComponent directo
    public void selectDifferentRole() {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-usuarios'));" +
            "var currentRol = comp.usuarioEditando().fkRol;" +
            "var otro = comp.roles().find(function(r){ return r.idRol !== currentRol; });" +
            "if(otro){ comp.rolSeleccionado.set(otro.idRol); }");
    }

    public void clearRoleSelection() {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-usuarios'));" +
            "comp.rolSeleccionado.set(null);");
    }

    public void submitRoleAssignment() {
        saveButton.click();
    }

    // Hace clic en el btn-toggle de la fila que contiene ese username
    public void clickToggleForUser(String username) {
        evaluateJavascript(
            "var rows = Array.from(document.querySelectorAll('table.tabla tbody tr'));" +
            "var row = rows.find(function(r){" +
            "  return r.querySelector('.cell-usuario')?.textContent.trim().includes('" + username + "');" +
            "});" +
            "if(row){ var btn = row.querySelector('button.btn-toggle'); if(btn) btn.click(); }");
    }

    // Espera hasta 8 segundos que el badge del usuario muestre la clase esperada.
    // El toggle es asíncrono (PATCH al backend), por eso se necesita polling.
    public boolean waitForUserBadge(String username, String expectedBadgeClass) {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            Object found = evaluateJavascript(
                "var rows = Array.from(document.querySelectorAll('table.tabla tbody tr'));" +
                "var row = rows.find(function(r){" +
                "  return r.querySelector('.cell-usuario')?.textContent.trim().includes('" + username + "');" +
                "});" +
                "var badge = row?.querySelector('.badge');" +
                "return badge ? badge.classList.contains('" + expectedBadgeClass + "') : false;");
            if (Boolean.TRUE.equals(found)) return true;
            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        return false;
    }

    public String getModalSuccessMessage() {
        waitFor(modalSuccessAlert);
        return modalSuccessAlert.getText().trim();
    }

    public String getModalErrorMessage() {
        waitFor(modalErrorAlert);
        return modalErrorAlert.getText().trim();
    }
}
