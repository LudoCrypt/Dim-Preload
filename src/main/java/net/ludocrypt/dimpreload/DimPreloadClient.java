package net.ludocrypt.dimpreload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class DimPreloadClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(DimPreload.id("change_s2c"), (client, handler, buf, responseSender) -> {
			Identifier destination = buf.readIdentifier();
			Identifier from = buf.readIdentifier();

			client.execute(() -> {
				PacketByteBuf bufc2s = PacketByteBufs.create();
				bufc2s.writeIdentifier(destination);
				bufc2s.writeIdentifier(from);
				ClientPlayNetworking.send(DimPreload.id("change_c2s"), bufc2s);
				client.setScreen(new DownloadingTerrainScreen());
			});
		});
	}

}
