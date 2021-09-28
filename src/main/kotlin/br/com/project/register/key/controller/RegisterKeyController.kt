package br.com.project.register.key.controller

import br.com.project.PixKeyManagerGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller
class RegisterKeyController(
    private val pixKeyManagerGrpc: PixKeyManagerGrpc.PixKeyManagerBlockingStub
) {

    @Post("register-key/{clientId}")
    fun registerKey( @PathVariable clientId : String, @Valid @Body registerKeyRequest : RegisterKeyRequest ) : HttpResponse<RegisterKeyResponse>{
        val response = registerKeyRequest.register(clientId, pixKeyManagerGrpc)
        println("eee")
        return HttpResponse
            .created( response )
            .header("Location", HttpResponse.uri("client/$clientId/pix/${response.pixId}").toString() )
    }

}