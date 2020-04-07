package kavi.tech.service.common.strategry

import io.vertx.core.Vertx
import io.vertx.core.VertxException
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class PBKDF2Strategy(vertx: Vertx) : AbstractHashingStrategy(vertx),
    HashStrategy {
    private val skf: SecretKeyFactory

    init {
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        } catch (ex: NoSuchAlgorithmException) {
            throw RuntimeException("PBKDF2 is not available", ex)
        }

    }


    override fun computeHash(password: String, salt: String, version: Int): String {

        var iterations = DEFAULT_ITERATIONS

        if (version >= 0) {
            this.nonces?.also {
                if (version < it.size()) {
                    iterations = it.getInteger(version)
                }
            } ?: throw VertxException("nonces are not available")
        }

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt.toByteArray(StandardCharsets.UTF_8),
            iterations,
            64 * 8
        )

        try {
            val hash = skf.generateSecret(spec).encoded
            return when{
                (version >= 0) -> bytesToHex(hash) + '$' + version
                else -> bytesToHex(hash)
            }
        } catch (ex: InvalidKeySpecException) {
            throw VertxException(ex)
        }

    }

    companion object {
        private const val DEFAULT_ITERATIONS = 10000
    }
}
