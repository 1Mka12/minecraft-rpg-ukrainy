
package com.minecraftrpgukrainy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgAttributeEvents {

    private static final ResourceLocation ENDURANCE_HEALTH =
            ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    "endurance_health"
            );

    private static final ResourceLocation ENDURANCE_ARMOR =
            ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    "endurance_armor"
            );

    private static final ResourceLocation AGILITY_SPEED =
            ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    "agility_speed"
            );

    private RpgAttributeEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            updateAttributes(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            updateAttributes(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 20 == 0) {
            updateAttributes(player);
        }
    }

    public static void updateAttributes(ServerPlayer player) {
        double total = RpgPlayerData.getTotalAllocatedPoints(player);

        double endurance = effectivePoints(
                RpgPlayerData.getPoints(
                        player,
                        RpgStats.Stat.ENDURANCE
                ),
                total
        );

        double agility = effectivePoints(
                RpgPlayerData.getPoints(
                        player,
                        RpgStats.Stat.AGILITY
                ),
                total
        );

        // Витривалість: +2 максимального здоров'я за ефективне очко.
        applyModifier(
                player,
                Attributes.MAX_HEALTH,
                ENDURANCE_HEALTH,
                endurance * 2.0
        );

        // Витривалість: +0,5 броні за ефективне очко.
        applyModifier(
                player,
                Attributes.ARMOR,
                ENDURANCE_ARMOR,
                endurance * 0.5
        );

        // Спритність: до +50% швидкості руху.
        // Кожне ефективне очко дає 1%, максимум 50%.
        double speedBonus = Math.min(0.50, agility * 0.01);

        applyModifier(
                player,
                Attributes.MOVEMENT_SPEED,
                AGILITY_SPEED,
                speedBonus
        );

        // Не лікуємо автоматично, якщо максимальне здоров'я зросло.
        // Якщо максимум зменшився, поточне здоров'я обмежується ним.
        double maxHealth = player.getMaxHealth();

        if (player.getHealth() > maxHealth) {
            player.setHealth((float) maxHealth);
        }
    }

    private static double effectivePoints(
            int allocated,
            double total
    ) {
        if (allocated <= 0 || total <= 0.0) {
            return 0.0;
        }

        double softCap = total * RpgStats.SOFT_CAP_SHARE;

        if (allocated <= softCap) {
            return allocated;
        }

        return softCap
                + (allocated - softCap)
                * RpgStats.ABOVE_SOFT_CAP_EFFICIENCY;
    }

    private static void applyModifier(
            ServerPlayer player,
            net.minecraft.core.Holder<Attribute> attribute,
            ResourceLocation id,
            double amount
    ) {
        AttributeInstance instance =
                player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);

        if (amount == 0.0) {
            return;
        }

        instance.addTransientModifier(
                new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                        id,
                        amount,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );
    }
}
