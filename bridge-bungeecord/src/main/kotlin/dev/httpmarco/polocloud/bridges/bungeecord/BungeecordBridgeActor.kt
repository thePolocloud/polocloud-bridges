package dev.httpmarco.polocloud.bridges.bungeecord

import dev.httpmarco.polocloud.bridge.api.actor.BridgeActor
import dev.httpmarco.polocloud.v1.player.PlayerActorResponse
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import java.util.UUID
import java.util.concurrent.CompletableFuture

class BungeecordBridgeActor : BridgeActor {

    override fun message(uuid: UUID, message: String): PlayerActorResponse {
        val player =
            ProxyServer.getInstance().getPlayer(uuid) ?: return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()

        player.sendMessage(TextComponent(message))
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }

    override fun kick(uuid: UUID, reason: String): PlayerActorResponse {
        val player =
            ProxyServer.getInstance().getPlayer(uuid) ?: return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()

        player.disconnect(TextComponent(reason))
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }

    override fun connect(uuid: UUID, server: String): CompletableFuture<PlayerActorResponse> {
        val player = ProxyServer.getInstance().getPlayer(uuid) ?: return CompletableFuture.completedFuture(
            PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()
        )

        if (player.server.info.name.equals(server, true)) {
            return CompletableFuture.completedFuture(
                PlayerActorResponse.newBuilder().setSuccess(false)
                    .setErrorMessage("The player is already on this server!").build()
            )
        }

        val serverInfo = ProxyServer.getInstance().getServerInfo(server) ?: return CompletableFuture.completedFuture(
            PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Server info is not present.").build()
        )

        val future = CompletableFuture<PlayerActorResponse>()

        player.connect(serverInfo) { result, error ->
            var result = PlayerActorResponse.newBuilder().setSuccess(result)

            if (error != null) {
                result = result.setErrorMessage(error.message)
            }

            future.complete(result.build())
        }
        return future
    }
}
