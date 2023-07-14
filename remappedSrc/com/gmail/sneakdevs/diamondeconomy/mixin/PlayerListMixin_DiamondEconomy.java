package com.gmail.sneakdevs.diamondeconomy.mixin;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerListMixin_DiamondEconomy {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void diamondeconomy_onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity serverPlayer, CallbackInfo ci) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        String uuid = serverPlayer.getUuidAsString();
        String name = serverPlayer.getName().getString();
        dm.addPlayer(uuid, name);
        dm.setName(uuid, name);
    }
}