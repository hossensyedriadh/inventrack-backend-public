package io.github.hossensyedriadh.InvenTrackRESTfulService.utils;

public class Generator {
    public static String generateRandomString(int length, boolean includeNumbers) {
        String letters;
        if (includeNumbers) {
            letters = "hH1VT9wbzAXp18gko0vK5OStG6WCxYLjJ42Ndn3mMFRaDZUuefBPyQrEsc7q";
        } else {
            letters = "rvqhWdXHzgmbeZFMNYsSGfQTxCnJAROBaPoLUkKwEDpjucytV";
        }

        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i += 1) {
            randomString.append(letters.toCharArray()[(int) (Math.random() * letters.length())]);
        }

        return randomString.toString();
    }
}
