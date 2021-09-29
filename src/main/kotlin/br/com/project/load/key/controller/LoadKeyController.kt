package br.com.project.load.key.controller

import br.com.project.LoadRequest
import br.com.project.PixKeyLoadManagerGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import java.time.Instant
import java.time.ZoneId

@Validated
@Controller
class LoadKeyController(
    private val loadInfo : PixKeyLoadManagerGrpc.PixKeyLoadManagerBlockingStub
) {

    @Get("load/client-id/{clientId}/pix-id/{pixId}")
    fun loadByClientIdAndPixId(@PathVariable clientId : String, @PathVariable pixId : String ) : HttpResponse<LoadKeyInformation> {
        val response = loadInfo.loadInfo(
            LoadRequest.newBuilder()
                .setPixId(
                    LoadRequest.PixId.newBuilder()
                        .setClientId(clientId)
                        .setPixKey(pixId)
                        .build()
                )
                .build()
        )
        return HttpResponse.ok(
            LoadKeyInformation(
                response.clientId,
                response.pixKeyId,
                PixKey(
                    response.pixKey.keyType.toString(),
                    response.pixKey.keyValue,
                    Account(
                        response.pixKey.account.accountType.toString(),
                        response.pixKey.account.institution,
                        response.pixKey.account.name,
                        response.pixKey.account.cpf,
                        response.pixKey.account.agency,
                        response.pixKey.account.number
                    ),
                    Instant
                        .ofEpochSecond(
                            response.pixKey.createdAt.seconds,
                            response.pixKey.createdAt.nanos.toLong()
                        ).atZone( ZoneId.of("America/Sao_Paulo") )
                        .toLocalDateTime()
                )
            )
        )
    }

    @Get("load/pix-key/{pixKey}")
    fun loadByPixKey( @PathVariable pixKey : String ) : HttpResponse<LoadKeyInformation>{
        val response = loadInfo.loadInfo(
            LoadRequest.newBuilder()
                .setKey( pixKey )
                .build()
        )
        return HttpResponse.ok(
            LoadKeyInformation(
                response.clientId,
                response.pixKeyId,
                PixKey(
                    response.pixKey.keyType.toString(),
                    response.pixKey.keyValue,
                    Account(
                        response.pixKey.account.accountType.toString(),
                        response.pixKey.account.institution,
                        response.pixKey.account.name,
                        response.pixKey.account.cpf,
                        response.pixKey.account.agency,
                        response.pixKey.account.number
                    ),
                    Instant
                        .ofEpochSecond(
                            response.pixKey.createdAt.seconds,
                            response.pixKey.createdAt.nanos.toLong()
                        ).atZone( ZoneId.of("America/Sao_Paulo") )
                        .toLocalDateTime()
                )
            )
        )
    }
}