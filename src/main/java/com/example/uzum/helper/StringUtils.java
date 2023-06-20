package com.example.uzum.helper;

import com.example.uzum.dto.Result;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
public class StringUtils {


    public static String capitalizeText(String text) {
        text = text.trim();
        if (text.isEmpty()) return null;
        String firstLetter = text.substring(0, 1).toUpperCase();
        return firstLetter.concat(text.substring(1).toLowerCase());
    }

    public static String generateVerificationCode() {
        String verificationCode = "";
        String numbers = "0123456789";
        Random random = new Random();
        char[] chars = new char[6];
        for (int i = 0; i < 6; i++) {
            chars[i] = numbers.charAt(random.nextInt(numbers.length()));
            verificationCode = verificationCode.concat(String.valueOf(chars[i]));
        }
        return verificationCode;
    }

    public static Result<?> validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 13) return new Result<>(false, Messages.PHONE_NUMBER_MUST_BE_13_CHARACTER);
        if (!phoneNumber.startsWith("+998"))
            return new Result<>(false, Messages.ENTER_PHONE_NUMBER_WHICH_IS_REGISTERED_IN_UZB);
        for (int i = 1; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i)))
                return new Result<>(false, Messages.ENTER_ONLY_NUMBERS_AS_PHONE_NUMBER);
        }
        return new Result<>(true, null);
    }

    public static Result<?> validateCardDetails(String cardNumber, String cardExpireDate) {
        cardNumber = cardNumber.trim();
        cardExpireDate = cardExpireDate.trim();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (cardNumber.length() != 16) return new Result<>(false, Messages.CARD_NUMBER_LENGTH_MUST_BE_16_DIGITS);
            if (!Character.isDigit(cardNumber.charAt(i)))
                return new Result<>(false, Messages.CARD_NUMBER_MUST_CONTAIN_ONLY_DIGITS);
        }
        if (cardExpireDate.length() != 4) return new Result<>(false, Messages.CARD_EXPIRED_DATE_MUST_BE_4_DIGITS);
        for (int i = 0; i < cardExpireDate.length(); i++) {
            if (!Character.isDigit(cardExpireDate.charAt(i)))
                return new Result<>(false, Messages.CARD_EXPIRED_DATE_MUST_CONTAIN_ONLY_DIGITS);
        }
        return new Result<>(true, null);
    }

    public static Result<?> validatePassword(String password) {
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;

        if (password.length() < 8) return new Result<>(false, Messages.PASSWORD_MUST_BE_8_CHARACTER_AT_LEAST);
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) upper = true;
            else if (Character.isLowerCase(password.charAt(i))) lower = true;
            else if (Character.isDigit(password.charAt(i))) digit = true;
        }

        if (upper && lower && digit) return new Result<>(true, null);
        else return new Result<>(false, Messages.PASSWORD_MUST_CONTAIN_ONE_UPPERCASE_ONE_LOWERCASE_ONE_DIGIT_AT_LEAST);
    }

    // Sorted: byDayMonthYear,  byMonthYear, byYear, allTime, latestWeek.
    public static List<LocalDateTime> getFromAndToInterval(List<String> date) {
        LocalDateTime from;
        LocalDateTime to;
        if (date.size() == 3) { // byDayMonthYear
            int day = Integer.parseInt(date.get(0));
            int month = Integer.parseInt(date.get(1));
            int year = Integer.parseInt(date.get(2));
            from = LocalDateTime.of(year, month, day, 0, 0);
            to = from.plusDays(1);
        } else if (date.size() == 2) {   // byMonthYear
            int month = Integer.parseInt(date.get(0));
            int year = Integer.parseInt(date.get(1));
            from = LocalDateTime.of(year, month, 1, 0, 0);
            to = from.plusMonths(1);
        } else if (date.get(0).equals(Filter.ALL_TIME)) {
            from = LocalDateTime.parse(Filter.DAY_OF_ESTABLISHMENT);
            to = LocalDateTime.now();
        } else if (date.get(0).equals(Filter.AT_MOMENT)) { // this is used only in order controller for preparing and delivering methods.
            return null;
        } else if (date.get(0).equals(Filter.LATEST_WEEK)) {
            from = LocalDateTime.now().minusDays(7);
            to = LocalDateTime.now();
        } else { // byYear
            int year = Integer.parseInt(date.get(0));
            from = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
            to = from.plusYears(1);
        }
        if (from.isBefore(LocalDateTime.now()) && to.isAfter(LocalDateTime.now())) to = LocalDateTime.now();
        return List.of(from, to);
    }

    public static String changeBracket(List<Integer> list){
        if (list.isEmpty()){
            return "()";
        }
        String listInString = list.toString();
        String listContent = listInString.substring(1,listInString.length()-1);
        return "(".concat(listContent).concat(")");
    }


    public static void sendDataToPaypalToWithdrawMoney(String fromCardNumber, String fromCardExpireDate, String toCardNumber, String toCardExpireDate, Integer amountMoney) {
        /*  DO NOTHING !   */
    }

    public static Integer sendDataToPaypalToCheckCardBalance(String cardNumber, String cardExpireDate) {
        /*  DO NOTHING ! */
        return 8500000;
    }

}
