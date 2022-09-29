package io.github.hossensyedriadh.inventrackrestfulservice.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Generator {
    public static String generateOtp(int length, boolean numbersOnly) {
        StringBuilder otp = new StringBuilder();

        if (numbersOnly) {
            String[] array = {"1", "0", "4", "7", "2", "5", "9", "3", "8", "6"};

            for (int i = 0; i < length; i += 1) {
                otp.append(array[(int) (Math.random() * array.length - 1)]);
            }
        } else {
            String letters = "hH1VT9wbzAXp18gkvK5StG6WCxYLjJ42Ndn3mMFRaDZUuefBPyQrEsc7q";

            for (int i = 0; i < length; i += 1) {
                otp.append(letters.charAt((int) (Math.random() * (letters.length() - 1))));
            }
        }

        return shuffle(otp.reverse().toString());
    }

    private static String shuffle(String string) {
        List<String> res = Arrays.asList(string.split(""));
        Collections.shuffle(res, new SecureRandom());

        return String.join("", res);
    }
}
