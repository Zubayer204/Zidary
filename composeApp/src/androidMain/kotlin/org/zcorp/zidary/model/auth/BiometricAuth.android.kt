package org.zcorp.zidary.model.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidBiometricAuthenticator(private val context: Context) : BiometricAuthenticator {
    override suspend fun authenticate(): Boolean {
        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            throw Exception("Biometric authentication is not available")
        }

        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(context as FragmentActivity,
                executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        if (continuation.isActive) {
                            continuation.resume(true)
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception(errString.toString()))
                        }
                    }

                    override fun onAuthenticationFailed() {
                        if (continuation.isActive) {
                            continuation.resume(false)
                        }
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate using biometrics")
                .setNegativeButtonText("Cancel")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }
}

class AndroidPlatformContext(val context: Context) : PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext {
    val context = LocalContext.current
    return AndroidPlatformContext(context)
}

actual fun getBiometricAuthenticator(context: PlatformContext): BiometricAuthenticator {
    val androidContext = (context as AndroidPlatformContext).context
    return AndroidBiometricAuthenticator(androidContext)
}