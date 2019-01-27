package ru.scratty

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val blockchain = Blockchain()

            blockchain.add(Block("first block", "0"))
            blockchain.get(0).mineBlock(5)

            blockchain.add(Block("second block", blockchain.get(0).hash))
            blockchain.get(1).mineBlock(5)

            blockchain.add(Block("third block", blockchain.get(1).hash))
            blockchain.get(2).mineBlock(5)

            println(blockchain.isChainValid())
        }
    }
}