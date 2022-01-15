package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.broker

data class EmailMessage(
    val receiver: String,
    val subject: String,
    val message: String
)
