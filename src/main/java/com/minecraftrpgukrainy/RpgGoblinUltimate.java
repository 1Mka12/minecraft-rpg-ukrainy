package com.minecraftrpgukrainy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class RpgGoblinUltimate {

    public static final int REQUIRED_PERCEPTION = 30;
    public static final int DURATION_TICKS = 60 * 20;
    public static final int COOLDOWN_TICKS = 180 * 20;

    private static final String DURATION_KEY =
            "rpg_goblin_ultimate_duration";
    private static final String COOLDOWN_KEY =
            "rpg_goblin_ultimate_cooldown";

    private static final ResourceLocation DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy", "goblin_ultimate_damage");

    private static final ResourceLocation SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy", "goblin_ultimate_speed");

    private static final ResourceLocation ARMOR_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy", "goblin_ultimate_armor");

    private RpgGoblinUltimate() {
    }

    public static boolean activate(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }

        // Перевірка через наявну систему даних RPG.
        RpgPlayerData data = RpgPlayerData.get(player);

        if (data == null
                || data.getPerception() < REQUIRED_PERCEPTION) {
            return false;
        }

        if (player.getPersistentData().getInt(COOLDOWN_KEY) > 0) {
            return false;
        }

        if (player.getPersistentData().getInt(DURATION_KEY) > 0) {
            return false;
        }

        addModifier(
                player.getAttribute(Attributes.ATTACK_DAMAGE),
                DAMAGE_ID,
                0.50,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );

        addModifier(
                player.getAttribute(Attributes.MOVEMENT_SPEED),
                SPEED_ID,
                0.50,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );

        addModifier(
                player.getAttribute(Attributes.ARMOR),
                ARMOR_ID,
                8.0,
                AttributeModifier.Operation.ADD_VALUE
        );

        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        player.getPersistentData().putInt(
                DURATION_KEY, DURATION_TICKS);
        player.getPersistentData().putInt(
                COOLDOWN_KEY, COOLDOWN_TICKS);

        return true;
    }

    private static void addModifier(
            AttributeInstance attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation) {

        if (attribute == null) {
            return;
        }

        attribute.removeModifier(id);
        attribute.addTransientModifier(
                new AttributeModifier(id, amount, operation));
    }

    public static void tick(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        var persistent = player.getPersistentData();

        int cooldown = persistent.getInt(COOLDOWN_KEY);
        if (cooldown > 0) {
            persistent.putInt(COOLDOWN_KEY, cooldown - 1);
        }

        int duration = persistent.getInt(DURATION_KEY);
        if (duration <= 0) {
            return;
        }

        duration--;
        persistent.putInt(DURATION_KEY, duration);

        if (duration == 0) {
            removeModifier(
                    player.getAttribute(Attributes.ATTACK_DAMAGE),
                    DAMAGE_ID);
            removeModifier(
                    player.getAttribute(Attributes.MOVEMENT_SPEED),
                    SPEED_ID);
            removeModifier(
                    player.getAttribute(Attributes.ARMOR),
                    ARMOR_ID);

            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        }
    }

    private static void removeModifier(
            AttributeInstance attribute,
            ResourceLocation id) {

        if (attribute != null) {
            attribute.removeModifier(id);
        }
    }

    public static boolean isActive(ServerPlayer player) {
        return player != null
                && player.getPersistentData()
                .getInt(DURATION_KEY) > 0;
    }

    public static int getCooldownTicks(ServerPlayer player) {
        return player == null
                ? 0
                : Math.max(0, player.getPersistentData()
                .getInt(COOLDOWN_KEY));
    }
}
