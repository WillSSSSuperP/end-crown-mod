package com.end_crown.util;

import net.minecraft.world.entity.player.Player;

public final class EndCrownState {
    private static final String ACTIVE_KEY = "end_crown_active";
    private EndCrownState() {
    }
    public static boolean isCrownActive(Player player) {
        return player.getPersistentData().getBoolean(ACTIVE_KEY);
    }
    public static void setCrownActive(Player player, boolean active) {
        player.getPersistentData().putBoolean(ACTIVE_KEY, active);
    }
}