package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("http://localhost:4200/login")
public class LoginPage extends PageObject {

    @FindBy(id = "usuario")
    private WebElementFacade usernameField;

    @FindBy(id = "clave")
    private WebElementFacade passwordField;

    @FindBy(css = "button.btn-submit")
    private WebElementFacade loginButton;

    @FindBy(css = ".alert.alert--error")
    private WebElementFacade errorAlert;

    public void enterUsername(String username) {
        usernameField.clear();
        usernameField.type(username);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.type(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public boolean isErrorAlertVisible() {
        return errorAlert.isPresent() && errorAlert.isCurrentlyVisible();
    }

    public String getErrorMessage() {
        waitFor(errorAlert);
        return errorAlert.getText().trim();
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
}
