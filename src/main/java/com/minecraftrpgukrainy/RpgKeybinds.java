
package com.minecraftrpgukrainy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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

    private RpgKeybinds() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(
            RegisterKeyMappingsEvent event) {
        event.register(OPEN_STATUS);
    }

    @EventBusSubscriber(
            modid = MinecraftRpgUkrainy.MOD_ID,
            bus = EventBusSubscriber.Bus.GAME,
            value = Dist.CLIENT
    )
    public static class ClientEvents {

        @SubscribeEvent
        public static void onClientTick(
                ClientTickEvent.Post event) {

            Minecraft minecraft = Minecraft.getInstance();

            while (OPEN_STATUS.consumeClick()) {
                if (minecraft.player == null
                        || minecraft.screen != null) {
                    continue;
                }

                minecraft.setScreen(new RpgStatusScreen());
            }
        }
    }
}
