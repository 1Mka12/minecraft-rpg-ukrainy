package com.minecraftrpgukrainy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class RpgGoblinUltimate {

    public static final int REQUIRED_PERCEPTION = 30;
    public static final int DURATION_SECONDS = 60;
    public static final int COOLDOWN_SECONDS = 180;

    public static final int DURATION_TICKS =
            DURATION_SECONDS * 20;

    public static final int COOLDOWN_TICKS =
            COOLDOWN_SECONDS * 20;

    private static final String PREFIX =
            "minecraft_rpg_ukrainy_goblin_ultimate_";

    private static final String DURATION_KEY =
            PREFIX + "duration";

    private static final String COOLDOWN_KEY =
            PREFIX + "cooldown";

    private static final String ACTIVE_KEY =
            PREFIX + "active";

    private static final ResourceLocation DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "goblin_ultimate_damage"
            );

    private static final ResourceLocation SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "goblin_ultimate_speed"
            );

    private static final ResourceLocation ARMOR_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "goblin_ultimate_armor"
            );

    private RpgGoblinUltimate() {
    }

    /**
     * Активує ультимативну навичку гобліна.
     *
     * Вимога: 30 очок Сприйняття.
     * Тривалість: 60 секунд.
     * Перезаряджання: 180 секунд.
     */
    public static boolean activate(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }

        CompoundTag data = player.getPersistentData();

        // Перевіряємо очки Сприйняття через наявну систему RPG.
        if (RpgPlayerData.getPoints(
                player,
                RpgStats.Stat.PERCEPTION
        ) < REQUIRED_PERCEPTION) {
            return false;
        }

        // Забороняємо повторну активацію під час дії
        // або перезаряджання.
        if (data.getInt(DURATION_KEY) > 0
                || data.getInt(COOLDOWN_KEY) > 0) {
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

        // +8 одиниць броні.
        addModifier(
                player.getAttribute(Attributes.ARMOR),
                ARMOR_ID,
                8.0,
                AttributeModifier.Operation.ADD_VALUE
        );

        // Опір I протягом дії навички.
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                DURATION_TICKS,
                0,
                false,
                true,
                true
        ));

        data.putInt(DURATION_KEY, DURATION_TICKS);
        data.putInt(COOLDOWN_KEY, COOLDOWN_TICKS);
        data.putBoolean(ACTIVE_KEY, true);

        return true;
    }

    /**
     * Викликати один раз на кожному серверному тику гравця.
     */
    public static void tick(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        int cooldown = data.getInt(COOLDOWN_KEY);

        if (cooldown > 0) {
            data.putInt(COOLDOWN_KEY, cooldown - 1);
        }

        int duration = data.getInt(DURATION_KEY);

        if (duration <= 0) {
            return;
        }

        duration--;
        data.putInt(DURATION_KEY, duration);

        if (duration == 0) {
            removeModifier(
                    player.getAttribute(Attributes.ATTACK_DAMAGE),
                    DAMAGE_ID
            );

            removeModifier(
                    player.getAttribute(Attributes.MOVEMENT_SPEED),
                    SPEED_ID
            );

            removeModifier(
                    player.getAttribute(Attributes.ARMOR),
                    ARMOR_ID
            );

            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            data.putBoolean(ACTIVE_KEY, false);
        }
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
                new AttributeModifier(id, amount, operation)
        );
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
                .getBoolean(ACTIVE_KEY);
    }

    public static int getRemainingTicks(ServerPlayer player) {
        if (player == null) {
            return 0;
        }

        return Math.max(
                0,
                player.getPersistentData().getInt(DURATION_KEY)
        );
    }

    public static int getCooldownTicks(ServerPlayer player) {
        if (player == null) {
            return 0;
        }

        return Math.max(
                0,
                player.getPersistentData().getInt(COOLDOWN_KEY)
        );
    }

    public static int getCooldownSeconds(ServerPlayer player) {
        return (getCooldownTicks(player) + 19) / 20;
    }
}
