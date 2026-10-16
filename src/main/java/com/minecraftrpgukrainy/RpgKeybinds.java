
package com.minecraftrpgukrainy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class RpgKeybinds {

    public static final KeyMapping OPEN_STATUS =
            new KeyMapping(
                    "key.minecraft_rpg_ukrainy.status",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_N,
                    "key.categories.misc"
            );

    public static final KeyMapping TOGGLE_COMBAT =
            new KeyMapping(
                    "key.minecraft_rpg_ukrainy.toggle_combat",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.misc"
            );

    public static final KeyMapping QUICK_MELEE =
            new KeyMapping(
                    "key.minecraft_rpg_ukrainy.quick_melee",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_X,
                    "key.categories.misc"
            );

    public static final KeyMapping QUICK_DASH =
            new KeyMapping(
                    "key.minecraft_rpg_ukrainy.quick_dash",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_V,
                    "key.categories.misc"
            );

    public static final KeyMapping[] SKILL_SLOTS = {
            createSlot(1, GLFW.GLFW_KEY_1),
            createSlot(2, GLFW.GLFW_KEY_2),
            createSlot(3, GLFW.GLFW_KEY_3),
            createSlot(4, GLFW.GLFW_KEY_4),
            createSlot(5, GLFW.GLFW_KEY_5),
            createSlot(6, GLFW.GLFW_KEY_6),
            createSlot(7, GLFW.GLFW_KEY_7),
            createSlot(8, GLFW.GLFW_KEY_8),
            createSlot(9, GLFW.GLFW_KEY_9)
    };

    private RpgKeybinds() {
    }

    private static KeyMapping createSlot(int slot, int key) {
        return new KeyMapping(
                "key.minecraft_rpg_ukrainy.skill_slot_" + slot,
                InputConstants.Type.KEYSYM,
                key,
                "key.categories.misc"
        );
    }

    @SubscribeEvent
    public static void registerKeyMappings(
            RegisterKeyMappingsEvent event) {

        event.register(OPEN_STATUS);
        event.register(TOGGLE_COMBAT);
        event.register(QUICK_MELEE);
        event.register(QUICK_DASH);

        for (KeyMapping slot : SKILL_SLOTS) {
            event.register(slot);
        }
    }

    @EventBusSubscriber(
            modid = MinecraftRpgUkrainy.MOD_ID,
            bus = EventBusSubscriber.Bus.GAME,
            value = Dist.CLIENT
    )
    public static class ClientEvents {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player == null || minecraft.screen != null) {
                return;
            }

            while (OPEN_STATUS.consumeClick()) {
                minecraft.setScreen(new RpgStatusScreen());
            }

            while (TOGGLE_COMBAT.consumeClick()) {
                minecraft.player.displayClientMessage(
                        Component.literal(
                                "Перемикання режиму бою: потрібне підключення системи бою."
                        ),
                        true
                );
            }

            while (QUICK_MELEE.consumeClick()) {
                minecraft.player.displayClientMessage(
                        Component.literal(
                                "Швидка навичка: Ближній бій — потрібне підключення навички."
                        ),
                        true
                );
            }

            while (QUICK_DASH.consumeClick()) {
                minecraft.player.displayClientMessage(
                        Component.literal(
                                "Швидка навичка: Ривок — потрібне підключення навички."
                        ),
                        true
                );
            }

            for (int i = 0; i < SKILL_SLOTS.length; i++) {
                while (SKILL_SLOTS[i].consumeClick()) {
                    final int slot = i + 1;

                    minecraft.player.displayClientMessage(
                            Component.literal(
                                    "Слот навички " + slot
                                            + ": навичку ще не призначено."
                            ),
                            true
                    );
                }
            }
        }
    }
}
