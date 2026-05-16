package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/rrhh/crear-usuario")
public class CrearUsuarioPage extends PageObject {

    @FindBy(id = "usuario")    private WebElementFacade usuarioField;
    @FindBy(id = "clave")      private WebElementFacade claveField;
    @FindBy(id = "fkRol")      private WebElementFacade rolSelect;
    @FindBy(id = "fkEmpleado") private WebElementFacade empleadoSelect;

    @FindBy(css = "button.btn-primary")   private WebElementFacade submitButton;
    @FindBy(css = ".alert.alert-error")   private WebElementFacade errorAlert;
    @FindBy(css = ".alert.alert-success") private WebElementFacade successAlert;

    // El formulario aparece dentro de @else de @if(cargando()) — esperar el botón
    public void waitForFormToLoad() {
        waitFor(submitButton);
    }

    public void fillUsuario(String username) {
        usuarioField.clear();
        usuarioField.type(username);
    }

    public void fillClave(String password) {
        claveField.clear();
        claveField.type(password);
    }

    // Con [ngValue]="rol.idRol" Angular usa un mapa interno entre DOM y modelo.
    // Setear el.value + dispatchEvent no traduce bien el valor al signal.
    // ng.getComponent() accede directamente al signal → solución fiable.

    public void selectFirstRole() {
        // Angular 17 signals: .set() programa el re-render automaticamente
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-crear-usuario'));" +
            "var roles = comp.roles();" +
            "if(roles.length > 0){ comp.fkRol.set(roles[0].idRol); }");
    }

    public void selectRoleByName(String roleName) {
        evaluateJavascript(
            "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'').toLowerCase();}" +
            "var comp = ng.getComponent(document.querySelector('app-crear-usuario'));" +
            "var rol = comp.roles().find(function(r){ return norm(r.nombreRol).includes(norm('" + roleName + "')); });" +
            "if(rol){ comp.fkRol.set(rol.idRol); }");
    }

    public void selectFirstEmployee() {
        Object count = evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-crear-usuario'));" +
            "var emps = comp.empleados();" +
            "if(emps.length > 0){ comp.fkEmpleado.set(emps[0].cedula); }" +
            "return emps.length;");
        if (count instanceof Long && (Long) count == 0) {
            throw new IllegalStateException(
                "No hay empleados activos en el dropdown de crear-usuario. " +
                "Verifica que el empleado fue creado antes de navegar al formulario.");
        }
    }

    public void selectEmployeeByCedula(String cedula) {
        evaluateJavascript(
            "var comp = ng.getComponent(document.querySelector('app-crear-usuario'));" +
            "var emp = comp.empleados().find(function(e){ return e.cedula.toString() === '" + cedula + "'; });" +
            "if(emp){ comp.fkEmpleado.set(emp.cedula); }");
    }

    public void submit() {
        submitButton.click();
    }

    public String getSuccessMessage() {
        waitFor(successAlert);
        return successAlert.getText().trim();
    }

    public String getErrorMessage() {
        waitFor(errorAlert);
        return errorAlert.getText().trim();
    }
}
