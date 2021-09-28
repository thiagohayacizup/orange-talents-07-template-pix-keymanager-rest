package br.com.project.load.key.controller

import java.time.LocalDateTime

data class PixKey(
    val keyType: String,
    val keyValue : String,
    val account : Account,
    val createdAt : LocalDateTime
)