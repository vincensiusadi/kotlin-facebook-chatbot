package com.vincen.fbbot.Controller

import com.github.messenger4j.Messenger
import com.github.messenger4j.Messenger.*
import com.github.messenger4j.exception.MessengerApiException
import com.github.messenger4j.exception.MessengerIOException
import com.github.messenger4j.exception.MessengerVerificationException
import com.github.messenger4j.send.MessagePayload
import com.github.messenger4j.send.NotificationType
import com.github.messenger4j.send.SenderActionPayload
import com.github.messenger4j.send.message.TextMessage
import com.github.messenger4j.send.recipient.IdRecipient
import com.github.messenger4j.send.senderaction.SenderAction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Optional.of

@RestController
@RequestMapping("/callback")
class HandlerController
@Autowired constructor(
        private val messenger: Messenger
){

    private val log = LoggerFactory.getLogger(HandlerController::class.java)

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun callbackHandler(@RequestParam(MODE_REQUEST_PARAM_NAME) mode: String,
                        @RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) verivyToken: String,
                        @RequestParam(CHALLENGE_REQUEST_PARAM_NAME) chalenge: String): ResponseEntity<String>{
        messenger.verifyWebhook(mode, verivyToken)
        return ResponseEntity.ok(chalenge)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun handleCallback(@RequestBody payload: String,
                       @RequestHeader(SIGNATURE_HEADER_NAME) signature: String): ResponseEntity<Void> {

        log.info("Received Messenger Platform callback - payload: {$payload} | signature: {$signature}")
        try {
            messenger.onReceiveEvents(payload, of( signature )) { event ->
                val senderId = event.senderId()
                val timestamp = event.timestamp()

                if (event.isTextMessageEvent) {
                    val textMessageEvent = event.asTextMessageEvent()
                    val messageId = textMessageEvent.messageId()
                    val text = textMessageEvent.text()
                    sendTextMessage(senderId, text)

                    log.info("Received text message from '{$senderId}' at '{$timestamp}' with content: {$text} (mid: {$messageId})")
                }
            }
            log.info("Processed callback payload successfully")
            return ResponseEntity.status(HttpStatus.OK).build()
        } catch (e: MessengerVerificationException) {
            log.error("Processing of callback payload failed: {e.printStackTrace()}")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

    }

    fun sendTextMessage(recipientId: String, text: String){
        try {
            var reply = ""
            val notificationType = NotificationType.REGULAR
            val recipient = IdRecipient.create(recipientId)

            when (text.toLowerCase()) {

                "halo" -> {
                    sendTypingOn(recipientId)
                    reply = "halo juga :)"
                    val payload = MessagePayload.create(recipient, TextMessage.create(reply), of(notificationType))
                    messenger.send(payload)
                }

                "thanks" -> {
                    sendTypingOn(recipientId)
                    reply = "sama-sama, kembali kasih"
                    val payload = MessagePayload.create(recipient, TextMessage.create(reply), of(notificationType))
                    messenger.send(payload)
                }

                else -> {
                    sendReadReceipt(recipientId)
                }
            }
        } catch (e: MessengerApiException) {
            //handleSendException(e)
        } catch (e: MessengerIOException) {
            //handleSendException(e)
        }
    }

    private fun sendTypingOn(recipientId: String){
        val payload = SenderActionPayload.create(recipientId, SenderAction.TYPING_ON)
        messenger.send(payload)
    }

    private fun sendReadReceipt(recipientId: String){
        val payload = SenderActionPayload.create(recipientId, SenderAction.MARK_SEEN)
        messenger.send(payload)
    }


}