package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MessageProducer (
    @Value("\${broker.emailQueue}") private val emailQueue: String,
    connection: Connection
) {
    private val channel: Channel
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun publishEmail(message: EmailMessage) {
        val ow: ObjectWriter = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json: String = ow.writeValueAsString(message)
        channel.basicPublish("", emailQueue, null, json.toByteArray())
        logger.info("Published Email Message to ${message.receiver}")
    }

    init {
        channel = connection.createChannel()
    }
}
