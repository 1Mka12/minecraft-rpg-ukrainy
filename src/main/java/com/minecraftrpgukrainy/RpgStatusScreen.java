
package com.minecraftrpgukrainy;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class RpgStatusScreen extends Screen {

    public RpgStatusScreen() {
        super(Component.literal("СТАТУС ПЕРСОНАЖА"));
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick) {

        this.renderBackground(
                graphics, mouseX, mouseY, partialTick
        );

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int panelWidth = Math.min(340, this.width - 20);
        int panelHeight = Math.min(250, this.height - 20);

        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;

        graphics.fill(
                left, top,
                left + panelWidth, top + panelHeight,
                0xEE151923
        );

        graphics.renderOutline(
                left, top, panelWidth, panelHeight,
                0xFFB99B59
        );

        graphics.drawCenteredString(
                this.font,
                "СТАТУС ПЕРСОНАЖА",
                centerX,
                top + 12,
                0xFFFFD875
        );

        Player player = this.minecraft.player;

        if (player != null) {
            int level = RpgPlayerLevel.getLevel(player);
            int xp = RpgPlayerLevel.getExperience(player);
            int required =
                    RpgPlayerLevel.getExperienceForNextLevel(player);

            graphics.drawString(
                    this.font,
                    "Герой: " + player.getName().getString(),
                    left + 14, top + 36, 0xFFFFFFFF
            );

            graphics.drawString(
                    this.font,
                    "Рівень: " + level,
                    left + 14, top + 52, 0xFFFFD875
            );

            graphics.drawString(
                    this.font,
                    "Досвід: " + xp + " / " + required,
                    left + 14, top + 66, 0xFF80E8A1
            );

            graphics.drawString(
                    this.font,
                    "Сила: " + RpgPlayerData.getPoints(
                            player, RpgStats.Stat.STRENGTH),
                    left + 14, top + 90, 0xFFFFFFFF
            );

            graphics.drawString(
                    this.font,
                    "Спритність: " + RpgPlayerData.getPoints(
                            player, RpgStats.Stat.AGILITY),
                    left + 14, top + 106, 0xFFFFFFFF
            );

            graphics.drawString(
                    this.font,
                    "Витривалість: " + RpgPlayerData.getPoints(
                            player, RpgStats.Stat.ENDURANCE),
                    left + 14, top + 122, 0xFFFFFFFF
            );

            graphics.drawString(
                    this.font,
                    "Сприйняття: " + RpgPlayerData.getPoints(
                            player, RpgStats.Stat.PERCEPTION),
                    left + 14, top + 138, 0xFFFFFFFF
            );

            graphics.drawString(
                    this.font,
                    "Інтелект: " + RpgPlayerData.getPoints(
                            player, RpgStats.Stat.INTELLIGENCE),
                    left + 14, top + 154, 0xFFFFFFFF
            );

            graphics.drawString(
                    this.font,
                    "Вільні очки: " +
                            RpgPlayerData.getFreePoints(player),
                    left + 14, top + 178, 0xFF80DFFF
            );
        }

        graphics.drawCenteredString(
                this.font,
                "Натисни ESC, щоб закрити",
                centerX,
                top + panelHeight - 18,
                0xFFBBBBBB
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
