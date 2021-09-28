package br.com.project.remove.key.controller

import br.com.project.KeyDeleteRequest
import br.com.project.PixKeyManagerGrpc
import br.com.project.register.key.controller.KeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated

@Validated
@Controller
class RemoveKeyController(
    private val pixKeyManagerGrpc: PixKeyManagerGrpc.PixKeyManagerBlockingStub
) {

    @Delete("delete-key/client-id/{clientId}/pix-id/{pixId}")
    fun deleteKey(@PathVariable clientId : String, @PathVariable pixId : String ) : HttpResponse<KeyResponse> {
        val response = pixKeyManagerGrpc.deleteKey( KeyDeleteRequest.newBuilder()
            .setClientId( clientId )
            .setPixKey( pixId )
            .build()
        )
        return HttpResponse.ok( KeyResponse(response.clientId, response.pixKey) )
    }

}