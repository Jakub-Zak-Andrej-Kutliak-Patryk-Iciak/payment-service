package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.controller

import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntent
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.CreatePaymentIntentResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto.GetConfigResponse
import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.service.StripeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("payment")
@CrossOrigin(origins = ["*"])
class PaymentController(val stripeService: StripeService) {

    @GetMapping("config")
    fun config(): GetConfigResponse {
        return stripeService.getConfig()
    }

    @PostMapping("/payment")
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
        return stripeService.confirmPayment(paymentIntentId)
    }

    @GetMapping("/{clientSecret}")
    fun getPaymentStatus(@PathVariable clientSecret: String): String {
        return stripeService.getPaymentStatus(clientSecret)
    }
}
