package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
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

public class ModifyCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> buildCommand(){
        return CommandManager.literal(DiamondEconomyConfig.getInstance().modifyCommandName)
                .requires((permission) -> permission.hasPermissionLevel(DiamondEconomyConfig.getInstance().opCommandsPermissionLevel))
                .then(
                        CommandManager.argument("players", EntityArgumentType.players())
                                .then(
                                        CommandManager.argument("amount", IntegerArgumentType.integer())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return modifyCommand(e, EntityArgumentType.getPlayers(e, "players").stream().toList(), amount);
                                                }))
                )
                .then(
                        CommandManager.argument("amount", IntegerArgumentType.integer())
                                .then(
                                        CommandManager.argument("shouldModifyAll", BoolArgumentType.bool())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    boolean shouldModifyAll = BoolArgumentType.getBool(e, "shouldModifyAll");
                                                    return modifyCommand(e, amount, shouldModifyAll);
                                                })
                                )
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return modifyCommand(e, amount, false);
                                })
                );
    }

    public static int modifyCommand(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> players, int amount) {
        players.forEach(player -> ctx.getSource().sendFeedback(() -> Text.literal((DiamondUtils.getDatabaseManager().changeBalance(player.getUuidAsString(), amount)) ? ("Modified " + players.size() + " players money by $" + amount) : ("That would go out of the valid money range for " + player.getName().getString())), true));
        return players.size();
    }

    public static int modifyCommand(CommandContext<ServerCommandSource> ctx, int amount, boolean shouldModifyAll) throws CommandSyntaxException {
        if (shouldModifyAll) {
            DiamondUtils.getDatabaseManager().changeAllBalance(amount);
            ctx.getSource().sendFeedback(() -> Text.literal(("Modified everyones account by $" + amount)), true);
        } else {
            String output = (DiamondUtils.getDatabaseManager().changeBalance(ctx.getSource().getPlayerOrThrow().getUuidAsString(), amount)) ? ("Modified your money by $" + amount) : ("That would go out of your valid money range");
            ctx.getSource().sendFeedback(() -> Text.literal(output), true);
        }
        return 1;
    }
}
