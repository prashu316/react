/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.typosquatting;

import java.awt.CardLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.utils.FontUtils;
import org.zaproxy.zap.view.ZapMenuItem;

/**
 * An example ZAP extension which adds a top level menu item, a pop-up menu item and a status panel.
 *
 * <p>{@link ExtensionAdaptor} classes are the main entry point for adding/loading functionalities
 * provided by the add-ons.
 *
 * @see #hook(ExtensionHook)
 */
public class ExtensionTyposquatting extends ExtensionAdaptor {

    // The name is public so that other extensions can access it
    public static final String NAME = "ExtensionTyposquatting";

    // The i18n prefix, by default the package name - defined in one place to make it easier
    // to copy and change this example
    protected static final String PREFIX = "Typosquatting";

    /**
     * Relative path (from add-on package) to load add-on resources.
     *
     * @see Class#getResource(String)
     */
    private static final String RESOURCES = "resources";

    private static final String EXAMPLE_FILE = "example/ExampleFile.txt";

    private ZapMenuItem menuExample;
    private RightClickMsgMenu popupMsgMenuExample;
    private AbstractPanel statusPanel;

    private TyposquattingAPI api;

    private Set<String> test_websites = new HashSet<>();

    private ConcreteTyposquattingPreventer CTP;

    private PreferencePersistor PP;

    private static final Logger LOGGER = LogManager.getLogger(ExtensionTyposquatting.class);

    public ExtensionTyposquatting() {

        super(NAME);
        setI18nPrefix(PREFIX);
        test_websites.add("google.com");
        CTP = new ConcreteTyposquattingPreventer(test_websites);
        PP = new PreferencePersistor("D:\\fin\\zap-extensions\\addOns\\typosquatting\\src\\main\\resources\\data.properties");
        PP.loadPreferenceFromFile();
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        this.api = new TyposquattingAPI();
        extensionHook.addApiImplementor(this.api);

        // As long as we're not running as a daemon
        if (hasView()) {
            extensionHook.getHookMenu().addToolsMenuItem(getMenuExample());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupMsgMenuExample());
            extensionHook.getHookView().addStatusPanel(getStatusPanel());
        }
    }

    @Override
    public boolean canUnload() {
        // The extension can be dynamically unloaded, all resources used/added can be freed/removed
        // from core.
        return true;
    }

    @Override
    public void unload() {
        super.unload();

        // In this example it's not necessary to override the method, as there's nothing to unload
        // manually, the components added through the class ExtensionHook (in hook(ExtensionHook))
        // are automatically removed by the base unload() method.
        // If you use/add other components through other methods you might need to free/remove them
        // here (if the extension declares that can be unloaded, see above method).
    }

    private AbstractPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new AbstractPanel();
            statusPanel.setLayout(new CardLayout());
            statusPanel.setName(Constant.messages.getString(PREFIX + ".panel.title"));
            statusPanel.setIcon(new ImageIcon(getClass().getResource(RESOURCES + "/cake.png")));
            JTextPane pane = new JTextPane();
            pane.setEditable(false);
            // Obtain (and set) a font with the size defined in the options
            pane.setFont(FontUtils.getFont("Dialog", Font.PLAIN));
            pane.setContentType("text/html");
            pane.setText(Constant.messages.getString(PREFIX + ".panel.msg"));
            statusPanel.add(pane);
        }
        return statusPanel;
    }

    private ZapMenuItem getMenuExample() {
        LOGGER.info("Menu item clicked");
        if (menuExample == null) {
            menuExample = new ZapMenuItem(PREFIX);
                menuExample.addActionListener(
                        e -> {
                            // Prompt for URL input
                            String url = JOptionPane.showInputDialog(View.getSingleton().getOutputPanel(), 
                                    "Enter URL to check for typosquatting:", 
                                    "Check URL", JOptionPane.PLAIN_MESSAGE);
                            if (url != null && !url.isEmpty()) {

                                displayURL(url);
                                // TODO: Perform typosquatting check
                            if (CTP.hasDetectedTyposquatting(url) == false) {
                                BrowserOpener.openURLInDefaultBrowser(url);
                                CTP.COMMON_WEBSITES.add(url);
                                } else {
                                int n = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                                        "Are you sure you want to continue?",
                                        "Warning",
                                        JOptionPane.YES_NO_OPTION);
                                if(n==0)
                                {
                                    //displayURL(System.getProperty("user.dir"));
                                    String url2 = PP.getPreference(url);
                                    //displayURL(url);
                                    //displayURL(url2);
                                    if(url2 != "")
                                    {BrowserOpener.openURLInDefaultBrowser(url2);
                                        CTP.COMMON_WEBSITES.add(url2);}
                                    else
                                    {
                                        PP.addPreference(url,url);
                                        PP.savePreferenceToFile();
                                        BrowserOpener.openURLInDefaultBrowser(url);
                                        CTP.COMMON_WEBSITES.add(url);
                                    }

                                }


                                }
                            }
                        });
        }
        return menuExample;
    }

    private void displayURL(String url) {
        if (!View.isInitialised()) {
            // Running in daemon mode, shouldn't have been called
            return;
        }
        try {
            View.getSingleton().getOutputPanel().append(url);
            // Give focus to the Output tab
            View.getSingleton().getOutputPanel().setTabFocus();
        } catch (Exception e) {
            // Something unexpected went wrong, write the error to the log
            LOGGER.error(e.getMessage(), e);
        }
    }

    private RightClickMsgMenu getPopupMsgMenuExample() {
        if (popupMsgMenuExample == null) {
            popupMsgMenuExample =
                    new RightClickMsgMenu(
                            this, Constant.messages.getString(PREFIX + ".popup.title"));
        }
        return popupMsgMenuExample;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString(PREFIX + ".desc");
    }
}
