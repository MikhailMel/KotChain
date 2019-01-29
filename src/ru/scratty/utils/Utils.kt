package ru.scratty.utils

import ru.scratty.Transaction
import java.security.*
import java.util.*
import kotlin.collections.ArrayList

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    val buffer = md.digest(toByteArray())

    return buffer.joinToString("") {
        String.format("%02x", it)
    }
}

fun Key.getString() = Base64.getEncoder().encodeToString(encoded)!!

fun PrivateKey.applyRSASig(data: String): ByteArray {
    val sig = Signature.getInstance("SHA256withRSA")
    sig.initSign(this)
    sig.update(data.toByteArray())
    return sig.sign()
}

fun PublicKey.verifyRSASig(data: String, signature: ByteArray): Boolean {
    val sig = Signature.getInstance("SHA256withRSA")
    sig.initVerify(this)
    sig.update(data.toByteArray())
    return sig.verify(signature)
}

fun ArrayList<Transaction>.getMerkleRoot(): String {
    var prevTreeLayer = map { it.transactionId }
    var treeLayer = prevTreeLayer

    while (treeLayer.size > 1) {
        treeLayer = ArrayList()

        for (i in 0 until prevTreeLayer.size - 1 step 2) {
            treeLayer.add((prevTreeLayer[i] + prevTreeLayer[i + 1]).sha256())
        }

        prevTreeLayer = treeLayer
    }

    return if (treeLayer.size == 1) treeLayer[0] else ""
}