package org.zaproxy.addon.typosquatting;

public class ReplacedCharacterRule implements TyposquattingRule {

    @Override
    public boolean hasDetectedTyposquatting(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false; 
        }

        int differences = 0;

        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                differences++;
                if (differences > 1) {
                    return false; 
                }
            }
        }

        return differences == 1;
    }
}
