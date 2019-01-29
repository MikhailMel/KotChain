package ru.scratty

import ru.scratty.utils.applyRSASig
import ru.scratty.utils.getString
import ru.scratty.utils.sha256
import ru.scratty.utils.verifyRSASig
import java.security.PrivateKey
import java.security.PublicKey

class Transaction(from: PublicKey, to: PublicKey, value: Float, inputs: List<TransactionInput>) {

    companion object {
        private var sequence = 0
    }

    var transactionId: String = "0"
        private set

    val sender: PublicKey = from
    val recipient: PublicKey = to
    val inputs: List<TransactionInput> = inputs
    val outputs = ArrayList<TransactionOutput>()
    val value: Float = value

    private var signature = byteArrayOf()

    fun processTransaction(): Boolean {
        if (!verifySignature()) {
            println("Signature failed to verify")
            return false
        }

        inputs.forEach {
            it.UTXO = Blockchain.UTXOs[it.transactionOutputId]
        }

        if (getInputsValue() < Blockchain.minTransaction) {
            println("Transaction inputs to small")
            return false
        }

        val left = getInputsValue() - value
        transactionId = calculateHash()

        outputs.add(TransactionOutput(recipient, value, transactionId))
        outputs.add(TransactionOutput(sender, left, transactionId))

        outputs.forEach {
            Blockchain.UTXOs[it.id] = it
        }

        inputs.forEach {
            if (it.UTXO != null) {
                Blockchain.UTXOs.remove(it.UTXO!!.id)
            }
        }

        return true
    }

    fun getInputsValue(): Float {
        var sum = 0f
        inputs.forEach {
            if (it.UTXO != null) {
                sum += it.UTXO!!.value
            }
        }
        return sum
    }

    fun getOutputsValue(): Float {
        var sum = 0f
        outputs.forEach {
            sum += it.value
        }
        return sum
    }

    fun generateSignature(privateKey: PrivateKey) {
        val data = sender.getString() + recipient.getString() + value
        signature = privateKey.applyRSASig(data)
    }

    fun verifySignature(): Boolean {
        val data = sender.getString() + recipient.getString() + value
        return sender.verifyRSASig(data, signature)
    }

    private fun calculateHash() = (sender.getString() + recipient.getString() + value + sequence).sha256()
}