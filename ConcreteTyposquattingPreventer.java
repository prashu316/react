package org.zaproxy.addon.typosquatting;

import java.util.Set;
import java.util.HashSet;

public class ConcreteTyposquattingPreventer implements TyposquattingPreventer {
    private final Set<TyposquattingRule> RULES = new HashSet<>();
    public final Set<String> COMMON_WEBSITES = new HashSet<>();

    public ConcreteTyposquattingPreventer(Set<String> commonWebsites) {
        // Rule 1: 1 missing character
        MissingCharacterRule missingCharacterRule = new MissingCharacterRule();
        addTyposquattingRule(missingCharacterRule);

        // Rule 2: 1 replaced character
        ReplacedCharacterRule replacedCharacterRule = new ReplacedCharacterRule();
        addTyposquattingRule(replacedCharacterRule);

        // Rule 3: 1 extra character
        ExtraCharactetrRule extraCharactetrRule = new ExtraCharactetrRule();
        addTyposquattingRule(extraCharactetrRule);

        // Rule 4: 2 adjacent swapped characters
        AjadcentSwappedRule ajadcentSwappedRule = new AjadcentSwappedRule();
        addTyposquattingRule(ajadcentSwappedRule);

        // Rule 5: appending an arbitrary word that appears legitimate
        ArbitraryWordRule arbitraryWordRule = new ArbitraryWordRule();
        addTyposquattingRule(arbitraryWordRule);

        COMMON_WEBSITES.addAll(commonWebsites);
    }

    public void addTyposquattingRule(TyposquattingRule rule) {
        this.RULES.add(rule);
    }


    @Override
    public boolean hasDetectedTyposquatting(String url) {
        for (TyposquattingRule rule : this.RULES) {
            for( String commonWebsite : this.COMMON_WEBSITES) {
                if (rule.hasDetectedTyposquatting(url, commonWebsite) == true) {
                    return true;
                }
            }
        }
        return false;
    }
}
