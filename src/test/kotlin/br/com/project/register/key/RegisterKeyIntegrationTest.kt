package br.com.project.register.key

import br.com.project.PixKeyManagerGrpc
import br.com.project.grpc.FactoryGRPC
import br.com.project.register.key.controller.RegisterKeyRequest
import br.com.project.register.key.controller.KeyResponse
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.hateoas.JsonError
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class RegisterKeyIntegrationTest{

    @field:Inject
    private lateinit var pixKeyManagerBlockingStub: PixKeyManagerGrpc.PixKeyManagerBlockingStub

    @field:Inject
    @field:Client("/")
    private lateinit var client : HttpClient

    @Test
    fun `register cpf key`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        given( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .willReturn(
                br.com.project.KeyResponse.newBuilder()
                    .setClientId(clientId)
                    .setPixKey(pixId)
                    .build()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "CPF","98024709023", "CONTA_CORRENTE"
        ))
        val response = client.toBlocking().exchange(request, KeyResponse::class.java)
        Assertions.assertEquals( clientId, response?.body()?.clientId)
        Assertions.assertEquals( pixId, response?.body()?.pixId)
    }

    @Test
    fun `register cellphone key`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        given( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .willReturn(
                br.com.project.KeyResponse.newBuilder()
                    .setClientId(clientId)
                    .setPixKey(pixId)
                    .build()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "NUMERO_CELULAR","+5512345643", "CONTA_CORRENTE"
        ))
        val response = client.toBlocking().exchange(request, KeyResponse::class.java)
        Assertions.assertEquals( clientId, response?.body()?.clientId)
        Assertions.assertEquals( pixId, response?.body()?.pixId)
    }

    @Test
    fun `register email key`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        given( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .willReturn(
                br.com.project.KeyResponse.newBuilder()
                    .setClientId(clientId)
                    .setPixKey(pixId)
                    .build()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "CONTA_POUPANCA"
        ))
        val response = client.toBlocking().exchange(request, KeyResponse::class.java)
        Assertions.assertEquals( clientId, response?.body()?.clientId)
        Assertions.assertEquals( pixId, response?.body()?.pixId)
    }

    @Test
    fun `register random key `(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        given( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .willReturn(
                br.com.project.KeyResponse.newBuilder()
                    .setClientId(clientId)
                    .setPixKey(pixId)
                    .build()
            )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "CHAVE_ALEATORIA",null, "CONTA_CORRENTE"
        ))
        val response = client.toBlocking().exchange(request, KeyResponse::class.java)
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
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "CHAVE_ALEATORIA","email@email.com", "CONTA_CORRENTE"
        ))
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid value for key type.", thrown.message)
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
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "CONTA_CORRENTE"
        ))
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.status )
        Assertions.assertEquals("Key already exists.", thrown.message)
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
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "AAAA","email@email.com", "CONTA_CORRENTE"
        ))
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid Key Type.", thrown.message)
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
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "AAAA"
        ))
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid Account Type.", thrown.message)
    }

    @Test
    fun `invalid client id`(){
        Mockito
            .`when`( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid Client Id.")
                    .asRuntimeException()
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = POST("register-key/aaaaaa", RegisterKeyRequest(
            "EMAIL","email@email.com", "CONTA_CORRENTE"
        ))
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid Client Id.", thrown.message)
    }

    @Test
    fun `internal error`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( pixKeyManagerBlockingStub.registerKey(Mockito.any()))
            .thenThrow(
                Status
                    .INTERNAL
                    .withDescription("Internal error.")
                    .asRuntimeException()
            ).thenReturn( br.com.project.KeyResponse.newBuilder().build() )
        val request = POST("register-key/$clientId", RegisterKeyRequest(
            "EMAIL","email@email.com", "CONTA_CORRENTE"
        ))
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.status )
        Assertions.assertEquals("Request cannot be completed : Internal error.", thrown.message)
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
