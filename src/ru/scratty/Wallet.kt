package ru.scratty

import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

class Wallet {

    lateinit var privateKey: PrivateKey
        private set

    lateinit var publicKey: PublicKey
        private set

    private val utxos = HashMap<String, TransactionOutput>()

    init {
        generateKeys()
    }

    fun getBalance(): Float {
        var sum = 0f
        Blockchain.UTXOs
            .filter { it.value.isMine(publicKey) }
            .forEach { _, utxo ->
                utxos[utxo.id] = utxo
                sum += utxo.value
            }

        return sum
    }

    fun sendFunds(recipient: PublicKey, value: Float): Transaction? {
        if (getBalance() < value) {
            println("Insufficient funds")
            return null
        }

        val inputs = ArrayList<TransactionInput>()

        var sum = 0f
        for (UTXO in utxos.values) {
            sum += UTXO.value
            inputs.add(TransactionInput(UTXO.id))

            if (sum > value) {
                break
            }
        }

        val transaction = Transaction(publicKey, recipient, value, inputs)
        transaction.generateSignature(privateKey)

        inputs.forEach {
            utxos.remove(it.transactionOutputId)
        }

        return transaction
    }

    private fun generateKeys() {
        val generator = KeyPairGenerator.getInstance("RSA")
        val rand = SecureRandom.getInstance("SHA1PRNG")

        generator.initialize(1024, rand)

        val keyPair = generator.genKeyPair()
        privateKey = keyPair.private
        publicKey = keyPair.public
    }
}