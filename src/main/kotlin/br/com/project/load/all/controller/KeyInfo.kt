package br.com.project.load.all.controller

import java.time.LocalDateTime

data class KeyInfo(
    val pixId : String,
    val keyType : String,
    val keyValue : String,
    val accountType : String,
    val createdAt : LocalDateTime
)
