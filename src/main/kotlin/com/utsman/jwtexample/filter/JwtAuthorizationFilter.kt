package com.utsman.jwtexample.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.utsman.jwtexample.Constant
import com.utsman.jwtexample.data.Response
import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter : OncePerRequestFilter() {

    // filter every request
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            if (checkToken(request)) {
                // get payload token
                val claims = validate(request)

                // get 'auth' in claims
                if (claims["auth"] != null) {
                    setupAuthentication(claims)
                } else {
                    // clear context
                    SecurityContextHolder.clearContext()
                }
            }

            // try chain filter
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            // exception to handle cases

            // setup status as 401
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            val responsesError = Response(false)

            // setup message and print to response
            when (e) {
                is ExpiredJwtException -> {
                    responsesError.message = "Token expired"
                    response.writer.println(responsesError.toStringJson())
                    return
                }
                is UnsupportedJwtException -> {
                    responsesError.message = "Token not supported"
                    response.writer.println(responsesError.toStringJson())
                    return
                }
                is MalformedJwtException, is SignatureException -> {
                    responsesError.message = "Token invalid"
                    response.writer.println(responsesError.toStringJson())
                    return
                }
                else -> {
                    responsesError.message = "Token invalid"
                    response.writer.println(responsesError.toStringJson())
                    return
                }
            }
        }
    }

    private fun Response.toStringJson(): String {
        return ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this)
    }

    // validate token from header and get payload
    private fun validate(request: HttpServletRequest): Claims {
        val jwtToken = request.getHeader(Constant.HEADER)
        return Jwts.parserBuilder()
                .setSigningKey(Constant.SECRET.toByteArray())
                .build()
                .parseClaimsJws(jwtToken)
                .body
    }

    // setup jwt authentication
    private fun setupAuthentication(claims: Claims) {
        @Suppress("UNCHECKED_CAST")
        // add 'auth' in payload token
        val authorities = claims["auth"] as List<String>
        // setup role of 'auth'
        val authStream = authorities.stream().map { role: String? -> SimpleGrantedAuthority(role) }
                .collect(Collectors.toList())

        val auth = UsernamePasswordAuthenticationToken(claims.subject, null, authStream)
        SecurityContextHolder.getContext().authentication = auth
    }

    // check token in header
    private fun checkToken(request: HttpServletRequest): Boolean {
        val authHeader = request.getHeader(Constant.HEADER)
        return authHeader != null
    }

}