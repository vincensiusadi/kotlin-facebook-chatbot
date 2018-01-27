package com.vincen.fbbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
@EnableAutoConfiguration
open class Application

fun main(args: Array<String>){
    SpringApplication.run(Application::class.java)
}