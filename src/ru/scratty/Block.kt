package ru.scratty

import ru.scratty.utils.getMerkleRoot
import ru.scratty.utils.sha256
import java.util.*
import kotlin.collections.ArrayList

class Block(val prevHash: String) {

    var hash: String
        private set

    val transactions = ArrayList<Transaction>()

    private var merkleRoot = ""

    private val timeStamp: Long = Date().time
    private var nonce = 0

    init {
        hash = calculateHash()
    }

    fun addTransaction(transaction: Transaction?): Boolean {
        if (transaction == null || prevHash != "0" && !transaction.processTransaction()) {
            println("Transaction failed")
            return false
        }

        transactions.add(transaction)
        println("Transaction added to block")
        return true
    }

    fun mineBlock(difficulty: Int) {
        merkleRoot = transactions.getMerkleRoot()

        val target = String(CharArray(difficulty)).replace('\u0000', '0')
        while (hash.substring(0, difficulty) != target) {
            nonce++
            hash = calculateHash()
        }
        println("Block Mined!!!: $hash")
    }

    fun calculateHash() = (prevHash + timeStamp + nonce + merkleRoot).sha256()
}