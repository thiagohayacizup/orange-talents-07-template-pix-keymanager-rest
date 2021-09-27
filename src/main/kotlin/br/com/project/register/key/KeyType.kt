package br.com.project.register.key

import br.com.project.KeyType

enum class KeyType {

    CPF,
    NUMERO_CELULAR,
    EMAIL,
    CHAVE_ALEATORIA;

    fun toKeyTypeGRPC(): KeyType {
        return when( this ){
            CPF -> KeyType.CPF
            NUMERO_CELULAR -> KeyType.NUMERO_CELULAR
            EMAIL -> KeyType.EMAIL
            CHAVE_ALEATORIA -> KeyType.CHAVE_ALEATORIA
        }
    }

}