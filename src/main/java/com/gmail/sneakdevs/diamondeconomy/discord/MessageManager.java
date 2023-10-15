package com.gmail.sneakdevs.diamondeconomy.discord;

import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageManager {
    private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

    public static void logTransaction(String sender, String recipient, int amount) {
        String url = DiamondEconomyConfig.getInstance().webhookURL;
        if (url == null || url.isEmpty()) {
            return;
        }
        var webhook = new Webhook(url);
        webhook.setContent("$" + amount + " " + sender + " -> " + recipient);
        try {
            webhook.execute();
        } catch (IOException e) {
            logger.error("Error sending webhook", e);
        }
    }
}
