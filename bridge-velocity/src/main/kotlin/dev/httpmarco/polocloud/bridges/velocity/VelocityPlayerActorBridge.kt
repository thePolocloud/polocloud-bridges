package dev.httpmarco.polocloud.bridges.velocity

import com.velocitypowered.api.proxy.ProxyServer
import dev.httpmarco.polocloud.bridge.api.actor.BridgeActor
import dev.httpmarco.polocloud.v1.player.PlayerActorResponse
import net.kyori.adventure.text.Component
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

class VelocityPlayerActorBridge(val server: ProxyServer) : BridgeActor {

    override fun message(
        uuid: UUID,
        message: String
    ): PlayerActorResponse {
        val player = server.getPlayer(uuid).getOrNull() ?: return PlayerActorResponse.newBuilder().setSuccess(false)
            .setErrorMessage("Player is not online.").build()

        if (!player.isActive) {
            return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not connected to a server.").build()
        }

        player.sendMessage(Component.text(message))
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }

    override fun kick(
        uuid: UUID,
        reason: String
    ): PlayerActorResponse {
        val player = server.getPlayer(uuid).getOrNull() ?: return PlayerActorResponse.newBuilder().setSuccess(false)
            .setErrorMessage("Player is not online.").build()

        if (!player.isActive) {
            return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not connected to a server.").build()
        }

        player.disconnect(Component.text(reason))
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }

    override fun connect(
        uuid: UUID,
        server: String
    ): CompletableFuture<PlayerActorResponse> {
        val player = this.server.getPlayer(uuid).getOrNull() ?: return CompletableFuture.completedFuture(
            PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()
        )


        if (!player.isActive) {
            return CompletableFuture.completedFuture(
                PlayerActorResponse.newBuilder().setSuccess(false)
                    .setErrorMessage("Player is not connected to a server.").build()
            )
        }

        val server = this.server.getServer(server).getOrNull() ?: return CompletableFuture.completedFuture(
            PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Server is not present.").build()
        )

        val future = CompletableFuture<PlayerActorResponse>()
        player.createConnectionRequest(server).connect().whenComplete { result, throwable ->
            future.complete(
                PlayerActorResponse.newBuilder().setSuccess(result.isSuccessful).build()
            )
        }
        return future
    }
}