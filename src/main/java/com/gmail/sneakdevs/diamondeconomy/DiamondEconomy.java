package com.gmail.sneakdevs.diamondeconomy;

import com.gmail.sneakdevs.diamondeconomy.command.DiamondEconomyCommands;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.SQLiteDatabaseManager;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

public class DiamondEconomy implements ModInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DiamondEconomy.class);
    public static final String MODID = "diamondeconomy";
    public static ArrayList<String> tableRegistry = new ArrayList<>();

    public static void initServer(MinecraftServer server) {
        DiamondUtils.registerTable("CREATE TABLE IF NOT EXISTS diamonds (uuid text PRIMARY KEY, name text NOT NULL, money integer DEFAULT 0);");
        SQLiteDatabaseManager.createNewDatabase((DiamondEconomyConfig.getInstance().fileLocation != null) ? (new File(DiamondEconomyConfig.getInstance().fileLocation)) : server.getWorldPath(LevelResource.ROOT).resolve(DiamondEconomy.MODID + ".sqlite").toFile());
    }

    public static void registerPlaceholders() {
        Placeholders.register(new ResourceLocation(MODID, "rank_from_player"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(Component.literal(DiamondUtils.getDatabaseManager().playerRank(ctx.player().getStringUUID()) + ""));
            } else {
                return PlaceholderResult.invalid();
            }
        });
        Placeholders.register(new ResourceLocation(MODID, "rank_from_string_uuid"), (ctx, arg) -> {
            if (arg != null) {
                return PlaceholderResult.value(Component.literal(DiamondUtils.getDatabaseManager().playerRank(arg) + ""));
            } else {
                return PlaceholderResult.invalid();
            }
        });
        Placeholders.register(new ResourceLocation(MODID, "balance_from_player"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(Component.literal(DiamondUtils.getDatabaseManager().getBalanceFromUUID(ctx.player().getStringUUID()) + ""));
            } else {
                return PlaceholderResult.invalid();
            }
        });
        Placeholders.register(new ResourceLocation(MODID, "balance_from_string_uuid"), (ctx, arg) -> {
            if (arg != null) {
                return PlaceholderResult.value(Component.literal(DiamondUtils.getDatabaseManager().getBalanceFromUUID(arg) + ""));
            } else {
                return PlaceholderResult.invalid();
            }
        });
        Placeholders.register(new ResourceLocation(MODID, "balance_from_name"), (ctx, arg) -> {
            if (arg != null) {
                return PlaceholderResult.value(Component.literal(DiamondUtils.getDatabaseManager().getBalanceFromName(arg) + ""));
            } else {
                return PlaceholderResult.invalid();
            }
        });
        Placeholders.register(new ResourceLocation(MODID, "player_from_rank"), (ctx, arg) -> {
            if (arg != null) {
                return PlaceholderResult.value(Component.literal(DiamondUtils.getDatabaseManager().rank(Integer.parseInt(arg))));
            } else {
                return PlaceholderResult.invalid();
            }
        });
    }

    @Override
    public void onInitialize() {
        logger.info("Starting Diamond Economy");

        AutoConfig.register(DiamondEconomyConfig.class, JanksonConfigSerializer::new);
        registerPlaceholders();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> DiamondEconomyCommands.register(dispatcher));
        ServerLifecycleEvents.SERVER_STARTING.register(DiamondEconomy::initServer);

        logger.info("Started Diamond Economy");
    }
}
