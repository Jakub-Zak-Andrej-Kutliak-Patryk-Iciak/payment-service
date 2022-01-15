package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.repository

import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.model.Payment
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository {
    fun confirmPayment(clientSecret: String)
    fun savePayment(payment: Payment)
    fun getPayment(clientSecret: String): Payment
}
