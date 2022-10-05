package com.johannsn.cyclemapbackend

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class CyclemapBackendApplication

fun main(args: Array<String>) {
	runApplication<CyclemapBackendApplication>(*args)
}

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer
 {
	 @Value("\${CycleMap.allowedEndpoint}")
	 private lateinit var allowedEndpoint: String
	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedMethods("GET")
			.allowedOrigins(allowedEndpoint)
	}
}