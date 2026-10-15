package com.minecraftrpgukrainy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RpgSkillPayload(int action) implements CustomPacketPayload {

    public static final Type<RpgSkillPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "minecraft_rpg_ukrainy",
                    "skill_action"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, RpgSkillPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RpgSkillPayload::action,
                    RpgSkillPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            RpgSkillPayload payload,
            IPayloadContext context) {

        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            int action = payload.action();

            if (action == 0) {
                boolean current = player.getPersistentData()
                        .getBoolean("minecraft_rpg_ukrainy_combat_mode");

                player.getPersistentData().putBoolean(
                        "minecraft_rpg_ukrainy_combat_mode",
                        !current
                );
                return;
            }

            // Захист від невідомих номерів слотів.
            if (action < 1 || action > 9) {
                return;
            }

            // Слот 6 — ультимативна навичка гобліна.
            if (action == 6) {
                RpgGoblinUltimate.activate(player);
                return;
            }

            // Демонстраційні навички слотів 1–5 та 7–9.
            // Їх можна буде замінити власними RPG-навичками.
            switch (action) {
                case 1 -> player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST, 200, 0));

                case 2 -> player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED, 200, 0));

                case 3 -> player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE, 200, 0));

                case 4 -> player.addEffect(new MobEffectInstance(
                        MobEffects.JUMP, 200, 1));

                case 5 -> player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION, 100, 0));

                case 7 -> player.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SPEED, 200, 0));

                case 8 -> player.addEffect(new MobEffectInstance(
                        MobEffects.NIGHT_VISION, 400, 0));

                case 9 -> player.addEffect(new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE, 200, 0));

                default -> {
                }
            }
        });
    }
}
