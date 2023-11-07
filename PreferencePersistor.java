package org.zaproxy.addon.typosquatting;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Map;
import java.util.stream.Collectors;

public class PreferencePersistor {
    // TODO: change filename and path
    private static String FILENAME;
    private HashMap<String, String> preferenceMapping = new HashMap<>(); // (key:val) -> (flagged site : site user chose to go to)

    public PreferencePersistor() {
        FILENAME = System.getProperty("user.dir")+"/addOns/typosquatting/src/main/resources/data.properties";
        loadPreferenceFromFile();
    }

    // Constructor for test scenario. Creates file using alternative relative path.
    public PreferencePersistor(String filename){
        FILENAME = filename;
        loadPreferenceFromFile();
    }

    public void savePreferenceToFile() {
        try {
            Properties properties = new Properties();
            properties.putAll(preferenceMapping);

            properties.store(new FileOutputStream(FILENAME), null);
        }
        catch (IOException e) {
            // TODO: unhandled
            throw new RuntimeException(e);
        }
    }

    public void loadPreferenceFromFile() {
        try {

            Properties properties = new Properties();
            properties.load(new FileInputStream(FILENAME));

            for (String key : properties.stringPropertyNames()) {
                preferenceMapping.put(key, properties.get(key).toString());
            }
           //  Uncomment to print data in map
            for (Map.Entry<String, String> m : preferenceMapping.entrySet()) {
                System.out.println(m.getKey() + " : " + m.getValue());
            }
        } catch (FileNotFoundException ignored){
        }
        catch ( IOException e) {
            // TODO: unhandled
            throw new RuntimeException(e);
        }
    }

    public void addPreference(String typedUrl, String desiredUrl){
        if (!preferenceMapping.containsKey(typedUrl)){
            preferenceMapping.put(typedUrl, desiredUrl);
            savePreferenceToFile();
        }
        else {
            // TODO: decide action for edge case, if a site already has been flagged
        }
    }

    public String getPreference(String url){
        // TODO: what to return if a preference is asked for and not registered?
        return preferenceMapping.getOrDefault(url, "");
    }

    // TODO: do we need this function?
    public HashSet<String> getPreviouslyAccessedSites(){
        if (!preferenceMapping.isEmpty()){
            return new HashSet<>(preferenceMapping.keySet());
        }
        // If no websites have been visited, return an empty HashSet
        return new HashSet<>();
    }

    // Returns all websites that we want to do typo-checking against
    public HashSet<String> getLegitSites() {
        return new HashSet<>(preferenceMapping.values());
    }

    // TODO: where should domain/URLs be stripped?
}
