package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.model

data class Payment(
    val clientSecret: String,
    val emailAddress: String,
    val clientName: String,
    var status: String
)
