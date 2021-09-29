package br.com.project.load.all.controller

import br.com.project.ListKeysClientRequest
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
class LoadAllController(
    private val loadInfo : PixKeyLoadManagerGrpc.PixKeyLoadManagerBlockingStub
) {

    @Get("load-all/client-id/{clientId}")
    fun loadAllClientKeys(@PathVariable clientId : String) : HttpResponse<LoadAllKeyInformation>{
        val response = loadInfo.listKeysByClient(
            ListKeysClientRequest.newBuilder().setClientId(clientId).build()
        )
        return HttpResponse.ok(
            LoadAllKeyInformation(
                response.clientId,
                response.pixKeyList.map {
                    KeyInfo(
                        it.pixId,
                        it.keyType.toString(),
                        it.keyValue,
                        it.accountType.toString(),
                        Instant
                            .ofEpochSecond(
                                it.createdAt.seconds,
                                it.createdAt.nanos.toLong()
                            ).atZone( ZoneId.of("America/Sao_Paulo") )
                            .toLocalDateTime()
                    )
                }
            )
        )
    }

}