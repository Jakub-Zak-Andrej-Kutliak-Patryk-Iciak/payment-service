package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.broker

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BrokerConfig(
    @Value("\${spring.rabbitmq.host}") private val host: String,
    @Value("\${spring.rabbitmq.port}") private val port: Int
) {

    @Bean
    fun connection(): Connection {
        val factory = ConnectionFactory()
        factory.host = host
        factory.port = port
        return factory.newConnection()
    }
}
