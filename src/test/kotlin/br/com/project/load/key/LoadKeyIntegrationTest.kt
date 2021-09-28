package br.com.project.load.key

import br.com.project.*
import br.com.project.grpc.FactoryGRPC
import br.com.project.load.key.controller.LoadKeyInformation
import br.com.project.register.key.controller.RegisterKeyRequest
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
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
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class LoadKeyIntegrationTest {

    @field:Inject
    private lateinit var loadInfoApp: PixKeyLoadManagerGrpc.PixKeyLoadManagerBlockingStub

    @field:Inject
    @field:Client("/")
    private lateinit var client : HttpClient

    @Test
    fun `key found clientId pixId`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val keyValue = "email2@email.com"
        Mockito
            .`when`( loadInfoApp.loadInfo(Mockito.any()))
            .thenReturn( LoadResponse.newBuilder()
                .setClientId(clientId)
                .setPixKeyId(pixId)
                .setPixKey(
                    LoadResponse.PixKey.newBuilder()
                        .setKeyType(KeyType.EMAIL)
                        .setKeyValue(keyValue)
                        .setAccount(
                            LoadResponse.PixKey.Account.newBuilder()
                                .setAccountType(AccountType.CONTA_CORRENTE)
                                .setInstitution("ITAU")
                                .setName("Joao")
                                .setCpf("12312312312")
                                .setAgency("0001")
                                .setNumber("13643334")
                                .build()
                        )
                        .setCreatedAt(Timestamp.newBuilder().setNanos(23423423423423.toInt()).setSeconds(4234234234234).build())
                )
                .build()
            )
        val request = HttpRequest.GET<String>("load/client-id/$clientId/pix-id/$pixId" )
        val response = client.toBlocking().exchange(request, LoadKeyInformation::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.status )

        val body = response.body()

        Assertions.assertEquals(clientId, body?.clientId)
        Assertions.assertEquals(pixId, body?.pixId)
        Assertions.assertEquals("EMAIL", body?.pixKey?.keyType)
        Assertions.assertEquals(keyValue, body?.pixKey?.keyValue)
    }

    @Test
    fun `key found pix key`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val keyValue = "email@email.com"
        Mockito
            .`when`( loadInfoApp.loadInfo(Mockito.any()))
            .thenReturn( LoadResponse.newBuilder()
                .setClientId(clientId)
                .setPixKeyId(pixId)
                .setPixKey(
                    LoadResponse.PixKey.newBuilder()
                        .setKeyType(KeyType.EMAIL)
                        .setKeyValue(keyValue)
                        .setAccount(
                            LoadResponse.PixKey.Account.newBuilder()
                                .setAccountType(AccountType.CONTA_CORRENTE)
                                .setInstitution("ITAU")
                                .setName("Joao")
                                .setCpf("12312312312")
                                .setAgency("0001")
                                .setNumber("13643334")
                                .build()
                        )
                        .setCreatedAt(Timestamp.newBuilder().setNanos(23423423423423.toInt()).setSeconds(4234234234234).build())
                )
                .build()
            )
        val request = HttpRequest.GET<String>("load/pix-key/$keyValue" )
        val response = client.toBlocking().exchange(request, LoadKeyInformation::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.status )

        val body = response.body()

        Assertions.assertEquals(clientId, body?.clientId)
        Assertions.assertEquals(pixId, body?.pixId)
        Assertions.assertEquals("EMAIL", body?.pixKey?.keyType)
        Assertions.assertEquals(keyValue, body?.pixKey?.keyValue)

    }

    @Test
    fun `ket not found`(){
        Mockito
            .`when`( loadInfoApp.loadInfo(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("key not found.")
                    .asRuntimeException()
            ).thenReturn( LoadResponse.newBuilder().build() )
        val request = HttpRequest.GET<String>("load/pix-key/email@email.com" )
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("key not found.", thrown.message)
    }

    @Test
    fun `invalid key`(){
        Mockito
            .`when`( loadInfoApp.loadInfo(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid key.")
                    .asRuntimeException()
            ).thenReturn( LoadResponse.newBuilder().build() )
        val request = HttpRequest.GET<String>("load/pix-key/aaaaaa" )
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid key.", thrown.message)
    }

    @Test
    fun `invalid client id`(){
        val pixId = UUID.randomUUID().toString()
        Mockito
            .`when`( loadInfoApp.loadInfo(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid client id.")
                    .asRuntimeException()
            ).thenReturn( LoadResponse.newBuilder().build() )
        val request = HttpRequest.GET<String>("load/client-id/aaaa/pix-id/$pixId" )
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
            .`when`( loadInfoApp.loadInfo(Mockito.any()))
            .thenThrow(
                Status
                    .INVALID_ARGUMENT
                    .withDescription("Invalid pix id.")
                    .asRuntimeException()
            ).thenReturn( LoadResponse.newBuilder().build() )
        val request = HttpRequest.GET<String>("load/client-id/$clientId/pix-id/aaaa" )
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, JsonError::class.java)
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.status )
        Assertions.assertEquals("Invalid pix id.", thrown.message)
    }

    @Factory
    @Replaces( factory = FactoryGRPC::class )
    internal class factory{
        @Singleton
        fun mock() : PixKeyLoadManagerGrpc.PixKeyLoadManagerBlockingStub{
            return Mockito.mock( PixKeyLoadManagerGrpc.PixKeyLoadManagerBlockingStub::class.java )
        }
    }

}