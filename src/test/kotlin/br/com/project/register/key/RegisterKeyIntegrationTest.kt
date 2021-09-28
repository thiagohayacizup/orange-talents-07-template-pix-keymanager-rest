package br.com.project.register.key

import br.com.project.KeyResponse
import br.com.project.PixKeyManagerGrpc
import br.com.project.grpc.FactoryGRPC
import br.com.project.register.key.controller.RegisterKeyRequest
import br.com.project.register.key.controller.RegisterKeyResponse
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.hateoas.JsonError
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class RegisterKeyIntegrationTest{

    @field:Inject
    private lateinit var pixKeyManagerBlockingStub: PixKeyManagerGrpc.PixKeyManagerBlockingStub

    @field:Inject
    @field:Client(value = "/")
    private lateinit var client : io.micronaut.http.client.HttpClient

    @Test
    fun `register cpf key`(){}

    @Test
    fun `register cellphone key`(){}

    @Test
    fun `register email key`(){}

    @Test
    fun `register random key `(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        given( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .willReturn(
                KeyResponse.newBuilder()
                    .setClientId(clientId)
                    .setPixKey(pixId)
                    .build()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "CHAVE_ALEATORIA","", "CONTA_CORRENTE"
        ))
        val response = client.toBlocking().exchange(request, RegisterKeyResponse::class.java)
        Assertions.assertEquals( clientId, response?.body()?.clientId)
        Assertions.assertEquals( pixId, response?.body()?.pixId)
    }

    @Test
    fun `key type invalid for key value`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid value for key type.")
                    .asRuntimeException()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "CHAVE_ALEATORIA","email@email.com", "CONTA_CORRENTE"
        ))
        try { client.toBlocking().exchange(request, JsonError::class.java) }
        catch ( exception : HttpClientResponseException ){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status )
            Assertions.assertEquals("Invalid value for key type.", exception.message)
        }
    }

    @Test
    fun `key already registered`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .thenThrow(
                Status
                    .ALREADY_EXISTS
                    .withDescription("Key already exists.")
                    .asRuntimeException()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "CONTA_CORRENTE"
        ))
        try { client.toBlocking().exchange(request, JsonError::class.java) }
        catch ( exception : HttpClientResponseException ){
            Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.status )
            Assertions.assertEquals("Key already exists.", exception.message)
        }
    }

    @Test
    fun `invalid key type`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid Key Type.")
                    .asRuntimeException()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "AAAA","email@email.com", "CONTA_CORRENTE"
        ))
        try { client.toBlocking().exchange(request, JsonError::class.java) }
        catch ( exception : HttpClientResponseException ){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status )
            Assertions.assertEquals("Invalid Key Type.", exception.message)
        }
    }

    @Test
    fun `invalid account type`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid Account Type.")
                    .asRuntimeException()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "AAAA"
        ))
        try { client.toBlocking().exchange(request, JsonError::class.java) }
        catch ( exception : HttpClientResponseException ){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status )
            Assertions.assertEquals("Invalid Account Type.", exception.message)
        }
    }

    @Test
    fun `invalid client id`(){
        val clientId = UUID.randomUUID().toString()
        given( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .willThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid Client Id.")
                    .asRuntimeException()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "CONTA_CORRENTE"
        ))
        client.toBlocking().exchange(request, JsonError::class.java)
        try { client.toBlocking().exchange(request, JsonError::class.java) }
        catch ( exception : HttpClientResponseException ){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status )
            Assertions.assertEquals("Invalid Client Id.", exception.message)
        }
    }

    @Factory
    @Replaces( factory = FactoryGRPC::class )
    internal class factory{
        @Singleton
        fun mock() : PixKeyManagerGrpc.PixKeyManagerBlockingStub{
            return Mockito.mock( PixKeyManagerGrpc.PixKeyManagerBlockingStub::class.java )
        }
    }

}
