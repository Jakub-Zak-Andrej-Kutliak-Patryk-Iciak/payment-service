package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.repository

import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository {
    fun confirmPayment(clientSecret: String)
    fun savePayment(clientSecret: String)
    fun getPaymentStatus(clientSecret: String): String
}
