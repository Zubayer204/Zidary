package org.zcorp.zidary.view.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import zidary.composeapp.generated.resources.Epilogue_Bold
import zidary.composeapp.generated.resources.Epilogue_Italic
import zidary.composeapp.generated.resources.Epilogue_Regular
import zidary.composeapp.generated.resources.Epilogue_SemiBold
import zidary.composeapp.generated.resources.Res

@Composable
fun Epilogue(): FontFamily {
    return FontFamily(
        Font(Res.font.Epilogue_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.Epilogue_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.Epilogue_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.Epilogue_Italic, FontWeight.Normal, FontStyle.Italic),
    )
}

@Composable
fun AppTypography() = Typography().run {
    val fontFamily = Epilogue()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
        )
}
