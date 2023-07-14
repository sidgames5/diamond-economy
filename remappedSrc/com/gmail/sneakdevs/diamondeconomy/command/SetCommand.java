package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SetCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> buildCommand(){
        return CommandManager.literal(DiamondEconomyConfig.getInstance().setCommandName)
                .requires((permission) -> permission.hasPermissionLevel(DiamondEconomyConfig.getInstance().opCommandsPermissionLevel))
                .then(
                        CommandManager.argument("players", EntityArgumentType.players())
                                .then(
                                        CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return setCommand(e, EntityArgumentType.getPlayers(e, "players").stream().toList(), amount);
                                                }))
                )
                .then(
                        CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                .then(
                                        CommandManager.argument("shouldModifyAll", BoolArgumentType.bool())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    boolean shouldModifyAll = BoolArgumentType.getBool(e, "shouldModifyAll");
                                                    return setCommand(e, amount, shouldModifyAll);
                                                })
                                )
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return setCommand(e, amount, false);
                                })
                );
    }

    public static int setCommand(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> players, int amount) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        players.forEach(player -> dm.setBalance(player.getUuidAsString(), amount));
        ctx.getSource().sendFeedback(() -> Text.literal("Updated balance of " + players.size() + " players to " + amount), true);
        return players.size();
    }

    public static int setCommand(CommandContext<ServerCommandSource> ctx, int amount, boolean shouldModifyAll) throws CommandSyntaxException {
        if (shouldModifyAll) {
            DiamondUtils.getDatabaseManager().setAllBalance(amount);
            ctx.getSource().sendFeedback(() -> Text.literal("All accounts balance to " + amount), true);
        } else {
            DiamondUtils.getDatabaseManager().setBalance(ctx.getSource().getPlayerOrThrow().getUuidAsString(), amount);
            ctx.getSource().sendFeedback(() -> Text.literal("Updated your balance to " + amount), true);
        }
        return 1;
    }
}
