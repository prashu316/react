package org.zaproxy.addon.typosquatting;

public class MissingCharacterRule implements TyposquattingRule {

    @Override
    public boolean hasDetectedTyposquatting(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        if (Math.abs(len1 - len2) > 1) {
            return false;
        }

        if (len1 == len2) {
            int diffCount = 0;
            for (int i = 0; i < len1; i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    diffCount++;
                }
                if (diffCount > 1) {
                    return false; 
                }
            }
            return diffCount == 1;
        } else {
            int i = 0, j = 0;
            int diffCount = 0;

            while (i < len1 && j < len2) {
                if (str1.charAt(i) != str2.charAt(j)) {
                    diffCount++;
                    if (len1 > len2) {
                        i++;
                    } else {
                        j++;
                    }
                } else {
                    i++;
                    j++;
                }
                if (diffCount > 1) {
                    return false;
                }
            }
            return diffCount == 1 || i < len1 || j < len2; 
        }
    }
}





    
