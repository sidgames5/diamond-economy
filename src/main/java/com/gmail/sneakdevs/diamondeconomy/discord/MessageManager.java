package com.gmail.sneakdevs.diamondeconomy.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageManager {
    private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

    public static void logTransaction(String sender, String recipient, int amount) {
        var webhook = new Webhook("https://discord.com/api/webhooks/1131960494257872936/YQWosOo2L5764ne5efROPYHYAN-ky93T4xtRQXoJ9xjanukQCOXQiStjAC1Ha51efiY4");
        webhook.setContent("$" + amount + " " + sender + " -> " + recipient);
        try {
            webhook.execute();
        } catch (IOException e) {
            logger.error("Error sending webhook", e);
        }
    }
}
