package com.utsman.jwtexample.data


import com.fasterxml.jackson.annotation.JsonProperty

data class UserData(
    var username: String,
    var password: String
)

data class UserAuth(
        @JsonProperty("token")
        var token: String?
)