package ru.scratty.utils

import java.security.MessageDigest

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    val buffer = md.digest(toByteArray())

    return buffer.joinToString("") {
        String.format("%02x", it)
    }
}