package org.zaproxy.addon.typosquatting;

public class ArbitraryWordRule implements TyposquattingRule {

    @Override
    public boolean hasDetectedTyposquatting(String str1, String str2) {
        String[] words = {"online", "shop", "store", "buy", "deals", "offers", "sale", "discount", "best", "cheap"};
        if (str2.startsWith(str1)) {
            for (String word : words) {
                if (str2.endsWith(word)) {
                    if (word.length() + str1.length() == str2.length()) return true;
                }
            }
        }
        return false;
    }
}