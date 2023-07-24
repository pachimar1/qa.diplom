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

    public static String getCardOwnerWithHyphen() {
        return "IVAN IVANOV-PETROV";
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

    @Value
    public static class CardData {
        private String cardNumber;
        private String month;
        private String year;
        private String cardOwner;
        private String cvc;
    }
}