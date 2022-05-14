package com.gmail.sneakdevs.diamondeconomy.mixin;

import com.gmail.sneakdevs.diamondeconomy.DiamondEconomy;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManagerMixin_DiamondEconomy {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void diamondeconomy_onPlayerConnectMixin(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        DatabaseManager dm = DiamondEconomy.getDatabaseManager();
        String uuid = serverPlayer.getStringUUID();
        String name = serverPlayer.getName().getString();
        dm.addPlayer(uuid, name);
        dm.setName(uuid, name);
    }
}