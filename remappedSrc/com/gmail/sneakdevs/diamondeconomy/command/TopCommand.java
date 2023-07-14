package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TopCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> buildCommand(){
        return CommandManager.literal(DiamondEconomyConfig.getInstance().topCommandName)
                .then(
                        CommandManager.argument("page", IntegerArgumentType.integer(1))
                                .executes(e -> {
                                    int page = IntegerArgumentType.getInteger(e, "page");
                                    return topCommand(e, page);
                                })
                )
                .executes(e -> topCommand(e, 1));
    }

    public static int topCommand(CommandContext<ServerCommandSource> ctx, int page) throws CommandSyntaxException {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        String output = dm.top(ctx.getSource().getPlayerOrThrow().getUuidAsString(), page);
        ctx.getSource().sendFeedback(() -> Text.literal(output), false);
        return 1;
    }
}
