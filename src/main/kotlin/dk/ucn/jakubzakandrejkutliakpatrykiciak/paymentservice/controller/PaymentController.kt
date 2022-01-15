package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.controller

import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.broker.EmailMessage
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.broker.MessageProducer
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntent
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntentResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.GetConfigResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.service.StripeService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("payment")
@CrossOrigin(origins = ["*"])
class PaymentController(
    val stripeService: StripeService,
    val messageProducer: MessageProducer
    ) {
    var logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("config")
    fun config(): GetConfigResponse {
        return stripeService.getConfig()
    }

    @PostMapping("/create-payment-intent")
    fun createPaymentIntent(@RequestBody createPaymentIntent: CreatePaymentIntent): CreatePaymentIntentResponse {
        return stripeService.createPaymentIntent(createPaymentIntent) // save to db
    }

    @PostMapping("/stripe/events")
    fun handleStripeEvent(
        @RequestHeader("Stripe-Signature") sigHeader: String?,
        @RequestBody payload: String
    ) {
        stripeService.handleStripeEvent(sigHeader, payload)
    }

    @PostMapping("/confirm-booking/{paymentIntentId}")
    fun confirmBooking(@PathVariable paymentIntentId: String) {
        val payment = stripeService.getPayment(paymentIntentId)
        if(payment.status == "confirmed") {
            logger.info("Received a confirm request for an already confirmed payment. PaymentIntentId: $paymentIntentId")
            return
        }
        stripeService.confirmPayment(paymentIntentId)
        messageProducer.publishEmail(EmailMessage(
            payment.emailAddress,
            "Your payment is confirmed!",
            "Hi ${payment.clientName}!\n\nPayment $paymentIntentId was successfully confirmed!\n\nHave a great day!"))
    }

    @GetMapping("/status/{clientSecret}")
    fun getPaymentStatus(@PathVariable clientSecret: String): String {
        return stripeService.getPayment(clientSecret).status
    }
}
