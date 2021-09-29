package br.com.project.load.all

import br.com.project.*
import br.com.project.load.all.controller.LoadAllKeyInformation
import com.google.protobuf.Timestamp
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
internal class LoadAllIntegrationTest {

    @field:Inject
    private lateinit var loadInfoApp: PixKeyLoadManagerGrpc.PixKeyLoadManagerBlockingStub

    @field:Inject
    @field:Client("/")
    private lateinit var client : HttpClient

    @Test
    fun `invalid client id`(){
        Mockito
            .`when`( loadInfoApp.listKeysByClient(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid client id.")
                    .asRuntimeException()
            ).thenReturn( ListKeysClientResponse.newBuilder().build() )
        val request = HttpRequest.GET<String>("load-all/client-id/aaaa" )
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid client id.", thrown.message)
    }

    @Test
    fun `client don't have keys`(){
        val clientId = UUID.randomUUID().toString()
        Mockito
            .`when`( loadInfoApp.listKeysByClient(Mockito.any()))
            .thenReturn( ListKeysClientResponse.newBuilder()
                .setClientId(clientId)
                .build()
            )
        val request = HttpRequest.GET<String>("load-all/client-id/$clientId" )
        val response = client.toBlocking().exchange(request, LoadAllKeyInformation::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.status )
        Assertions.assertEquals(clientId, response?.body()?.clientId)
        Assertions.assertEquals(0, response?.body()?.pixKey?.size)
    }

    @Test
    fun `client has a key`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val key = "email@email.com"
        Mockito
            .`when`( loadInfoApp.listKeysByClient(Mockito.any()))
            .thenReturn( ListKeysClientResponse.newBuilder()
                .setClientId(clientId)
                .addAllPixKey(
                    arrayListOf(
                        ListKeysClientResponse.PixKey.newBuilder()
                            .setPixId(pixId)
                            .setKeyType(KeyType.EMAIL)
                            .setKeyValue(key)
                            .setAccountType(AccountType.CONTA_CORRENTE)
                            .setCreatedAt(Timestamp.newBuilder().setNanos(23423423423423.toInt()).setSeconds(4234234234234).build())
                            .build()
                    )
                )
                .build()
            )
        val request = HttpRequest.GET<String>("load-all/client-id/$clientId" )
        val response = client.toBlocking().exchange(request, LoadAllKeyInformation::class.java)
        val body = response?.body()!!
        Assertions.assertEquals(HttpStatus.OK, response.status )
        Assertions.assertEquals(clientId, body.clientId)
        Assertions.assertEquals(1, body.pixKey.size)
        Assertions.assertEquals(pixId, body.pixKey[0].pixId)
        Assertions.assertEquals("EMAIL", body.pixKey[0].keyType)
        Assertions.assertEquals(key, body.pixKey[0].keyValue)
        Assertions.assertEquals("CONTA_CORRENTE", body.pixKey[0].accountType)
    }

}