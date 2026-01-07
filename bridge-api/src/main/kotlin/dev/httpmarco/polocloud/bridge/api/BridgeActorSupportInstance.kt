package dev.httpmarco.polocloud.bridge.api

import com.google.protobuf.Any
import com.google.protobuf.Message
import dev.httpmarco.polocloud.bridge.api.actor.BridgeActor
import dev.httpmarco.polocloud.sdk.java.Polocloud
import dev.httpmarco.polocloud.sdk.java.player.PlayerProvider
import dev.httpmarco.polocloud.v1.player.PlayerConnectActorRequest
import dev.httpmarco.polocloud.v1.player.PlayerKickActorRequest
import dev.httpmarco.polocloud.v1.player.PlayerMessageActorRequest
import java.util.UUID

abstract class BridgeActorSupportInstance<F, T>(val actor: BridgeActor) : BridgeInstance<F, T>() {

    init {
        (Polocloud.instance().playerProvider() as PlayerProvider).verifyActorStreaming({
            val clazz = Class.forName(it.className)
            val actor = it.actor.unpack(
                Class.forName(it.className) as Class<out Message>
            )

            when (actor) {
                is PlayerKickActorRequest -> {
                    this.actor.kick(UUID.fromString(actor.uniqueId), actor.reason)
                }

                is PlayerConnectActorRequest -> {
                    this.actor.connect(UUID.fromString(actor.uniqueId), actor.targetServiceName)
                }

                is PlayerMessageActorRequest -> {
                    this.actor.message(UUID.fromString(actor.uniqueId), actor.message)
                }

                else -> {
                    throw IllegalArgumentException("Unknown actor request type: ${clazz.name}")
                }
            }
        })
    }

}