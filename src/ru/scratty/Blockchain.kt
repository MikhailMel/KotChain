package ru.scratty

class Blockchain {

    companion object {
        val blocks = ArrayList<Block>()
        val UTXOs = HashMap<String, TransactionOutput>()

        const val minTransaction = 0.1f
    }

    private val genesisTransaction: Transaction

    private val difficulty = 5

    init {
        val coinbase = Wallet()
        val wallet1 = Wallet()
        val wallet2 = Wallet()

        genesisTransaction = Transaction(coinbase.publicKey, wallet1.publicKey, 100f, emptyList())
        genesisTransaction.generateSignature(coinbase.privateKey)
        genesisTransaction.outputs.add(
            TransactionOutput(
                genesisTransaction.recipient,
                genesisTransaction.value,
                genesisTransaction.transactionId
            )
        )
        UTXOs[genesisTransaction.outputs[0].id] = genesisTransaction.outputs[0]

        println("Create and mine genesis block")
        val genesisBlock = Block("0")
        genesisBlock.addTransaction(genesisTransaction)
        add(genesisBlock)

        println("Wallet1 balance: ${wallet1.getBalance()}")
        println("Wallet2 balance: ${wallet2.getBalance()}")

        val block1 = Block(genesisBlock.hash)
        val transaction1 = wallet1.sendFunds(wallet2.publicKey, 17f)
        block1.addTransaction(transaction1)
        add(block1)

        println("Wallet1 balance: ${wallet1.getBalance()}")
        println("Wallet2 balance: ${wallet2.getBalance()}")

        val block2 = Block(block1.hash)
        val transaction2 = wallet1.sendFunds(wallet2.publicKey, 170f)
        block2.addTransaction(transaction2)
        add(block2)

        println("Wallet1 balance: ${wallet1.getBalance()}")
        println("Wallet2 balance: ${wallet2.getBalance()}")

        val block3 = Block(block2.hash)
        val transaction3 = wallet2.sendFunds(wallet1.publicKey, 7f)
        block3.addTransaction(transaction3)
        add(block3)

        println("Wallet1 balance: ${wallet1.getBalance()}")
        println("Wallet2 balance: ${wallet2.getBalance()}")
    }


    fun get(index: Int) = blocks[index]

    fun add(block: Block) {
        blocks.add(block)
    }

    fun isChainValid(): Boolean {
        val target = String(CharArray(difficulty)).replace('\u0000', '0')

        val tempUTXOs = HashMap<String, TransactionOutput>()
        tempUTXOs[genesisTransaction.outputs[0].id] = genesisTransaction.outputs[0]

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
            if (curBlock.hash.substring(0, difficulty) != target) {
                println("Block has no be mined")
                return false
            }

            curBlock.transactions.forEach { transaction ->
                if (!transaction.verifySignature()) {
                    println("Invalid signature")
                    return false
                }
                if (transaction.getInputsValue() != transaction.getOutputsValue()) {
                    println("Inputs are not equal to outputs")
                    return false
                }

                transaction.inputs.forEach { input ->
                    val tempOutput = tempUTXOs[input.transactionOutputId]

                    if (tempOutput == null) {
                        println("Referenced input missing")
                        return false
                    }
                    if (input.UTXO!!.value != tempOutput.value) {
                        println("Values are not equals")
                        return false
                    }

                    tempUTXOs.remove(input.transactionOutputId)
                }

                transaction.outputs.forEach {
                    tempUTXOs[it.id] = it
                }

                if (transaction.outputs[0].recipient != transaction.recipient) {
                    println("Recipient are not equal")
                    return false
                }
                if (transaction.outputs[1].recipient != transaction.sender) {
                    println("Sender are not equal")
                    return false
                }
            }
        }

        println("Blockchain is valid")
        return true
    }
}