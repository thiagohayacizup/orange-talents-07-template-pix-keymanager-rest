package br.com.project.register.key.controller

import br.com.project.KeyRequest
import br.com.project.PixKeyManagerGrpc
import br.com.project.register.key.AccountType
import br.com.project.register.key.KeyType
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class RegisterKeyRequest(
    @field:NotNull( message = "KeyType must not be null." )
    val keyType : KeyType,
    @field:Size( max = 77, message = "KeyValue size must have max 77 characters." )
    val keyValue : String,
    @field:NotNull( message = "AccountType must not be null." )
    val accountType : AccountType
) {

    fun register( clientId : String, grpcClient : PixKeyManagerGrpc.PixKeyManagerBlockingStub ): RegisterKeyResponse {
        val response = grpcClient.registerKey(
            KeyRequest.newBuilder()
                .setClientId(clientId)
                .setKeyType(keyType.toKeyTypeGRPC())
                .setKeyValue(keyValue)
                .setAccountType(accountType.toAccountTypeGRPC())
                .build()
        )
        return RegisterKeyResponse(response.clientId,response.pixKey)
    }

}
