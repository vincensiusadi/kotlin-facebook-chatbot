package com.vincen.fbbot.Config

import com.github.messenger4j.Messenger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommonConfig {

    @Bean
    open fun messengerSendClient(@Value("\${messenger4j.pageAccessToken}") pageAccessToken: String, @Value("\${messenger4j.appSecret}") appSecret: String,
                                 @Value("\${messenger4j.verifyToken}") veriyToken: String): Messenger {
        return Messenger.create(pageAccessToken,appSecret,veriyToken)
    }

}