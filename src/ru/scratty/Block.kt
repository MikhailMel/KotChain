package ru.scratty

import ru.scratty.utils.sha256
import java.util.*

class Block(private val data: String,
    val prevHash: String) {

    var hash: String

    private val timeStamp: Long = Date().time
    private var nonce = 0

    init {
        hash = calculateHash()
    }

    fun mineBlock(difficulty: Int) {
        val target = String(CharArray(difficulty)).replace('\u0000', '0')
        while (hash.substring(0, difficulty) != target) {
            nonce++
            hash = calculateHash()
        }
        println("Block Mined!!!: $hash")
    }

    fun calculateHash() = (prevHash + timeStamp + nonce + data).sha256()
}