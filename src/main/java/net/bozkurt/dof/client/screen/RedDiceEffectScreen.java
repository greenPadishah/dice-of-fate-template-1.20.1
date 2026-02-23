package net.bozkurt.dof.client.screen;

import net.bozkurt.dof.effect.DiceEffect;
import net.bozkurt.dof.effect.DiceEffectRegistry;
import net.bozkurt.dof.network.DiceOfFateNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;

public class RedDiceEffectScreen extends Screen {
    private final Hand hand;
    private final List<DiceEffect> allEffects;
    private final List<ButtonWidget> effectButtons = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int VISIBLE_BUTTONS = 10;
    private static final int BUTTON_HEIGHT = 22;

    public RedDiceEffectScreen(Hand hand) {
        super(Text.literal("Red Dice - Admin Mode"));
        this.hand = hand;
        this.allEffects = DiceEffectRegistry.getAllEffects();
    }

    @Override
    protected void init() {
        int buttonWidth = 300;
        int centerX = this.width / 2;
        int startY = 40;

        effectButtons.clear();

        for (int i = 0; i < allEffects.size(); i++) {
            final int index = i;
            DiceEffect effect = allEffects.get(i);
            
            String translationKey = "effect.dice-of-fate." + effect.id();
            Text displayText = Text.translatable(translationKey);
            
            ButtonWidget button = ButtonWidget.builder(displayText, btn -> selectEffect(index))
            .dimensions(centerX - buttonWidth / 2, startY + (i * BUTTON_HEIGHT), buttonWidth, 20)
            .build();
            
            effectButtons.add(button);
        }

        updateVisibleButtons();
    }

    private void updateVisibleButtons() {
        clearChildren();
        
        int endIndex = Math.min(scrollOffset + VISIBLE_BUTTONS, effectButtons.size());
        for (int i = scrollOffset; i < endIndex; i++) {
            ButtonWidget button = effectButtons.get(i);
            int displayIndex = i - scrollOffset;
            button.setY(40 + (displayIndex * BUTTON_HEIGHT));
            addDrawableChild(button);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int maxScroll = Math.max(0, effectButtons.size() - VISIBLE_BUTTONS);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) amount));
        updateVisibleButtons();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            this.title, 
            this.width / 2, 
            15, 
            0xFF5555
        );
        
        String countText = "Effects: " + allEffects.size() + 
                          " | Showing: " + (scrollOffset + 1) + "-" + 
                          Math.min(scrollOffset + VISIBLE_BUTTONS, allEffects.size());
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            Text.literal(countText), 
            this.width / 2, 
            this.height - 20, 
            0xAAAAAA
        );
        
        super.render(context, mouseX, mouseY, delta);
    }

    private void selectEffect(int effectIndex) {
        var buf = PacketByteBufs.create();
        buf.writeInt(hand.ordinal());
        buf.writeInt(effectIndex);
        ClientPlayNetworking.send(DiceOfFateNetworking.RED_DICE_EFFECT_PACKET, buf);

        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
