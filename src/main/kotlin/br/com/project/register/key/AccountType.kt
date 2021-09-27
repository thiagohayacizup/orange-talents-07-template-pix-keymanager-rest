package br.com.project.register.key

import br.com.project.AccountType

enum class AccountType {

    CONTA_CORRENTE,
    CONTA_POUPANCA;

    fun toAccountTypeGRPC(): AccountType {
        return when( this ){
            CONTA_CORRENTE -> AccountType.CONTA_CORRENTE
            CONTA_POUPANCA -> AccountType.CONTA_POUPANCA
        }
    }

}