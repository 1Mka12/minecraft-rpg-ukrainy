package com.minecraftrpgukrainy;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class RpgStatusScreen extends Screen {

    private static final int ROW_HEIGHT = 27;

    private static final RpgStats.Stat[] STATS = {
            RpgStats.Stat.STRENGTH,
            RpgStats.Stat.AGILITY,
            RpgStats.Stat.ENDURANCE,
            RpgStats.Stat.PERCEPTION,
            RpgStats.Stat.INTELLIGENCE
    };

    public RpgStatusScreen() {
        super(Component.literal("СТАТУС ПЕРСОНАЖА"));
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(390, this.width - 20);
        int panelHeight = Math.min(290, this.height - 20);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;

        int buttonWidth = 24;
        int plusX = left + panelWidth - 43;
        int minusX = left + panelWidth - 72;

        // Запитуємо актуальні дані сервера.
        if (this.minecraft != null && this.minecraft.player != null) {
            PacketDistributor.sendToServer(
                    new RpgAllocateStatPayload(-1, false)
            );
        }

        for (int i = 0; i < STATS.length; i++) {
            final RpgStats.Stat stat = STATS[i];
            int y = top + 93 + i * ROW_HEIGHT;

            addRenderableWidget(
                    Button.builder(
                            Component.literal("+"),
                            button -> allocate(stat)
                    ).bounds(plusX, y - 3, buttonWidth, 20).build()
            );

            addRenderableWidget(
                    Button.builder(
                            Component.literal("-"),
                            button -> remove(stat)
                    ).bounds(minusX, y - 3, buttonWidth, 20).build()
            );
        }
    }

    private void allocate(RpgStats.Stat stat) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }

        PacketDistributor.sendToServer(
                new RpgAllocateStatPayload(stat.ordinal(), false)
        );
    }

    private void remove(RpgStats.Stat stat) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }

        PacketDistributor.sendToServer(
                new RpgAllocateStatPayload(stat.ordinal(), true)
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick) {

        renderBackground(graphics, mouseX, mouseY, partialTick);

        int panelWidth = Math.min(390, this.width - 20);
        int panelHeight = Math.min(290, this.height - 20);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;
        int centerX = this.width / 2;

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
                font,
                "СТАТУС ПЕРСОНАЖА",
                centerX, top + 12,
                0xFFFFD875
        );

        Player player = this.minecraft == null
                ? null
                : this.minecraft.player;

        if (player != null) {
            int level = RpgClientStatsCache.getLevel();
            int experience = RpgClientStatsCache.getExperience();

            graphics.drawString(
                    font,
                    "Герой: " + player.getName().getString(),
                    left + 14, top + 34,
                    0xFFFFFFFF
            );

            graphics.drawString(
                    font,
                    "Рівень: " + level,
                    left + 14, top + 50,
                    0xFFFFD875
            );

            graphics.drawString(
                    font,
                    "Досвід: " + experience + " / "
                            + getRequiredExperienceForDisplay(level),
                    left + 14, top + 64,
                    0xFF80E8A1
            );

            for (int i = 0; i < STATS.length; i++) {
                RpgStats.Stat stat = STATS[i];
                int y = top + 93 + i * ROW_HEIGHT;

                graphics.drawString(
                        font,
                        stat.getDisplayName() + ": "
                                + getCachedStat(stat),
                        left + 14, y + 3,
                        0xFFFFFFFF
                );
            }

            graphics.drawString(
                    font,
                    "Вільні очки: "
                            + RpgClientStatsCache.getFreePoints(),
                    left + 14, top + 238,
                    0xFF80DFFF
            );
        }

        graphics.drawCenteredString(
                font,
                "ESC — закрити",
                centerX, top + panelHeight - 17,
                0xFFBBBBBB
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private int getRequiredExperienceForDisplay(int level) {
        if (level <= 1) {
            return 8;
        }

        if (level >= 32) {
            return Integer.MAX_VALUE;
        }

        return 8 << (level - 1);
    }

    private int getCachedStat(RpgStats.Stat stat) {
        return switch (stat) {
            case STRENGTH ->
                    RpgClientStatsCache.getStrength();
            case AGILITY ->
                    RpgClientStatsCache.getAgility();
            case ENDURANCE ->
                    RpgClientStatsCache.getEndurance();
            case PERCEPTION ->
                    RpgClientStatsCache.getPerception();
            case INTELLIGENCE ->
                    RpgClientStatsCache.getIntelligence();
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
