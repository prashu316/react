package org.zaproxy.addon.typosquatting;

public class ExtraCharactetrRule implements TyposquattingRule {
    
    @Override
    public boolean hasDetectedTyposquatting(String str1, String str2) {
        if (str2.length() == str1.length() + 1) {
            if (str2.contains(str1)) {
                return true;
            }
        }
        return false;
    }
}
