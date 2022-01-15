package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.service

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import com.stripe.param.PaymentIntentCreateParams
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntent
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntentResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.GetConfigResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.model.Payment
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class StripeService(
    private val paymentRepository: PaymentRepository,
    @Value("\${stripe.secret-key}") private val secretKey: String,
    @Value("\${stripe.public-key}") private val publicKey: String,
    @Value("\${stripe.webhook.secret}") private val webhookSecret: String,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    private fun init() {
        Stripe.apiKey = secretKey
    }

    fun createPaymentIntent(createPaymentIntent: CreatePaymentIntent): CreatePaymentIntentResponse {
        val intentParameters = PaymentIntentCreateParams.builder()
            .setCurrency(createPaymentIntent.currency)
            .setAmount(createPaymentIntent.amount)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods
                    .builder()
                    .setEnabled(true)
                    .build()
            )
            .build()
        val paymentIntent = PaymentIntent.create(intentParameters)
        val payment = Payment(paymentIntent.clientSecret, createPaymentIntent.email, createPaymentIntent.clientName, "created")
        paymentRepository.savePayment(payment)
        logger.info("Created Payment Intent ${createPaymentIntent.amount} ${createPaymentIntent.currency}: ${createPaymentIntent.description}", createPaymentIntent.description)
        return CreatePaymentIntentResponse(paymentIntent.clientSecret)
    }

    fun handleStripeEvent(sigHeader: String?, payload: String) {
        val event = Webhook.constructEvent(payload, sigHeader, webhookSecret)
        val stripeObject = event.dataObjectDeserializer.`object`.orElseThrow()

        when(event.type) {
            "payment_intent.succeeded" -> logger.info("Succeeded " + (stripeObject as PaymentIntent).amount)
            else -> logger.error("Unhandled event type: " + event.type)
        }
    }

    fun getConfig(): GetConfigResponse {
        return GetConfigResponse(publicKey)
    }

    fun confirmPayment(paymentIntentId: String) {
        paymentRepository.confirmPayment(paymentIntentId)
        logger.info("Payment confirmed $paymentIntentId")
    }

    fun getPayment(clientSecret: String): Payment {
        return paymentRepository.getPayment(clientSecret)
    }
}
