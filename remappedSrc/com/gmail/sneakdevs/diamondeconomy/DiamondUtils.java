package com.gmail.sneakdevs.diamondeconomy;

import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.gmail.sneakdevs.diamondeconomy.sql.SQLiteDatabaseManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class DiamondUtils {
    public static void registerTable(String query){
        DiamondEconomy.tableRegistry.add(query);
    }

    public static DatabaseManager getDatabaseManager() {
        return new SQLiteDatabaseManager();
    }

    public static int dropItem(int amount, ServerPlayerEntity player) {


        if (DiamondEconomyConfig.getInstance().greedyWithdraw) {

            for (int i = DiamondEconomyConfig.getCurrencyValues().length - 1; i >= 0 && amount > 0; i--) {

                int val = DiamondEconomyConfig.getCurrencyValues()[i];
                int currSize = DiamondEconomyConfig.getCurrency(i).getMaxCount();
                Item curr = DiamondEconomyConfig.getCurrency(i);

                while (amount >= val * currSize) {
                    ItemEntity itemEntity = player.dropItem(new ItemStack(curr, currSize), true);
                    itemEntity.resetPickupDelay();
                    amount -= val * currSize;
                }

                if (amount >= val) {
                    ItemEntity itemEntity = player.dropItem(new ItemStack(curr, amount / val), true);
                    itemEntity.resetPickupDelay();
                    amount -= amount / val * val;
                }

            }

        } else {

            int val = DiamondEconomyConfig.getCurrencyValues()[0];
            int currSize = DiamondEconomyConfig.getCurrency(0).getMaxCount();
            Item curr = DiamondEconomyConfig.getCurrency(0);

            while (amount >= val * currSize) {
                ItemEntity itemEntity = player.dropItem(new ItemStack(curr, currSize), true);
                itemEntity.resetPickupDelay();
                amount -= val * currSize;
            }

            if (amount >= val) {
                ItemEntity itemEntity = player.dropItem(new ItemStack(curr, amount / val), true);
                itemEntity.resetPickupDelay();
                amount -= amount / val * val;
            }
        }

        DatabaseManager dm = getDatabaseManager();
        dm.changeBalance(player.getUuidAsString(), amount);

        return amount;
    }
}
