package kavi.tech.service.common.strategry

import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.ext.auth.PRNG

abstract class AbstractHashingStrategy constructor(vertx: Vertx) :
    HashStrategy {

    private val random: PRNG = PRNG(vertx)
    override var nonces: JsonArray? = null

    override fun generateSalt(): String {
        val salt = ByteArray(32)
        random.nextBytes(salt)

        return bytesToHex(salt)
    }

    override fun version(password: String, version: Int): Int {
        val sep = password.lastIndexOf('$')
        if (sep != -1) {
            try {
                return Integer.parseInt(password.substring(sep + 1))
            } catch (e: NumberFormatException) {
                throw NumberFormatException("Invalid nonce version: $version")
            }
        }
        return version
    }

    override fun salt(row: JsonArray): String {
        return row.getString(1)
    }

    companion object {
        private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

        fun bytesToHex(bytes: ByteArray): String {
            val chars = CharArray(bytes.size * 2)
            for (i in bytes.indices) {
                val x = 0xFF and bytes[i].toInt()
                chars[i * 2] = HEX_CHARS[x.ushr(4)]
                chars[1 + i * 2] = HEX_CHARS[0x0F and x]
            }
            return String(chars)
        }
    }
}
