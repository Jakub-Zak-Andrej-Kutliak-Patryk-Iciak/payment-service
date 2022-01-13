package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.service

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import com.stripe.param.PaymentIntentCreateParams
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntent
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntentResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.GetConfigResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.math.log


@Service
class StripeService(
    private val paymentRepository: PaymentRepository,
    @Value("\${stripe.secret-key}") private val secretKey: String,
    @Value("\${stripe.public-key}") private val publicKey: String,
    @Value("\${stripe.webhook.secret}") private val webhookSecret: String,
) {

    @PostConstruct
    private fun init() {
        Stripe.apiKey = secretKey;
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
        val paymentIntent = PaymentIntent.create(intentParameters);
        paymentRepository.savePayment(paymentIntent.clientSecret)
        return CreatePaymentIntentResponse(paymentIntent.clientSecret)
    }

    fun handleStripeEvent(sigHeader: String?, payload: String) {
        val event = Webhook.constructEvent(payload, sigHeader, webhookSecret)
        val stripeObject = event.dataObjectDeserializer.`object`.orElseThrow()
    }

    fun getConfig(): GetConfigResponse {
        return GetConfigResponse(publicKey);
    }

    fun confirmPayment(paymentIntentId: String) {
        paymentRepository.confirmPayment(paymentIntentId)
    }

    fun getPaymentStatus(clientSecret: String): String {
        return paymentRepository.getPaymentStatus(clientSecret)
    }
}
