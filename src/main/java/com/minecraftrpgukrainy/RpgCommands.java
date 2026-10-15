
package com.minecraftrpgukrainy;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgCommands {

    private RpgCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(
            RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("rpglevel")
                        .executes(context -> {
                            ServerPlayer player =
                                    context.getSource().getPlayerOrException();

                            showLevel(player);
                            return Command.SINGLE_SUCCESS;
                        })
        );

        event.getDispatcher().register(
                Commands.literal("rpgstats")
                        .executes(context -> {
                            ServerPlayer player =
                                    context.getSource().getPlayerOrException();

                            showStats(player);
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }

    private static void showLevel(ServerPlayer player) {
        int level = RpgPlayerLevel.getLevel(player);
        int experience = RpgPlayerLevel.getExperience(player);
        int required =
                RpgPlayerLevel.getExperienceForNextLevel(player);

        player.sendSystemMessage(
                Component.literal("§6=== РІВЕНЬ RPG ===")
        );

        player.sendSystemMessage(
                Component.literal("§eРівень: §f" + level)
        );

        player.sendSystemMessage(
                Component.literal(
                        "§aДосвід: §f" + experience + " / " + required
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "§bВільні очки: §f"
                                + RpgPlayerData.getFreePoints(player)
                )
        );
    }

    private static void showStats(ServerPlayer player) {
        player.sendSystemMessage(
                Component.literal("§6=== ХАРАКТЕРИСТИКИ RPG ===")
        );

        showStat(player, RpgStats.Stat.STRENGTH);
        showStat(player, RpgStats.Stat.AGILITY);
        showStat(player, RpgStats.Stat.ENDURANCE);
        showStat(player, RpgStats.Stat.PERCEPTION);
        showStat(player, RpgStats.Stat.INTELLIGENCE);

        player.sendSystemMessage(
                Component.literal(
                        "§bВільні очки: §f"
                                + RpgPlayerData.getFreePoints(player)
                )
        );
    }

    private static void showStat(
            ServerPlayer player, RpgStats.Stat stat) {

        player.sendSystemMessage(
                Component.literal(
                        "§e" + stat.getDisplayName()
                                + ": §f"
                                + RpgPlayerData.getPoints(player, stat)
                                + " §7("
                                + String.format(
                                        java.util.Locale.ROOT,
                                        "%.1f",
                                        RpgPlayerData.getAllocationPercent(
                                                player, stat
                                        )
                                )
                                + "%)"
                )
        );
    }
}
