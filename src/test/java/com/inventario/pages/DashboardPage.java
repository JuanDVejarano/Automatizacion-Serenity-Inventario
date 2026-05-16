package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@DefaultUrl("http://localhost:4200/dashboard")
public class DashboardPage extends PageObject {

    @FindBy(css = "button.btn-logout-icon")
    private WebElementFacade logoutButton;

    // exp: 9999999999 = año 2286, siempre vigente
    public void setValidSession() {
        setSessionWithRole("Administrador");
    }

    public void setSessionWithRole(String role) {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String header = enc.encodeToString(
            "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payloadJson = "{\"idUsuario\":1,\"usuario\":\"admin\",\"rol\":\"" + role + "\",\"exp\":9999999999}";
        String payload = enc.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String token = header + "." + payload + ".fakesignature";
        evaluateJavascript("localStorage.setItem('access_token', '" + token + "')");
    }

    public void clearSession() {
        evaluateJavascript("localStorage.removeItem('access_token')");
    }

    public boolean isTokenPresent() {
        return evaluateJavascript("return localStorage.getItem('access_token')") != null;
    }

    public void waitForLogoutButton() {
        waitFor(logoutButton);
    }

    public void clickLogoutButton() {
        logoutButton.click();
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public void navigateToDashboard() {
        getDriver().navigate().to("http://localhost:4200/dashboard");
    }

    public void openDashboardInNewTab() {
        evaluateJavascript("window.open('http://localhost:4200/dashboard', '_blank')");
    }

    public void switchToTab(int index) {
        List<String> handles = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(handles.get(index));
    }

    public void closeCurrentTab() {
        getDriver().close();
    }

    public int getOpenTabCount() {
        return getDriver().getWindowHandles().size();
    }

    public void navigateTo(String path) {
        getDriver().navigate().to("http://localhost:4200" + path);
    }

    // Normaliza tildes con NFD para comparar nombres de módulos feature↔DOM
    private static final String NORM_FN =
        "function norm(s){return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,'').toLowerCase();}";

    public boolean isModuleEnabled(String moduleName) {
        String script = NORM_FN +
            "var t='" + moduleName + "';" +
            "return Array.from(document.querySelectorAll('a.module-card'))" +
            ".some(function(e){var m=e.querySelector('.module-name');" +
            "return m&&norm(m.textContent.trim())===norm(t);});";
        return Boolean.TRUE.equals(evaluateJavascript(script));
    }

    public boolean isModuleDisabled(String moduleName) {
        String script = NORM_FN +
            "var t='" + moduleName + "';" +
            "return Array.from(document.querySelectorAll('.module-card--disabled'))" +
            ".some(function(e){var m=e.querySelector('.module-name');" +
            "return m&&norm(m.textContent.trim())===norm(t);});";
        return Boolean.TRUE.equals(evaluateJavascript(script));
    }

    public boolean hasRolesManagementModule() {
        String script =
            "return Array.from(document.querySelectorAll('.module-name'))" +
            ".some(function(e){return e.textContent.toLowerCase().includes('rol');});";
        return Boolean.TRUE.equals(evaluateJavascript(script));
    }
}
