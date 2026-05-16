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
    private static final String VALID_TOKEN = buildToken();

    private static String buildToken() {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String header = enc.encodeToString(
            "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = enc.encodeToString(
            "{\"idUsuario\":1,\"usuario\":\"admin\",\"rol\":\"Administrador\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".fakesignature";
    }

    public void setValidSession() {
        evaluateJavascript("localStorage.setItem('access_token', '" + VALID_TOKEN + "')");
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
}
