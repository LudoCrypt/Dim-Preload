package net.ludocrypt.dimpreload.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.ludocrypt.dimpreload.DimPreload;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	@Shadow
	public boolean notInAnyWorld;

	@Shadow
	public ServerPlayNetworkHandler networkHandler;

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
	private void dimpreload$moveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> ci) {
		this.detach();
		this.getServerWorld().removePlayer(((ServerPlayerEntity) (Object) this), Entity.RemovalReason.CHANGED_DIMENSION);
		if (!this.notInAnyWorld) {
			this.notInAnyWorld = true;
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeIdentifier(destination.getRegistryKey().getValue());
			buf.writeIdentifier(this.getServerWorld().getRegistryKey().getValue());
			ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, DimPreload.id("change_s2c"), buf);
		}
	}

	@Shadow
	public abstract ServerWorld getServerWorld();

}
