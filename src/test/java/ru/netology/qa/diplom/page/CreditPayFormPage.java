package ru.netology.qa.diplom.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class CreditPayFormPage extends FormPage {
    private SelenideElement heading = $$("h3").findBy(text("Кредит по данным карты"));

    public CreditPayFormPage() {
        heading.shouldBe(visible);
    }
}
