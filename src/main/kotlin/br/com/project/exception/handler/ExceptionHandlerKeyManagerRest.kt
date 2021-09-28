package br.com.project.exception.handler

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ExceptionHandlerKeyManagerRest : ExceptionHandler<StatusRuntimeException, HttpResponse<Any>>{

    override fun handle(request: HttpRequest<*>?, exception: StatusRuntimeException?): HttpResponse<Any> {
        val description = exception?.status?.description ?: ""
        val (httpStatus, message) = when( exception?.status?.code ){
            Status.NOT_FOUND.code -> Pair( HttpStatus.NOT_FOUND, description )
            Status.INVALID_ARGUMENT.code -> Pair( HttpStatus.BAD_REQUEST, description )
            Status.ALREADY_EXISTS.code -> Pair( HttpStatus.UNPROCESSABLE_ENTITY, description )
            else -> Pair(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Request cannot be completed : $description"
            )
        }
        return HttpResponse.status<JsonError>( httpStatus ).body( JsonError(message) )
    }

}