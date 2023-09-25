package continued.hideaway.mod.feat.ui;

import continued.hideaway.mod.HideawayPlus;
import continued.hideaway.mod.feat.ext.AbstractContainerScreenAccessor;
import continued.hideaway.mod.feat.lifecycle.Lifecycle;
import continued.hideaway.mod.feat.lifecycle.Task;
import continued.hideaway.mod.util.StaticValues;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static continued.hideaway.mod.util.StaticValues.friendsMenuClosed;

public class FriendsListUI {
    private static ChestMenu oldMenu = null;

    public static void tick() {
        if (!StaticValues.friendsList.contains(HideawayPlus.client().player.getName().getString())) StaticValues.friendsList.add(HideawayPlus.client().player.getName().getString());
        if (StaticValues.friendsCheck) return;
        if (HideawayPlus.client().screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen<ChestMenu> abstractContainerScreen = (AbstractContainerScreen<ChestMenu>) HideawayPlus.client().screen;
            ChestMenu menu = abstractContainerScreen.getMenu();
            if (oldMenu != null && oldMenu == menu) return;
            oldMenu = menu;

            List<ItemStack> allItems = new ArrayList<>(menu.getItems());
            boolean hasMorePages = menu.getItems().stream().anyMatch(itemStack -> itemStack.getItem() == Items.PAPER && itemStack.getTag().getAsString().contains("→"));

            HideawayPlus.lifecycle().addAsync(
                    "friendsCheck",
                    CompletableFuture.runAsync(() -> {
                        List<ItemStack> newAllItems = new ArrayList<>(allItems);
                        for (ItemStack itemStack : newAllItems) {
                            if (itemStack.getItem() == Items.PLAYER_HEAD) {
                                if (itemStack.getTag().toString().contains("Left click to Accept")) continue;
                                String name = itemStack.getTag().getCompound("SkullOwner").getString("Name");
                                if (!StaticValues.friendsList.contains(name)) StaticValues.friendsList.add(name);
                            }
                        }
                    })
            );

            if (!hasMorePages) {
                StaticValues.friendsCheck = true;
                HideawayPlus.client().setScreen(null);
            } else {
                Slot paperSlot = menu.slots.stream().filter(slot -> slot.getItem().getItem() == Items.PAPER && slot.getItem().getTag().getAsString().contains("→")).findFirst().orElse(null);
                ((AbstractContainerScreenAccessor) abstractContainerScreen).hp$slotChange(paperSlot, 0, 0, ClickType.PICKUP);
            }
        } else {

            LastSeenMessages.Update messages = new LastSeenMessages.Update(0, new BitSet());

            Instant now = Instant.now();
            HideawayPlus.client().player.connection.send(
                    new ServerboundChatCommandPacket(
                            "friend",
                            now,
                            0L,
                            ArgumentSignatures.EMPTY,
                            messages)
            );
        }
    }
}
