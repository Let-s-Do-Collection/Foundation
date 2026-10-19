package net.satisfy.foundation.overlay;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.satisfy.foundation.Foundation;

import java.util.ArrayList;
import java.util.List;

public final class EntityInfoSync {
    private static final double MAX_DISTANCE_SQR = 64.0;
    private static final int REQUEST_INTERVAL = 10;
    private static final int NO_ENTITY = -1;
    private static final CompoundTag EMPTY = new CompoundTag();
    private static final List<Handler> HANDLERS = new ArrayList<>();

    private static CompoundTag clientData = EMPTY;
    private static int clientId = NO_ENTITY;
    private static int lastRequest = NO_ENTITY;

    private EntityInfoSync() {
    }

    @FunctionalInterface
    public interface Handler {
        void write(ServerLevel level, Entity entity, ServerPlayer player, CompoundTag tag);
    }

    public record Request(int entityId) implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(Foundation.identifier("entity_info_request"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Request::entityId, Request::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Response(int entityId, CompoundTag data) implements CustomPacketPayload {
        public static final Type<Response> TYPE = new Type<>(Foundation.identifier("entity_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Response> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Response::entityId, ByteBufCodecs.COMPOUND_TAG, Response::data, Response::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void registerHandler(Handler handler) {
        HANDLERS.add(handler);
    }

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), Request.TYPE, Request.CODEC, (payload, context) -> context.queue(() -> {
            if (!(context.getPlayer() instanceof ServerPlayer player)) {
                return;
            }
            Entity entity = player.serverLevel().getEntity(payload.entityId());
            if (entity == null || player.distanceToSqr(entity) > MAX_DISTANCE_SQR) {
                return;
            }
            CompoundTag tag = new CompoundTag();
            for (Handler handler : HANDLERS) {
                handler.write(player.serverLevel(), entity, player, tag);
            }
            NetworkManager.sendToPlayer(player, new Response(payload.entityId(), tag));
        }));
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), Response.TYPE, Response.CODEC, (payload, context) -> context.queue(() -> {
                clientId = payload.entityId();
                clientData = payload.data();
            }));
        } else {
            NetworkManager.registerS2CPayloadType(Response.TYPE, Response.CODEC);
        }
    }

    public static CompoundTag poll(Entity entity) {
        if (entity.level().getGameTime() % REQUEST_INTERVAL == 0 || entity.getId() != lastRequest) {
            lastRequest = entity.getId();
            NetworkManager.sendToServer(new Request(entity.getId()));
        }
        return entity.getId() == clientId ? clientData : EMPTY;
    }
}
