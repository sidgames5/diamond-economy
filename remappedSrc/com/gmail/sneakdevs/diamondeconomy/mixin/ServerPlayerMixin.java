package com.gmail.sneakdevs.diamondeconomy.mixin;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void diamondeconomy_tickMixin(CallbackInfo ci) {
        if (DiamondEconomyConfig.getInstance().moneyAddAmount != 0 && DiamondEconomyConfig.getInstance().moneyAddTimer > 0) {
            int playTime = ((ServerPlayerEntity)(Object)this).getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
            if (playTime > 0 && playTime % (DiamondEconomyConfig.getInstance().moneyAddTimer * 20) == 0) {
                DiamondUtils.getDatabaseManager().changeBalance(((ServerPlayerEntity)(Object)this).getUuidAsString(), DiamondEconomyConfig.getInstance().moneyAddAmount);
            }
        }
    }
}