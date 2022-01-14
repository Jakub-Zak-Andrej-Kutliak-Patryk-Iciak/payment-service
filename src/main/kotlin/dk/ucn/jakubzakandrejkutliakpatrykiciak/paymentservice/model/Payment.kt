package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.model

data class Payment(
    val clientSecret: String,
    var status: String
)
