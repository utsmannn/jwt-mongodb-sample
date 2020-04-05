package com.utsman.jwtexample.configuration

import com.utsman.jwtexample.filter.JwtAuthorizationFilter
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@Configuration
class JwtConfiguration : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        // security configuration
        http.csrf().disable()
                .addFilterAfter(JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java) // add jwt filter
                .authorizeRequests().antMatchers(HttpMethod.POST, "/api/login", "/api/register").permitAll() // create exception secure for login and register
                .anyRequest()
                .authenticated()
    }
}