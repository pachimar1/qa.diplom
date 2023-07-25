package ru.netology.qa.diplom.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
    String expectedPrice = "4500000";

    @BeforeEach
    void openHost() {
        open(address);

        dashboardPage = new DashboardPage();
        paymentForm = dashboardPage.openPaymentForm();
    }

    @Nested
    class IncreasedTimeout {

        @Test
        @DisplayName("I 1.Оплата тура картой со статусом APPROVED")
        void shouldSendFormWithApprovedCard() {
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
        @DisplayName("I 2.Оплата тура картой со статусом DECLINED")
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

        @Test
        @DisplayName("3. Проверяем ввод недостаточного количества символов в поле Номер карты")
        void shouldErrorWithInvalidNumber() {
            paymentForm.fillForm(DataHelper.generateCardDataWithThreeSymbols(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardNumberError("Неверный формат", 2);
        }

        @Test
        @DisplayName("4. Проверяем ввод данных с истекшим сроком карты")
        void shouldErrorWithExpiryDateInPrevMonth() {
            paymentForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(-1));
            paymentForm.sendForm();

            paymentForm.checkMonthError("Неверно указан срок действия карты", 2);
        }

        @Test
        @DisplayName("5. Проверяем ввод невалидного числа месяца в поле Месяц")
        void shouldErrorWithWrongMonth() {
            paymentForm.fillForm(DataHelper.generateCardDataWithWrongMonth(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkMonthError("Неверно указан срок действия карты", 2);
        }

        @Test
        @DisplayName("6. Проверка граничащего значения в поле Год, + 6 лет от текущего года")
        void shouldErrorWithMoreThanExpiryYear() {
            paymentForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears + 1));
            paymentForm.sendForm();

            paymentForm.checkYearError("Неверно указан срок действия карты", 2);
        }

        @Test
        @DisplayName("7. Проверяем ввод имени владельца карты кириллицей")
        void shouldErrorWithCyrillicCardOwner() {
            paymentForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerLocale("ru", expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
        }

        @Test
        @DisplayName("8. Проверяем ввод в имени владельца недопустимых символов")
        void shouldInvalidCardOwnerWithSymbols() {
            paymentForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerSymbols(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
        }

        @Test
        @DisplayName("9. Проверка ввода цифр в поле Владелец")
        void shouldInvalidCardOwnerWithNumbers() {
            paymentForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerNumbers(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
        }

        @Test
        @DisplayName("10. Проверка ввода нижних граничащих значений в поле Владелец. Одно слово")
        void shouldWithIncompleteCardOwner() {
            paymentForm.fillForm(DataHelper.generateCardDataWithIncompleteCardOwner(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
        }

        @Test
        @DisplayName("11. Проверка ввода верхних граничащих значений в поле Владелец. 31 символ включая пробелы")
        void shouldWithOverlongCardOwner() {
            paymentForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength + 1, expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
        }
        @Test
        @DisplayName("12. Проверка отправки формы заявки с пустым значением в поле Номер карты")
        void shouldNotSendFormWithoutCardNumber() {
            paymentForm.fillForm(DataHelper.generateCardDataWithEmptyCard(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardNumberError("Неверный формат", 2);
        }

        @Test
        @DisplayName("13. Проверка отправки формы заявки с пустым значением в поле Месяц")
        void shouldNotSendFormWithoutMonth() {
            paymentForm.fillForm(DataHelper.generateCardDataWithEmptyMonth(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkMonthError("Неверный формат", 2);
        }

        @Test
        @DisplayName("14. Проверка отправки формы заявки с пустым значением в поле Год")
        void shouldNotSendFormWithoutYear() {
            paymentForm.fillForm(DataHelper.generateCardDataWithEmptyYear(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkYearError("Неверный формат", 2);
        }

        @Test
        @DisplayName("15. Проверка отправки формы заявки с пустым значением в поле Владелец")
        void shouldNotSendFormWithoutCardOwner() {
            paymentForm.fillForm(DataHelper.generateCardDataWithEmptyCardOwner(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
        }

        @Test
        @DisplayName("16. Проверка отправки формы заявки с пустым значением в поле CVC/CVV")
        void shouldNotSendFormWithoutCVC() {
            paymentForm.fillForm(DataHelper.generateCardDataWithEmptyCVC(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCVCError("Неверный формат", 2);
        }

        @Test
        @DisplayName("17. Проверка отправки формы заявки с пустыми значениями во всех полях")
        void shouldNotSendEmptyForm() {
            paymentForm.sendForm();

            paymentForm.checkCardNumberError("Неверный формат", 2);
            paymentForm.checkMonthError("Неверный формат", 2);
            paymentForm.checkYearError("Неверный формат", 2);
            paymentForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
            paymentForm.checkCVCError("Неверный формат", 2);
        }
        @Test
        @DisplayName("18. Проверка ввода буквенных символов в поле Номер карты")
        void shouldNotEnterInvalidCardNumber() {
            paymentForm.fillForm(DataHelper.generateCardDataWithSymbols(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardNumberError("Неверный формат", 2);
        }

        @Test
        @DisplayName("19. Проверка ввода недопустимых символов в поле ввода Месяц")
        void shouldNotEnterInvalidSymbolsInMonth() {
            paymentForm.fillMonth(DataHelper.getInvalidSymbolsForNumericFields());
            String actual = paymentForm.getMonthValue();

            assertEquals("", actual);
        }

        @Test
        @DisplayName("20. Проверка ввода недопустимых символов в поле ввода Год")
        void shouldNotEnterInvalidSymbolsInYear() {
            paymentForm.fillYear(DataHelper.getInvalidSymbolsForNumericFields());
            String actual = paymentForm.getYearValue();

            assertEquals("", actual);
        }

        @Test
        @DisplayName("21. Проверка ввода недопустимых символов в поле ввода CVC/CVV")
        void shouldNotEnterInvalidSymbolsInCVC() {
            paymentForm.fillCVC(DataHelper.getInvalidSymbolsForNumericFields());
            String actual = paymentForm.getCVCValue();

            assertEquals("", actual);
        }

        @Test
        @DisplayName("22. Проверка ввода несуществующего в базе данных номера карты")
        void shouldWithMissingInDatabase() {
            paymentForm.fillForm(DataHelper.generateCardDataWithNotDatabase(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkCardNumberError("Неверный формат", 2);
        }
   }
}

