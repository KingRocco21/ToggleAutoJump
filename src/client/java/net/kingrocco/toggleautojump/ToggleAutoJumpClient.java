package net.kingrocco.toggleautojump;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.fabricmc.api.ClientModInitializer;
import net.kingrocco.toggleautojump.mixin.client.GameOptionsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ToggleAutoJumpClient implements ClientModInitializer {
	private boolean wasToggled = false;
	private boolean wasUntoggled = false;
	private SimpleOption<Boolean> autoJump;
	@Override
	public void onInitializeClient() {
		StickyKeyBinding jumpToggle = new StickyKeyBinding(
				"key.toggleautojump.toggle",
				GLFW.GLFW_KEY_LEFT_ALT,
				"key.categories.movement",
				() -> true
		);

		KeyBindingHelper.registerKeyBinding(jumpToggle);

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			autoJump = ((GameOptionsAccessor) client.options).getAutoJump();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (jumpToggle.isPressed()) {
				wasUntoggled = false;

				if (!wasToggled) {
					autoJump.setValue(true);
					client.player.sendMessage(Text.literal("Auto Jump toggled."), true);
					wasToggled = true;
				}
			} else {
				wasToggled = false;

				if (client.player != null) {
					if (!wasUntoggled) {
						autoJump.setValue(false);
						client.player.sendMessage(Text.literal("Auto Jump untoggled."), true);
						wasUntoggled = true;
					}
				}
			}
		});
	}
}