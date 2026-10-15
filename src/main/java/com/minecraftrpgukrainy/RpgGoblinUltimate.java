package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;

public final class RpgGoblinUltimate {

    private RpgGoblinUltimate() {
    }

    public static final int REQUIRED_PERCEPTION = 30;

    public static final int DURATION_SECONDS = 60;
    public static final int DURATION_TICKS = DURATION_SECONDS * 20;

    public static final int COOLDOWN_SECONDS = 180;
    public static final int COOLDOWN_TICKS = COOLDOWN_SECONDS * 20;

    private static final int RESISTANCE_AMPLIFIER = 0;

    private static final ResourceLocation DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "goblin_ultimate_damage"
            );

    private static final ResourceLocation SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "goblin_ultimate_speed"
            );

    private static final ResourceLocation ARMOR_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "goblin_ultimate_armor"
            );

    /**
     * Активує ультимативну навичку гобліна.
     *
     * @return true, якщо навичку активовано.
     */
    public static boolean activate(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }

        // Перевіряємо вимогу до Сприйняття через наявні дані гравця.
        RpgPlayerData data = RpgPlayerData.get(player);

        if (data == null || data.getPerception() < REQUIRED_PERCEPTION) {
            return false;
        }

        // Не дозволяємо повторно активувати навичку під час дії
        // або перезаряджання.
        if (player.getPersistentData().getInt("rpg_goblin_ultimate_cooldown") > 0) {
            return false;
        }

        // +50% до завданої шкоди.
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(DAMAGE_MODIFIER_ID);
            damage.addTransientModifier(
                    new AttributeModifier(
                            DAMAGE_MODIFIER_ID,
                            0.50,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +50% до швидкості пересування.
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER_ID);
            speed.addTransientModifier(
                    new AttributeModifier(
                            SPEED_MODIFIER_ID,
                            0.50,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        // +8 одиниць броні на час дії навички.
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(ARMOR_MODIFIER_ID);
            armor.addTransientModifier(
                    new AttributeModifier(
                            ARMOR_MODIFIER_ID,
                            8.0,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        // Додаткове зниження отриманої шкоди.
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                DURATION_TICKS,
                RESISTANCE_AMPLIFIER,
                false,
                true,
                true
        ));

        // Таймери зберігаються в даних гравця.
        player.getPersistentData().putInt(
                "rpg_goblin_ultimate_duration",
                DURATION_TICKS
        );

        player.getPersistentData().putInt(
                "rpg_goblin_ultimate_cooldown",
                COOLDOWN_TICKS
        );

        return true;
    }

    /**
     * Знімає бонуси після завершення дії навички.
     * Виклик цього методу потрібно під'єднати до серверного тика.
     */
    public static void tick(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        int duration = player.getPersistentData()
                .getInt("rpg_goblin_ultimate_duration");

        int cooldown = player.getPersistentData()
                .getInt("rpg_goblin_ultimate_cooldown");

        if (cooldown > 0) {
            player.getPersistentData().putInt(
                    "rpg_goblin_ultimate_cooldown",
                    cooldown - 1
            );
        }

        if (duration <= 0) {
            return;
        }

        duration--;

        player.getPersistentData().putInt(
                "rpg_goblin_ultimate_duration",
                duration
        );

        if (duration == 0) {
            removeModifiers(player);
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        }
    }

    private static void removeModifiers(ServerPlayer player) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(DAMAGE_MODIFIER_ID);
        }

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER_ID);
        }

        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(ARMOR_MODIFIER_ID);
        }
    }
}
