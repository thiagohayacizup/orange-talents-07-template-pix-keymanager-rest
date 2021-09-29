package br.com.project.load.all.controller

data class LoadAllKeyInformation(
    val clientId : String,
    val pixKey: List<KeyInfo> = ArrayList()
)
