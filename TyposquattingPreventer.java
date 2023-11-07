package org.zaproxy.addon.typosquatting;

public interface TyposquattingPreventer {
    boolean hasDetectedTyposquatting(String url);
}
