package dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.repository

import dk.ucn.jakubzakandrejkutliakpatrykiciak.paymentservice.model.Payment
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl : PaymentRepository {
    val payments: MutableList<Payment> = ArrayList()

    override fun confirmPayment(clientSecret: String) {
        val payment = payments.stream()
            .filter { x -> x.clientSecret == clientSecret }
            .findFirst().orElseThrow()
        payment.status = "confirmed"
    }

    override fun savePayment(clientSecret: String) {
        payments.add(Payment(clientSecret, "created"))
    }

    override fun getPaymentStatus(clientSecret: String): String {
        return payments.stream()
            .filter { x -> x.clientSecret == clientSecret }
            .findFirst().orElseThrow().status
    }
}
