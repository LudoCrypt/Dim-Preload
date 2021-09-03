package net.ludocrypt.dimpreload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class DimPreload implements ModInitializer {

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(DimPreload.id("change_c2s"), (server, player, handler, buf, responseSender) -> {
			Identifier destination = buf.readIdentifier();
			Identifier from = buf.readIdentifier();

			server.execute(() -> {
				RegistryKey<World> destinationKey = RegistryKey.of(Registry.WORLD_KEY, destination);
				RegistryKey<World> fromKey = RegistryKey.of(Registry.WORLD_KEY, from);
				if (player.notInAnyWorld) {
					player.notInAnyWorld = false;
					Criteria.CHANGED_DIMENSION.trigger(player, fromKey, destinationKey);
				}
			});
		});
	}

	public static Identifier id(String id) {
		return new Identifier("dimpreload", id);
	}

}
