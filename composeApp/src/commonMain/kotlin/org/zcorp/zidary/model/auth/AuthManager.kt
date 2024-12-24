package org.zcorp.zidary.model.auth

import org.zcorp.zidary.viewModel.SettingsManager

class AuthManager(
    private val settingsManager: SettingsManager,
    context: PlatformContext,
) {
    private val biometricAuth = getBiometricAuthenticator(context)

    suspend fun authenticateIfRequired(): Boolean {
        return if (settingsManager.isBiometricLockEnabled()) {
            biometricAuth.authenticate()
        } else {
            true // If biometric lock is not enabled, authentication is always successful
        }
    }

//    suspend fun isBiometricAvailable(): Boolean {
//        return biometricAuth.isBiometricAvailable()
//    }
//
//    fun getBiometricTypeName(): String {
//        return biometricAuth.getBiometricTypeName()
//    }
}