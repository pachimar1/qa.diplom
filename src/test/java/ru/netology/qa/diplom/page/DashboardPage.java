package ru.netology.qa.diplom.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private SelenideElement buttonPayment = $(byText("Купить"));
    private SelenideElement buttonCredit = $(byText("Купить в кредит"));

    public DashboardPage() {
        buttonPayment.shouldBe(enabled);
        buttonCredit.shouldBe(enabled);
    }

    public PaymentFormPage openPaymentForm() {
        buttonPayment.click();
        return new PaymentFormPage();
    }

    public CreditPayFormPage openCreditForm() {
        buttonCredit.click();
        return new CreditPayFormPage();
    }
}