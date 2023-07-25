package ru.netology.qa.diplom.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.netology.qa.diplom.data.DataHelper;
import ru.netology.qa.diplom.data.DataSQL;
import ru.netology.qa.diplom.page.CreditPayFormPage;
import ru.netology.qa.diplom.page.DashboardPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditPageTest extends TestData {
    static DashboardPage dashboardPage;
    static CreditPayFormPage creditForm;
    final String creditRequestsTable = "credit_request_entity";

    @BeforeEach
    void openHost() {
        open(address);

        dashboardPage = new DashboardPage();
        creditForm = dashboardPage.openCreditForm();
    }

    @Nested
    class IncreasedTimeout {

        @Test
        @DisplayName("II 1.Оплата тура в кредит с картой со статусом APPROVED")
        void shouldSendFormWithApprovedCard() {
            long rowsOrdersBefore = DataSQL.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = DataSQL.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateValidCardData(expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);

            assertEquals(rowsOrdersBefore + 1, DataSQL.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, DataSQL.getRowsAmountFrom(creditRequestsTable));

            assertEquals(approved, DataSQL.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("II 2. Оплата тура в кредит с картой со статусом DECLINED")
        void shouldSendFormWithDeclinedCard() {
            long rowsOrdersBefore = DataSQL.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = DataSQL.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
            creditForm.sendForm();

            creditForm.checkErrorNotification(10);
            assertEquals(rowsOrdersBefore + 1, DataSQL.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, DataSQL.getRowsAmountFrom(creditRequestsTable));
            assertEquals(declined, DataSQL.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("3. Проверяем ввод недостаточного количества символов в поле Номер карты")
        void shouldErrorWithInvalidNumber() {
            creditForm.fillForm(DataHelper.generateCardDataWithThreeSymbols(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardNumberError("Неверный формат", 2);
        }

        @Test
        @DisplayName("4. Проверяем ввод данных с истекшим сроком карты")
        void shouldErrorWithExpiryDateInPrevMonth() {
            creditForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(-1));
            creditForm.sendForm();

            creditForm.checkMonthError("Неверно указан срок действия карты", 2);
        }

        @Test
        @DisplayName("5. Проверяем ввод невалидного числа месяца в поле Месяц")
        void shouldErrorWithWrongMonth() {
            creditForm.fillForm(DataHelper.generateCardDataWithWrongMonth(expiryYears));
            creditForm.sendForm();

            creditForm.checkMonthError("Неверно указан срок действия карты", 2);
        }

        @Test
        @DisplayName("6. Проверка граничащего значения в поле Год, + 6 лет от текущего года")
        void shouldErrorWithMoreThanExpiryYear() {
            creditForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears + 1));
            creditForm.sendForm();

            creditForm.checkYearError("Неверно указан срок действия карты", 2);
        }

        @Test
        @DisplayName("7. Проверяем ввод имени владельца карты кириллицей")
        void shouldErrorWithCyrillicCardOwner() {
            creditForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerLocale("ru", expiryYears));
            creditForm.sendForm();

            creditForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
        }

        @Test
        @DisplayName("8. Проверяем ввод в имени владельца недопустимых символов")
        void shouldInvalidCardOwnerWithSymbols() {
            creditForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerSymbols(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
        }

        @Test
        @DisplayName("9. Проверка ввода цифр в поле Владелец")
        void shouldInvalidCardOwnerWithNumbers() {
            creditForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerNumbers(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
        }

        @Test
        @DisplayName("10. Проверка ввода нижних граничащих значений в поле Владелец. Одно слово")
        void shouldWithIncompleteCardOwner() {
            creditForm.fillForm(DataHelper.generateCardDataWithIncompleteCardOwner(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
        }

        @Test
        @DisplayName("11. Проверка ввода верхних граничащих значений в поле Владелец. 31 символ включая пробелы")
        void shouldWithOverlongCardOwner() {
            creditForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength + 1, expiryYears));
            creditForm.sendForm();

            creditForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
        }
        @Test
        @DisplayName("12. Проверка отправки формы заявки с пустым значением в поле Номер карты")
        void shouldNotSendFormWithoutCardNumber() {
            creditForm.fillForm(DataHelper.generateCardDataWithEmptyCard(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardNumberError("Неверный формат", 2);
        }

        @Test
        @DisplayName("13. Проверка отправки формы заявки с пустым значением в поле Месяц")
        void shouldNotSendFormWithoutMonth() {
            creditForm.fillForm(DataHelper.generateCardDataWithEmptyMonth(expiryYears));
            creditForm.sendForm();

            creditForm.checkMonthError("Неверный формат", 2);
        }

        @Test
        @DisplayName("14. Проверка отправки формы заявки с пустым значением в поле Год")
        void shouldNotSendFormWithoutYear() {
            creditForm.fillForm(DataHelper.generateCardDataWithEmptyYear(expiryYears));
            creditForm.sendForm();

            creditForm.checkYearError("Неверный формат", 2);
        }

        @Test
        @DisplayName("15. Проверка отправки формы заявки с пустым значением в поле Владелец")
        void shouldNotSendFormWithoutCardOwner() {
            creditForm.fillForm(DataHelper.generateCardDataWithEmptyCardOwner(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
        }

        @Test
        @DisplayName("16. Проверка отправки формы заявки с пустым значением в поле CVC/CVV")
        void shouldNotSendFormWithoutCVC() {
            creditForm.fillForm(DataHelper.generateCardDataWithEmptyCVC(expiryYears));
            creditForm.sendForm();

            creditForm.checkCVCError("Неверный формат", 2);
        }

        @Test
        @DisplayName("17. Проверка отправки формы заявки с пустыми значениями во всех полях")
        void shouldNotSendEmptyForm() {
            creditForm.sendForm();

            creditForm.checkCardNumberError("Неверный формат", 2);
            creditForm.checkMonthError("Неверный формат", 2);
            creditForm.checkYearError("Неверный формат", 2);
            creditForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
            creditForm.checkCVCError("Неверный формат", 2);
        }
        @Test
        @DisplayName("18. Проверка ввода буквенных символов в поле Номер карты")
        void shouldNotEnterInvalidCardNumber() {
            creditForm.fillForm(DataHelper.generateCardDataWithSymbols(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardNumberError("Неверный формат", 2);
        }

        @Test
        @DisplayName("19. Проверка ввода недопустимых символов в поле ввода Месяц")
        void shouldNotEnterInvalidSymbolsInMonth() {
            creditForm.fillMonth(DataHelper.getInvalidSymbolsForNumericFields());
            String actual = creditForm.getMonthValue();

            assertEquals("", actual);
        }

        @Test
        @DisplayName("20. Проверка ввода недопустимых символов в поле ввода Год")
        void shouldNotEnterInvalidSymbolsInYear() {
            creditForm.fillYear(DataHelper.getInvalidSymbolsForNumericFields());
            String actual = creditForm.getYearValue();

            assertEquals("", actual);
        }

        @Test
        @DisplayName("21. Проверка ввода недопустимых символов в поле ввода CVC/CVV")
        void shouldNotEnterInvalidSymbolsInCVC() {
            creditForm.fillCVC(DataHelper.getInvalidSymbolsForNumericFields());
            String actual = creditForm.getCVCValue();

            assertEquals("", actual);
        }

        @Test
        @DisplayName("22. Проверка ввода несуществующего в базе данных номера карты")
        void shouldWithMissingInDatabase() {
            creditForm.fillForm(DataHelper.generateCardDataWithNotDatabase(expiryYears));
            creditForm.sendForm();

            creditForm.checkCardNumberError("Неверный формат", 2);
        }
    }
}
