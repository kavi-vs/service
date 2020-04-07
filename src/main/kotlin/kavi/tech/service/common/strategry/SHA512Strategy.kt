package kavi.tech.service.common.strategry

import io.vertx.core.Vertx
import io.vertx.core.VertxException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SHA512Strategy(vertx: Vertx) : AbstractHashingStrategy(vertx),
    HashStrategy {

    private val md: MessageDigest

    init {

        try {
            md = MessageDigest.getInstance("SHA-512")
        } catch (nsae: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-512 is not available", nsae)
        }

    }

    override fun computeHash(password: String, salt: String, version: Int): String {
        var concat = salt + password
        if (version >= 0) {
            this.nonces?.also{
                if (version < it.size()) {
                    concat += it.getString(version)
                }
            } ?: throw VertxException("nonces are not available")
        }

        val bHash = md.digest(concat.toByteArray(StandardCharsets.UTF_8))
        return when{
            (version >= 0) -> bytesToHex(bHash) + '$' + version
            else -> bytesToHex(bHash)
        }
    }
}
