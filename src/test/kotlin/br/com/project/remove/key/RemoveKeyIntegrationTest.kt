package br.com.project.remove.key

import br.com.project.PixKeyManagerGrpc
import br.com.project.register.key.controller.KeyResponse
import br.com.project.register.key.controller.RegisterKeyRequest
import io.grpc.Status
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.hateoas.JsonError
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest
class RemoveKeyIntegrationTest {

    @field:Inject
    private lateinit var pixKeyManagerBlockingStub: PixKeyManagerGrpc.PixKeyManagerBlockingStub

    @field:Inject
    @field:Client("/")
    private lateinit var client : HttpClient

    @Test
    fun `key deleted`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.deleteKey(Mockito.any()))
            .thenReturn( br.com.project.KeyResponse.newBuilder()
                .setClientId(clientId)
                .setPixKey(pixId)
                .build()
            )
        val request = HttpRequest.DELETE<String>("delete-key/client-id/$clientId/pix-id/$pixId")
        val response = client.toBlocking().exchange(request, KeyResponse::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.status )
        Assertions.assertEquals(clientId, response?.body()?.clientId)
        Assertions.assertEquals(pixId, response?.body()?.pixId)
    }

    @Test
    fun `key not found`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.deleteKey(Mockito.any()))
            .thenThrow(
                Status
                    .NOT_FOUND
                    .withDescription("Key not found.")
                    .asRuntimeException()
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = HttpRequest.DELETE<String>("delete-key/client-id/$clientId/pix-id/$pixId")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.status )
        Assertions.assertEquals("Key not found.", thrown.message)
    }

    @Test
    fun `invalid client id`(){
        val pixId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.deleteKey(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid client id.")
                    .asRuntimeException()
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = HttpRequest.DELETE<String>("delete-key/client-id/aaaaa/pix-id/$pixId")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid client id.", thrown.message)
    }

    @Test
    fun `invalid pix id`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.deleteKey(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid pix key.")
                    .asRuntimeException()
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = HttpRequest.DELETE<String>("delete-key/client-id/$clientId/pix-id/aaaaa")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid pix key.", thrown.message)
    }

    @Test
    fun `internal error`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.deleteKey(Mockito.any()))
            .thenThrow(
                Status
                    .INTERNAL
                    .withDescription("Internal Error.")
                    .asRuntimeException()
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = HttpRequest.DELETE<String>("delete-key/client-id/$clientId/pix-id/$pixId")
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.status )
        Assertions.assertEquals("Request cannot be completed : Internal Error.", thrown.message)
    }

}