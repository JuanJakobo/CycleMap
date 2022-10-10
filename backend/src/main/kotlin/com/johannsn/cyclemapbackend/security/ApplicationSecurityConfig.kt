package com.johannsn.cyclemapbackend.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class ApplicationSecurityConfig : WebSecurityConfigurerAdapter() {

    @Throws(java.lang.Exception::class)
    override fun configure(http: HttpSecurity) {
        http.headers().frameOptions().disable()
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/**").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
    }
}