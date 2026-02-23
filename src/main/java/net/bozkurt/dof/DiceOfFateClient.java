package net.bozkurt.dof;

import net.bozkurt.dof.network.DiceOfFateNetworking;
import net.bozkurt.dof.client.screen.WagerScreen;
import net.bozkurt.dof.effect.DiceType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class DiceOfFateClient implements ClientModInitializer {
    private static final int ROLL_DURATION_TICKS = 60;
    private static final int FINAL_HOLD_TICKS = 12;
    private static final int STEP_TICKS = 2;
    private static final float SCALE = 2.5f;

    private static boolean active;
    private static int ticksRemaining;
    private static int finalRoll;
    private static int lastStepIndex = -1;
    private static boolean finalSoundPlayed;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DiceOfFateNetworking.DICE_ROLL_PACKET, (client, handler, buf, responseSender) -> {
            int roll = buf.readInt();
            client.execute(() -> startRoll(roll));
        });

        ClientPlayNetworking.registerGlobalReceiver(DiceOfFateNetworking.OPEN_WAGER_PACKET, (client, handler, buf, responseSender) -> {
            boolean isBlack = buf.readBoolean();
            int handOrdinal = buf.readInt();
            Hand hand = Hand.values()[Math.max(0, Math.min(Hand.values().length - 1, handOrdinal))];
            DiceType diceType = isBlack ? DiceType.BLACK : DiceType.WHITE;
            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new WagerScreen(diceType, hand));
                }
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!active) {
                return;
            }

            int elapsed = ROLL_DURATION_TICKS - ticksRemaining;
            if (ticksRemaining > FINAL_HOLD_TICKS) {
                int stepIndex = elapsed / STEP_TICKS;
                if (stepIndex != lastStepIndex) {
                    lastStepIndex = stepIndex;
                    playSpinSound(client);
                }
            } else if (!finalSoundPlayed) {
                finalSoundPlayed = true;
                playFinalSound(client);
            }

            ticksRemaining--;
            if (ticksRemaining <= 0) {
                active = false;
            }
        });

        HudRenderCallback.EVENT.register(this::renderRollHud);
    }

    private static void startRoll(int roll) {
        finalRoll = roll;
        ticksRemaining = ROLL_DURATION_TICKS;
        active = true;
        lastStepIndex = -1;
        finalSoundPlayed = false;
    }

    private static void playSpinSound(MinecraftClient client) {
        if (client == null) {
            return;
        }

        client.getSoundManager().play(
            PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 0.6f, 1.4f)
        );
    }

    private static void playFinalSound(MinecraftClient client) {
        if (client == null) {
            return;
        }

        client.getSoundManager().play(
            PositionedSoundInstance.master(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, 0.9f, 1.0f)
        );
    }

    private void renderRollHud(DrawContext drawContext, float tickDelta) {
        if (!active) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) {
            return;
        }

        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int lineHeight = client.textRenderer.fontHeight + 4;
        int elapsed = ROLL_DURATION_TICKS - ticksRemaining;

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(centerX, centerY, 0);
        drawContext.getMatrices().scale(SCALE, SCALE, 1.0f);

        if (ticksRemaining <= FINAL_HOLD_TICKS) {
            drawContext.drawCenteredTextWithShadow(
                client.textRenderer,
                Text.literal(String.valueOf(finalRoll)),
                0,
                -client.textRenderer.fontHeight / 2,
                0xFFFFFF
            );
        } else {
            float stepProgress = ((elapsed % STEP_TICKS) + tickDelta) / (float) STEP_TICKS;
            float offsetY = stepProgress * lineHeight;
            int baseIndex = (elapsed / STEP_TICKS) % 6;

            for (int i = -2; i <= 2; i++) {
                int number = ((baseIndex + i) % 6 + 6) % 6 + 1;
                int y = Math.round(i * lineHeight - offsetY - client.textRenderer.fontHeight / 2.0f);
                drawContext.drawCenteredTextWithShadow(
                    client.textRenderer,
                    Text.literal(String.valueOf(number)),
                    0,
                    y,
                    0xFFFFFF
                );
            }
        }

        drawContext.getMatrices().pop();
    }
}
