package br.com.project.register.key.controller

import br.com.project.AccountType
import br.com.project.KeyRequest
import br.com.project.KeyType
import br.com.project.PixKeyManagerGrpc
import io.micronaut.core.annotation.Introspected

@Introspected
data class RegisterKeyRequest(
    val keyType : String?,
    val keyValue : String?,
    val accountType : String?
) {

    fun register( clientId : String, grpcClient : PixKeyManagerGrpc.PixKeyManagerBlockingStub ): KeyResponse {
        val response = grpcClient.registerKey(
            KeyRequest.newBuilder()
                .setClientId(clientId)
                .setKeyType(keyType?.toKeyType() ?: KeyType.UNKNOWN_TYPE)
                .setKeyValue(keyValue ?: "")
                .setAccountType(accountType?.toAccountType() ?: AccountType.UNKNOWN_ACCOUNT)
                .build()
        )
        return KeyResponse(response.clientId,response.pixKey)
    }

}
