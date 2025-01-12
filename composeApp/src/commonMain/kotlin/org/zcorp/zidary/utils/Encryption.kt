package org.zcorp.zidary.utils

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.SHA256
import kotlin.random.Random

sealed class CryptoResult<out T> {
    data class Success<T>(val data: T) : CryptoResult<T>()
    data class Error(val exception: Exception) : CryptoResult<Nothing>()
}

object Encryption {
    private const val SALT_SIZE = 16

    private val provider = CryptographyProvider.Default
    private val aes = provider.get(AES.CBC)
    private val digest = provider.get(SHA256)

    fun encrypt(data: ByteArray, password: String): CryptoResult<ByteArray> {
        return try {

            val salt = Random.nextBytes(SALT_SIZE)

            val cipher = getCipher(password, salt)
            val encryptedData = cipher.encryptBlocking(data)
            CryptoResult.Success(salt + encryptedData)
        } catch (e: Exception) {
            CryptoResult.Error(e)
        }
    }

    fun decrypt(encryptedData: ByteArray, password: String): CryptoResult<ByteArray> {
        return try {
            if (encryptedData.size < 32) {
                return CryptoResult.Error(IllegalArgumentException("Invalid encrypted data size"))
            }

            val salt = encryptedData.sliceArray(0 until SALT_SIZE)
            val content = encryptedData.sliceArray(SALT_SIZE until encryptedData.size)

            val cipher = getCipher(password, salt)
            val decryptedData = cipher.decryptBlocking(content)

            CryptoResult.Success(decryptedData)
        } catch (e: Exception) {
            CryptoResult.Error(e)
        }
    }

    private fun getCipher(password: String, salt: ByteArray): AES.IvCipher {
        val keyData = digest.hasher().hashBlocking(password.encodeToByteArray() + salt)
        val key = aes.keyDecoder()
            .decodeFromByteArrayBlocking(AES.Key.Format.RAW, keyData)
        return key.cipher()
    }

}