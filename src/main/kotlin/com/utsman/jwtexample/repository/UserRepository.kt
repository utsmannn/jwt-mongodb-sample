package com.utsman.jwtexample.repository

import com.utsman.jwtexample.data.UserData
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserData, String> {
    fun findByUsername(username: String) : UserData?
}