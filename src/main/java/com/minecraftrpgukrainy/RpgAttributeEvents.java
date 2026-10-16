
package com.minecraftrpgukrainy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

    private static final double HEALTH_PER_POINT = 2.0;
    private static final double ARMOR_PER_POINT = 0.5;

    private static final String MODIFIER_PREFIX =
            "minecraft_rpg_ukrainy_";

    private static final ResourceLocation HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    MODIFIER_PREFIX + "endurance_health"
            );

    private static final ResourceLocation ARMOR_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    MODIFIER_PREFIX + "endurance_armor"
            );

    private RpgAttributeEvents() {
    }

    /**
     * Оновлення після входу гравця.
     */
    @SubscribeEvent
    public static void onPlayerLogin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            updateAttributes(player);
        }
    }

    /**
     * Оновлення після відродження.
     */
    @SubscribeEvent
    public static void onPlayerRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            updateAttributes(player);
        }
    }

    /**
     * Перевірка після серверного тику.
     *
     * Це також підхоплює зміну характеристик,
     * виконану через RpgAllocateStatPayload.
     */
    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        updateAttributes(player);
    }

    /**
     * Застосовує бонуси Витривалості.
     */
    public static void updateAttributes(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        int totalPoints = RpgPlayerData.getTotalAllocatedPoints(player);
        int endurance = RpgPlayerData.getPoints(
                player,
                RpgStats.Stat.ENDURANCE
        );

        double effectiveEndurance = getEffectiveEndurance(
                endurance,
                totalPoints
        );

        double healthBonus =
                effectiveEndurance * HEALTH_PER_POINT;

        double armorBonus =
                effectiveEndurance * ARMOR_PER_POINT;

        applyModifier(
                player.getAttribute(Attributes.MAX_HEALTH),
                HEALTH_ID,
                healthBonus
        );

        applyModifier(
                player.getAttribute(Attributes.ARMOR),
                ARMOR_ID,
                armorBonus
        );

        // Не лікуємо гравця при збільшенні максимуму.
        // Якщо максимум зменшився нижче поточного здоров'я,
        // обмежуємо здоров'я новим максимумом.
        double maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();

        if (currentHealth > maxHealth) {
            player.setHealth((float) maxHealth);
        }
    }

    /**
     * Повторює формулу RpgStats.getEffectivePoints()
     * для збережених даних гравця.
     */
    private static double getEffectiveEndurance(
            int endurance,
            int totalPoints
    ) {
        if (endurance <= 0 || totalPoints <= 0) {
            return 0.0;
        }

        double softCap =
                totalPoints * RpgStats.SOFT_CAP_SHARE;

        if (endurance <= softCap) {
            return endurance;
        }

        return softCap
                + (endurance - softCap)
                * RpgStats.ABOVE_SOFT_CAP_EFFICIENCY;
    }

    /**
     * Оновлює тільки власний модифікатор.
     * Інші модифікатори, зокрема бонус броні
     * ультимативної навички, залишаються недоторканими.
     */
    private static void applyModifier(
            AttributeInstance attribute,
            ResourceLocation id,
            double amount
    ) {
        if (attribute == null) {
            return;
        }

        AttributeModifier existing = attribute.getModifier(id);

        // Не оновлюємо атрибут без потреби.
        if (existing != null
                && Double.compare(existing.amount(), amount) == 0
                && existing.operation()
                == AttributeModifier.Operation.ADD_VALUE) {
            return;
        }

        attribute.addOrUpdateTransientModifier(
                new AttributeModifier(
                        id,
                        amount,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }
}
