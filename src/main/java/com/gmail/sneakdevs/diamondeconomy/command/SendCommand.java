package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.discord.MessageManager;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SendCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
        return Commands.literal(DiamondEconomyConfig.getInstance().sendCommandName)
            .then(Commands.argument("playerName", StringArgumentType.string())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(e -> {
                        String player = StringArgumentType.getString(e, "playerName");
                        int amount = IntegerArgumentType.getInteger(e, "amount");

                        sendCommand(e, player, e.getSource().getPlayerOrException(), amount);
                        return 1;
                    })))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(e -> {
                        String player = EntityArgument.getPlayer(e, "player").getName().getString();
                        int amount = IntegerArgumentType.getInteger(e, "amount");

                        sendCommand(e, player, e.getSource().getPlayerOrException(), amount);
                        return 1;
                    })));
    }

    public static void sendCommand(CommandContext<CommandSourceStack> ctx, String player, ServerPlayer serverPlayer1, int amount) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        MinecraftServer server = ctx.getSource().getServer();

        if (amount < 1) {
            ctx.getSource().sendSuccess(() -> Component.literal("Error: amount too low"), false);
            return;
        }

        String playerUUID = null;
        String playerName = null;
        ServerPlayer serverPlayer = server.getPlayerList().getPlayerByName(player);
        if (serverPlayer != null) {
            playerUUID = serverPlayer.getStringUUID();
            playerName = serverPlayer.getName().getString();
        } else {
            playerUUID = dm.getUUIDFromName(player);
            if (playerUUID != null) {
                playerName = dm.getNameFromUUID(playerUUID);
            } else {
                playerName = null;
            }

        }
        if (playerUUID == null || playerName == null || playerUUID.isEmpty() || playerName.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("Error: user was not found"), false);
            return;
        }

        try {
            Math.addExact(dm.getBalanceFromUUID(playerUUID), amount);
        } catch (ArithmeticException e) {
            ctx.getSource().sendSuccess(() -> Component.literal("Error: total amount would go over the max value"), false);
            return;
        }

        if (!dm.changeBalance(serverPlayer1.getStringUUID(), -amount)) {
            ctx.getSource().sendSuccess(() -> Component.literal("Error: you do not have enough money"), false);
            return;
        }

        dm.changeBalance(playerUUID, amount);
        if (serverPlayer != null) {
            serverPlayer.displayClientMessage(Component.literal("You received $" + amount + " from " + serverPlayer1.getName().getString()), false);
        }
        MessageManager.logTransaction(serverPlayer1.getName().getString(), playerName, amount);
        String finalServerPlayerName = playerName;
        ctx.getSource().sendSuccess(() -> Component.literal("Sent $" + amount + " to " + finalServerPlayerName), false);
    }
}
