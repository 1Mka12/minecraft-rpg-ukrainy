package com.minecraftrpgukrainy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgTouchControls {

    private static final int BUTTON_WIDTH = 28;
    private static final int BUTTON_HEIGHT = 26;
    private static final int GAP = 3;

    private RpgTouchControls() {
    }

    private static int panelWidth() {
        return 9 * BUTTON_WIDTH + 8 * GAP;
    }

    private static int panelX(int screenWidth) {
        return Math.max(2, (screenWidth - panelWidth()) / 2);
    }

    private static int panelY(int screenHeight) {
        return screenHeight - 72;
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.screen != null) {
            return;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        GuiGraphics graphics = event.getGuiGraphics();

        int x = panelX(width);
        int y = panelY(height);

        boolean combatMode = mc.player.getPersistentData()
                .getBoolean("minecraft_rpg_ukrainy_combat_mode");

        // Кнопка режиму бою.
        int combatY = y - 29;
        int combatColor = combatMode ? 0xD0208040 : 0xD0444444;

        graphics.fill(x, combatY, x + 100, combatY + 23, combatColor);
        graphics.renderOutline(x, combatY, 100, 23, 0xFFFFFFFF);

        String combatText = combatMode
                ? "R: БІЙ УВІМК."
                : "R: БІЙ ВИМК.";

        graphics.drawString(
                mc.font,
                combatText,
                x + 5,
                combatY + 8,
                0xFFFFFFFF,
                false
        );

        // Дев'ять сенсорних кнопок.
        for (int slot = 1; slot <= 9; slot++) {
            int bx = x + (slot - 1) * (BUTTON_WIDTH + GAP);

            int color = slot == 6
                    ? 0xD0A040C0
                    : 0xD0303030;

            graphics.fill(
                    bx, y,
                    bx + BUTTON_WIDTH,
                    y + BUTTON_HEIGHT,
                    color
            );

            graphics.renderOutline(
                    bx, y,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    0xFFFFFFFF
            );

            String label = Integer.toString(slot);

            graphics.drawString(
                    mc.font,
                    label,
                    bx + (BUTTON_WIDTH - mc.font.width(label)) / 2,
                    y + 5,
                    0xFFFFFFFF,
                    false
            );

            String skillLabel = switch (slot) {
                case 1 -> "УД";
                case 2 -> "РИ";
                case 3 -> "ЗА";
                case 4 -> "СТ";
                case 5 -> "ЛІ";
                case 6 -> "УЛ";
                case 7 -> "ША";
                case 8 -> "ЗІ";
                case 9 -> "ВО";
                default -> "";
            };

            graphics.drawString(
                    mc.font,
                    skillLabel,
                    bx + (BUTTON_WIDTH - mc.font.width(skillLabel)) / 2,
                    y + 15,
                    0xFFFFFFFF,
                    false
            );
        }
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (event.getAction() != GLFW.GLFW_PRESS
                || event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.screen != null) {
            return;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        double mouseX = mc.mouseHandler.xpos()
                * width / mc.getWindow().getScreenWidth();

        double mouseY = mc.mouseHandler.ypos()
                * height / mc.getWindow().getScreenHeight();

        int x = panelX(width);
        int y = panelY(height);

        // Перемикання бойового режиму.
        int combatY = y - 29;

        if (mouseX >= x && mouseX < x + 100
                && mouseY >= combatY && mouseY < combatY + 23) {

            PacketDistributor.sendToServer(
                    new RpgSkillPayload(0)
            );

            event.setCanceled(true);
            return;
        }

        // Активація вибраного слота.
        if (mouseY < y || mouseY >= y + BUTTON_HEIGHT) {
            return;
        }

        for (int slot = 1; slot <= 9; slot++) {
            int bx = x + (slot - 1) * (BUTTON_WIDTH + GAP);

            if (mouseX >= bx && mouseX < bx + BUTTON_WIDTH) {
                PacketDistributor.sendToServer(
                        new RpgSkillPayload(slot)
                );

                event.setCanceled(true);
                return;
            }
        }
    }
}
