package com.inventario.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

public class UnauthorizedPage extends PageObject {

    @FindBy(css = ".unauthorized-card p")
    private WebElementFacade accessDeniedMessage;

    public boolean isAccessDeniedMessageVisible() {
        return accessDeniedMessage.isPresent() && accessDeniedMessage.isCurrentlyVisible();
    }

    public String getAccessDeniedMessage() {
        waitFor(accessDeniedMessage);
        return accessDeniedMessage.getText().trim();
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public void waitForRedirect() {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            if (getDriver().getCurrentUrl().contains("/unauthorized")) return;
            try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        }
    }
}
