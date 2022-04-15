package com.gmail.sneakdevs.diamondeconomy.mixin;

import com.gmail.sneakdevs.diamondeconomy.interfaces.LockableContainerBlockEntityInterface;
import com.gmail.sneakdevs.diamondeconomy.config.DEConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LockableContainerBlockEntity.class)
public class LockableContainerBlockEntityMixin implements LockableContainerBlockEntityInterface {
    private String diamondeconomy_owner;

    public void diamondeconomy_setOwner(String newOwner) {
        if (newOwner == null) newOwner = "@";
        this.diamondeconomy_owner = newOwner;
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void diamondeconomy_writeNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        if (AutoConfig.getConfigHolder(DEConfig.class).getConfig().chestShops) {
            if (diamondeconomy_owner == null) diamondeconomy_owner = "@";
            if (!nbt.contains("ShopOwner")) nbt.putString("ShopOwner", diamondeconomy_owner);
            if (!nbt.contains("IsShop")) nbt.putBoolean("IsShop", false);
        }
    }
}