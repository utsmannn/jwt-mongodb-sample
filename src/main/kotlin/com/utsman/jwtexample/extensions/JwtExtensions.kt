package com.utsman.jwtexample.extensions

import com.utsman.jwtexample.Constant
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import java.util.*
import java.util.stream.Collectors

// setup token from string
fun String.getToken(): String {
    val granted = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER")
    val grantedStream = granted.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
    return Jwts.builder()
            .setSubject(this)
            .claim("auth", grantedStream)
            .setExpiration(Date(System.currentTimeMillis() + 600000))
            .signWith(Keys.hmacShaKeyFor(Constant.SECRET.toByteArray()), SignatureAlgorithm.HS512)
            .compact()
}