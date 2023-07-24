package ru.netology.qa.diplom.test;

import org.junit.jupiter.api.*;
import ru.netology.qa.diplom.data.DataHelper;
import ru.netology.qa.diplom.data.DataSQL;
import ru.netology.qa.diplom.page.DashboardPage;
import ru.netology.qa.diplom.page.PaymentFormPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentPageTest extends TestData {
    static DashboardPage dashboardPage;
    static PaymentFormPage paymentForm;

    final String paymentsTable = "payment_entity";
    String expectedPrice = "4000000";

    @BeforeEach
    void openHost() {
        open(testHost);

        dashboardPage = new DashboardPage();
        paymentForm = dashboardPage.openPaymentForm();
    }

    @Nested
    class IncreasedTimeout {

        @Test
        @DisplayName("1. Оплата тура картой со статусом APPROVED")
        void shouldApprovePaymentWithValidData() {
            long rowsOrdersBefore = DataSQL.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = DataSQL.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateValidCardData(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);

            assertEquals(rowsOrdersBefore + 1, DataSQL.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, DataSQL.getRowsAmountFrom(paymentsTable));

            DataSQL.Payment lastEntry = DataSQL.getLastEntryFromPaymentsTable();

            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("2. Оплата тура картой со статусом DECLINED")
        void shouldSendFormWithDeclinedCard() {
            long rowsOrdersBefore = DataSQL.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = DataSQL.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkErrorNotification(10);

            assertEquals(rowsOrdersBefore + 1, DataSQL.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, DataSQL.getRowsAmountFrom(paymentsTable));

            DataSQL.Payment lastEntry = DataSQL.getLastEntryFromPaymentsTable();

            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(declined, lastEntry.getStatus());
        }
    }
}

