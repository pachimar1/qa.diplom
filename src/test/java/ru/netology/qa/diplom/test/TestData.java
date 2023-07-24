package ru.netology.qa.diplom.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.netology.qa.diplom.data.DataSQL;

public class TestData {
    final String approved = "APPROVED";
    final String declined = "DECLINED";
    final int expiryYears = 5;
    final int cardOwnerMaxLength = 27;
    final int cardNumberMaxLength = 16;

    final String ordersTable = "order_entity";
    final String address = "http://localhost:8080";

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
        DataSQL.cleanDatabase();
    }
}
