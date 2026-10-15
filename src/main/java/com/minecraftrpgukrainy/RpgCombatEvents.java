package com.minecraftrpgukrainy;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgGoblinUltimate {

    private static final String COOLDOWN_KEY =
            "minecraft_rpg_ukrainy_goblin_ultimate_cooldown";

    private static final int DURATION_TICKS = 60 * 20;
    private static final int COOLDOWN_TICKS = 5 * 60 * 20;

    private RpgGoblinUltimate() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(
            RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("rpgultimate")
                        .executes(context ->
                                activate(
                                        context.getSource()
                                                .getPlayerOrException()
                                )
                        )
        );
    }

    private static int activate(ServerPlayer player) {

        // Потрібно 30 очок Сприйняття.
        if (!RpgPlayerData.isSkillUnlocked(
                player,
                RpgStats.Skill.GOBLIN_ULTIMATE)) {

            player.sendSystemMessage(
                    Component.literal(
                            "§cУльтимативна навичка Гобліна "
                                    + "потребує 30 очок Сприйняття!"
                    )
            );

            return 0;
        }

        long now = player.level().getGameTime();

        long cooldownEnd = player.getPersistentData()
                .getLong(COOLDOWN_KEY);

        if (now < cooldownEnd) {
            long secondsLeft = (cooldownEnd - now + 19) / 20;

            player.sendSystemMessage(
                    Component.literal(
                            "§eНавичка ще перезаряджається: "
                                    + secondsLeft + " с."
                    )
            );

            return 0;
        }

        // Тривалість усіх ефектів — 60 секунд.
        player.addEffect(new MobEffectInstance(
                MobEffects.STRENGTH,
                DURATION_TICKS,
                1
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.SPEED,
                DURATION_TICKS,
                1
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.RESISTANCE,
                DURATION_TICKS,
                0
        ));

        player.addEffect(new MobEffectInstance(
                MobEffects.HASTE,
                DURATION_TICKS,
                1
        ));

        player.getPersistentData().putLong(
                COOLDOWN_KEY,
                now + COOLDOWN_TICKS
        );

        player.sendSystemMessage(
                Component.literal(
                        "§6§lУЛЬТИМАТИВНА НАВИЧКА ГОБЛІНА!"
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "§aНа 60 секунд посилено атаку, "
                                + "швидкість, захист і швидкість копання."
                )
        );

        return 1;
    }
}
