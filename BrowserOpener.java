package org.zaproxy.addon.typosquatting;

import java.awt.Desktop;
import java.net.URI;

public class BrowserOpener {

    public static void openURLInDefaultBrowser(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                } catch (Exception e) {
                    System.err.println("Exception while trying to open URL: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}

