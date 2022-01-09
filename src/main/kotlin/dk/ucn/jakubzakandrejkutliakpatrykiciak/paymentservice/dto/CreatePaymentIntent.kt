package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.dto

data class CreatePaymentIntent(
    val currency: String,
    val amount: Long,
    val description: String,
)

data class CreatePaymentIntentResponse(
    val clientSecret: String
)
