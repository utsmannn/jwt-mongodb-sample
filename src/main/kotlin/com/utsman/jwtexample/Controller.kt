package com.utsman.jwtexample

import com.utsman.jwtexample.data.Response
import com.utsman.jwtexample.data.UserAuth
import com.utsman.jwtexample.data.UserData
import com.utsman.jwtexample.extensions.getToken
import com.utsman.jwtexample.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api")
class Controller {
    @Autowired
    lateinit var userRepository: UserRepository

    @PostMapping("/register")
    fun register(@RequestBody userData: UserData, response: HttpServletResponse): Response {
        // find user in db
        val userDb = userRepository.findByUsername(userData.username)
        return if (userDb != null) {
            // user is exist, forbidden register
            response.status = HttpServletResponse.SC_FORBIDDEN
            Response(status = false, message = "User is exist")
        } else {
            // user not exist, continue register
            userRepository.save(userData)
            Response(status = true, message = "Ok", data = userData)
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody userData: UserData, response: HttpServletResponse): Response {
        // find user in db
        val userDb = userRepository.findByUsername(userData.username)

        return if (userDb != null && userData.password == userDb.password) {
            // get token from username
            val token = userDb.username.getToken()
            val userAuth = UserAuth(token)
            Response(true, "Ok", userAuth)
        } else {
            // user is not exist
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            Response(false, "User not found")
        }
    }

    @GetMapping("/test")
    fun test(): Response {
        return Response(status = true, message = "Ok", data = "Test oke")
    }
}