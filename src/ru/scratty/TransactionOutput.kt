package ru.scratty

import ru.scratty.utils.getString
import ru.scratty.utils.sha256
import java.security.PublicKey

class TransactionOutput(val recipient: PublicKey,
                        val value: Float,
                        val parentTransactionId: String) {

    val id = (recipient.getString() + value + parentTransactionId).sha256()

    fun isMine(publicKey: PublicKey) = publicKey == recipient
}