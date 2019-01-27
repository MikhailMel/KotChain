package ru.scratty

class Blockchain {

    private val blocks = ArrayList<Block>()

    fun get(index: Int) = blocks[index]

    fun add(block: Block) {
        blocks.add(block)
    }

    fun isChainValid(): Boolean {
        for (i in 1 until blocks.size) {
            val curBlock = blocks[i]
            val prevBlock = blocks[i - 1]

            if (curBlock.hash != curBlock.calculateHash()) {
                println("Current hashes not equal")
                return false
            }
            if (prevBlock.hash != curBlock.prevHash) {
                println("Previous hashes not equal")
                return false
            }
        }
        return true
    }
}