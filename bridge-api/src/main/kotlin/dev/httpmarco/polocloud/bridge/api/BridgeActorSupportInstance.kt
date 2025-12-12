package dev.httpmarco.polocloud.bridge.api

import dev.httpmarco.polocloud.bridge.api.actor.BridgeActor

abstract class BridgeActorSupportInstance<F, T>(val actor: BridgeActor) : BridgeInstance<F, T>(){

}