package br.com.project.grpc

import br.com.project.PixKeyLoadManagerGrpc
import br.com.project.PixKeyManagerGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class FactoryGRPC(@GrpcChannel("keyManager") val channel : ManagedChannel ) {

    @Singleton
    fun registerKey() = PixKeyManagerGrpc.newBlockingStub( channel )

    @Singleton
    fun loadKey() = PixKeyLoadManagerGrpc.newBlockingStub( channel )

}