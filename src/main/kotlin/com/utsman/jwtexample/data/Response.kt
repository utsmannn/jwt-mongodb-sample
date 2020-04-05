package com.utsman.jwtexample.data

data class Response(
        val status: Boolean,
        var message: String? = null,
        var data: Any? = null
)