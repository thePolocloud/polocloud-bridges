package dev.httpmarco.polocloud.bridge.api.actor

import dev.httpmarco.polocloud.v1.player.PlayerActorResponse
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface BridgeActor {

    fun message(uuid : UUID, message : String) : PlayerActorResponse

    fun kick(uuid : UUID, reason : String) : PlayerActorResponse

    fun connect(uuid : UUID, server : String) : CompletableFuture<PlayerActorResponse>

}