package org.zaproxy.addon.typosquatting;

public class AjadcentSwappedRule implements TyposquattingRule {
    @Override
    public boolean hasDetectedTyposquatting(String str1, String str2) {
        // Check if the lengths of the two strings are equal
        if (str1.length() != str2.length()) {
            return false;
        }

        // Convert the strings to character arrays
        char[] charArray1 = str1.toCharArray();
        char[] charArray2 = str2.toCharArray();

        int firstDifferenceIndex = -1;
        int secondDifferenceIndex = -1;

        for (int i = 0; i < charArray1.length; i++) {
            if (charArray1[i] != charArray2[i]) {
                if (firstDifferenceIndex == -1) {
                    firstDifferenceIndex = i;
                } else if (secondDifferenceIndex == -1) {
                    secondDifferenceIndex = i;
                } else {
                    // More than 2 differences, return false
                    return false;
                }
            }
        }

        // Check if exactly 2 differences are found, and they are adjacent
        return firstDifferenceIndex != -1 && secondDifferenceIndex != -1 && 
               str1.charAt(firstDifferenceIndex) == str2.charAt(secondDifferenceIndex) && 
               str1.charAt(secondDifferenceIndex) == str2.charAt(firstDifferenceIndex);
    }
}
