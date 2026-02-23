package net.bozkurt.dof.client.screen;

import net.bozkurt.dof.effect.DiceType;
import net.bozkurt.dof.network.DiceOfFateNetworking;
import net.bozkurt.dof.wager.WagerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class WagerScreen extends Screen {
    private final DiceType diceType;
    private final Hand hand;
    private ButtonWidget ironButton;
    private ButtonWidget diamondButton;
    private ButtonWidget lifeButton;

    public WagerScreen(DiceType diceType, Hand hand) {
        super(Text.literal("Choose Your Wager"));
        this.diceType = diceType;
        this.hand = hand;
    }

    @Override
    protected void init() {
        int buttonWidth = 220;
        int buttonHeight = 20;
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int startY = centerY - 30;

        ironButton = addDrawableChild(ButtonWidget.builder(Text.literal("Iron Bar"), button -> sendWager(WagerType.IRON))
            .dimensions(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight)
            .tooltip(Tooltip.of(Text.literal("Low Price, Low Risk")))
            .build());

        diamondButton = addDrawableChild(ButtonWidget.builder(Text.literal("Diamond"), button -> sendWager(WagerType.DIAMOND))
            .dimensions(centerX - buttonWidth / 2, startY + 26, buttonWidth, buttonHeight)
            .tooltip(Tooltip.of(Text.literal("One Diamond is not that much right?")))
            .build());

        lifeButton = addDrawableChild(ButtonWidget.builder(Text.literal("Your Life"), button -> sendWager(WagerType.LIFE))
            .dimensions(centerX - buttonWidth / 2, startY + 52, buttonWidth, buttonHeight)
            .tooltip(Tooltip.of(Text.literal("Are you really that greedy?")))
            .build());

        updateButtonStates();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        updateButtonStates();
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        drawEnabledOutline(context, ironButton);
        drawEnabledOutline(context, diamondButton);
        drawEnabledOutline(context, lifeButton);
        super.render(context, mouseX, mouseY, delta);
    }

    private void updateButtonStates() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        PlayerInventory inventory = client.player.getInventory();
        ironButton.active = hasItem(inventory, Items.IRON_INGOT);
        diamondButton.active = hasItem(inventory, Items.DIAMOND);
        lifeButton.active = true;
    }

    private boolean hasItem(PlayerInventory inventory, Item item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return true;
            }
        }

        for (int i = 0; i < inventory.offHand.size(); i++) {
            ItemStack stack = inventory.offHand.get(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    private void drawEnabledOutline(DrawContext context, ButtonWidget button) {
        if (button != null && button.active) {
            context.drawBorder(button.getX() - 2, button.getY() - 2, button.getWidth() + 4, button.getHeight() + 4, 0xFFFFD700);
        }
    }

    private void sendWager(WagerType wager) {
        var buf = PacketByteBufs.create();
        buf.writeBoolean(diceType == DiceType.BLACK);
        buf.writeInt(hand.ordinal());
        buf.writeInt(wager.ordinal());
        ClientPlayNetworking.send(DiceOfFateNetworking.WAGER_PACKET, buf);

        MinecraftClient.getInstance().setScreen(null);
    }
}
