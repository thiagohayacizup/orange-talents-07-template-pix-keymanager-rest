package br.com.project.register.key.controller

import br.com.project.AccountType
import br.com.project.KeyType

fun String.toKeyType() : KeyType {
    return when( this ) {
        "CPF" -> KeyType.CPF
        "NUMERO_CELULAR" -> KeyType.NUMERO_CELULAR
        "EMAIL" -> KeyType.EMAIL
        "CHAVE_ALEATORIA" -> KeyType.CHAVE_ALEATORIA
        else -> KeyType.UNKNOWN_TYPE
    }
}

fun String.toAccountType() : AccountType {
    return when( this ){
        "CONTA_CORRENTE" -> AccountType.CONTA_CORRENTE
        "CONTA_POUPANCA" -> AccountType.CONTA_POUPANCA
        else -> AccountType.UNKNOWN_ACCOUNT
    }
}