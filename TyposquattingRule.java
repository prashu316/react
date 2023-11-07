package org.zaproxy.addon.typosquatting;

public interface TyposquattingRule {
    boolean hasDetectedTyposquatting(String str1, String str2);
}
