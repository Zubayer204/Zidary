package org.zcorp.zidary.model.auth

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.LocalAuthentication.LAContext
import platform.Foundation.NSError
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LABiometryTypeOpticID
import platform.LocalAuthentication.LABiometryTypeTouchID
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.LocalAuthentication.kLAErrorAuthenticationFailed
import platform.LocalAuthentication.kLAErrorUserCancel
import kotlin.coroutines.resume

class IOSBiometricAuthenticator: BiometricAuthenticator {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun authenticate(): Boolean = suspendCancellableCoroutine { continuation ->
        val context = LAContext()
        if (context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)) {
            context.evaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = "Authenticate using biometrics"
            ) { success, error ->
                when {
                    success -> continuation.resume(true)
                    error?.code?.toInt() == kLAErrorUserCancel -> continuation.resume(false)
                    error?.code?.toInt() == kLAErrorAuthenticationFailed -> continuation.resume(false)
                    else -> continuation.resume(false)
                }
            }
        } else {
            continuation.resume(false)
        }
    }
}

object IOSPlatformContext : PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = IOSPlatformContext

actual fun getBiometricAuthenticator(context: PlatformContext): BiometricAuthenticator = IOSBiometricAuthenticator()
