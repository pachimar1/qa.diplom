package ru.netology.qa.diplom.data;


import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataHelper {
    static Random random = new Random();

    private DataHelper() {
    }

    public static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getInvalidSymbolsForNumericFields() {
        Faker fakerRU = new Faker(new Locale("ru"));
        Faker fakerEN = new Faker(new Locale("en"));
        return fakerRU.lorem().characters(5, true, false) +
                fakerEN.lorem().characters(5, true, false) +
                "~`@\"#№$;%^:&?*()_-+=/\\{}[]|<>'";
    }

    public static String getInvalidSymbolsForCharacterFields() {
        return generateNumericCode(10) + "~`@\"#№$;%^:&?*()_+=/\\{}[]|<>'";
    }

    public static String getInvalidNumbersForCharacterFields() {
        return generateNumericCode(10) + "1234567890";
    }

    public static String getCardOwnerWithHyphen() {
        return "AGAEV ALI-AZIS";
    }

    public static String generateCardOwner(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.name().firstName().toUpperCase(Locale.ROOT) + " " + faker.name().lastName().toUpperCase(Locale.ROOT);
    }

    public static String generateNameOnly(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.name().firstName().toUpperCase(Locale.ROOT);
    }

    public static String generateCardOwnerWithFixedLength(String locale, int length) {
        Faker faker = new Faker(new Locale(locale));
        String first = faker.lorem().characters(length / 2, false, false);
        String second = faker.lorem().characters(length - first.length() - 1, false, false);

        return first + " " + second;
    }

    public static String generateMonth() {
        return String.format("%02d", (random.nextInt(12) + 1));
    }

    public static String generateWrongMonth() {
        return String.format("%02d", (random.nextInt(99 - 13) + 13));
    }

    public static String generateShiftedYearFromCurrent(int shift) {
        LocalDate date = LocalDate.now().plusYears(shift);
        return DateTimeFormatter.ofPattern("yy").format(date);
    }

    public static String generateNumericCode(int length) {
        Faker faker = new Faker();
        return faker.number().digits(length);
    }

    public static CardData generateValidCardData(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithDeclinedCard(int expiryYears) {
        return new CardData(getDeclinedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithCardOwnerFixedLength(int cardOwnerLength, int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwnerWithFixedLength("en", cardOwnerLength),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithInvalidCardOwnerLocale(String locale, int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner(locale),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithInvalidCardOwnerSymbols(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                getInvalidSymbolsForCharacterFields(),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithInvalidCardOwnerNumbers(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                getInvalidNumbersForCharacterFields(),
                generateNumericCode(3));
    }


    public static CardData generateCardDataWithIncompleteCardOwner(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateNameOnly("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithShiftedMonthFromCurrent(int shiftInMonths) {
        LocalDate date = LocalDate.now().plusMonths(shiftInMonths);
        return new CardData(getApprovedCardNumber(),
                DateTimeFormatter.ofPattern("MM").format(date),
                DateTimeFormatter.ofPattern("yy").format(date),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithShiftedYearFromCurrent(int shiftInYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(shiftInYears),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithWrongMonth(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateWrongMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithThreeSymbols(int expiryYears) {
        return new CardData("123",
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithSymbols(int expiryYears) {
        return new CardData("ABCD",
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithNotDatabase(int expiryYears) {
        return new CardData("4444 4444 4444 4444",
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }


    public static CardData generateCardDataWithEmptyCard(int expiryYears) {
        return new CardData(null,
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithEmptyMonth(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                null,
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithEmptyYear(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                null,
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithEmptyCardOwner(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                null,
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithEmptyCVC(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                null);
    }

    public static CardData generateCardDataWithHyphenCardOwner(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                getCardOwnerWithHyphen(),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithZeroYear() {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                "00",
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithZeroMonth(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                "00",
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    @Value
    public static class CardData {
        private String cardNumber;
        private String month;
        private String year;
        private String cardOwner;
        private String cvc;
    }
}