package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.controller

import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntent
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntentResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.service.StripeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("payment")
class PaymentController(val stripeService: StripeService) {

    @GetMapping
    fun index(): String {
        return "Hello world"
    }

    @PostMapping("/create-payment-intent")
    fun createPaymentIntent(createPaymentIntent: CreatePaymentIntent): CreatePaymentIntentResponse {
        return stripeService.createPaymentIntent(createPaymentIntent)
    }

    @PostMapping("/stripe/events")
    fun handleStripeEvent(
        @RequestHeader("Stripe-Signature") sigHeader: String?,
        @RequestBody payload: String
    ) {
        stripeService.handleStripeEvent(sigHeader, payload)
    }
}
