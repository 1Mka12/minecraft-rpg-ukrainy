
package com.minecraftrpgukrainy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class RpgKeybinds {

    public static final KeyMapping OPEN_STATUS = key(
            "status", GLFW.GLFW_KEY_N
    );

    public static final KeyMapping TOGGLE_COMBAT = key(
            "toggle_combat", GLFW.GLFW_KEY_R
    );

    public static final KeyMapping QUICK_MELEE = key(
            "quick_melee", GLFW.GLFW_KEY_X
    );

    public static final KeyMapping QUICK_DASH = key(
            "quick_dash", GLFW.GLFW_KEY_V
    );

    public static final KeyMapping[] SKILL_SLOTS = {
            key("skill_slot_1", GLFW.GLFW_KEY_1),
            key("skill_slot_2", GLFW.GLFW_KEY_2),
            key("skill_slot_3", GLFW.GLFW_KEY_3),
            key("skill_slot_4", GLFW.GLFW_KEY_4),
            key("skill_slot_5", GLFW.GLFW_KEY_5),
            key("skill_slot_6", GLFW.GLFW_KEY_6),
            key("skill_slot_7", GLFW.GLFW_KEY_7),
            key("skill_slot_8", GLFW.GLFW_KEY_8),
            key("skill_slot_9", GLFW.GLFW_KEY_9)
    };

    private RpgKeybinds() {
    }

    private static KeyMapping key(String name, int keyCode) {
        return new KeyMapping(
                "key.minecraft_rpg_ukrainy." + name,
                InputConstants.Type.KEYSYM,
                keyCode,
                "key.categories.misc"
        );
    }

    @SubscribeEvent
    public static void registerKeyMappings(
            RegisterKeyMappingsEvent event
    ) {
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

            while (OPEN_STATUS.consumeClick()) {
                if (minecraft.player != null
                        && minecraft.screen == null) {
                    minecraft.setScreen(new RpgStatusScreen());
                }
            }

            if (minecraft.player == null || minecraft.screen != null) {
                return;
            }

            while (TOGGLE_COMBAT.consumeClick()) {
                PacketDistributor.sendToServer(
                        new RpgSkillPayload(
                                RpgSkillPayload.TOGGLE_COMBAT
                        )
                );
            }

            while (QUICK_MELEE.consumeClick()) {
                PacketDistributor.sendToServer(
                        new RpgSkillPayload(
                                RpgSkillPayload.QUICK_MELEE
                        )
                );
            }

            while (QUICK_DASH.consumeClick()) {
                PacketDistributor.sendToServer(
                        new RpgSkillPayload(
                                RpgSkillPayload.QUICK_DASH
                        )
                );
            }

            for (int i = 0; i < SKILL_SLOTS.length; i++) {
                while (SKILL_SLOTS[i].consumeClick()) {
                    PacketDistributor.sendToServer(
                            new RpgSkillPayload(i + 1)
                    );
                }
            }
        }
    }
}
