package org.zcorp.zidary.view.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import org.zcorp.zidary.model.data.AvailableFontFamily
import zidary.composeapp.generated.resources.Epilogue_Bold
import zidary.composeapp.generated.resources.Epilogue_Italic
import zidary.composeapp.generated.resources.Epilogue_Regular
import zidary.composeapp.generated.resources.Epilogue_SemiBold
import zidary.composeapp.generated.resources.FrederickatheGreat_Regular
import zidary.composeapp.generated.resources.FunnelDisplay_VariableFont_wght
import zidary.composeapp.generated.resources.GemunuLibre_VariableFont_wght
import zidary.composeapp.generated.resources.GreatVibes_Regular
import zidary.composeapp.generated.resources.Res
import zidary.composeapp.generated.resources.SpaceMono_Bold
import zidary.composeapp.generated.resources.SpaceMono_BoldItalic
import zidary.composeapp.generated.resources.SpaceMono_Italic
import zidary.composeapp.generated.resources.SpaceMono_Regular

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
fun FredrickaTheGreat(): FontFamily {
    return FontFamily(
        Font(Res.font.FrederickatheGreat_Regular, FontWeight.Normal, FontStyle.Normal),
    )
}

@Composable
fun FunnelDisplay(): FontFamily {
    return FontFamily(
        Font(Res.font.FunnelDisplay_VariableFont_wght, FontWeight.Normal, FontStyle.Normal),
    )
}

@Composable
fun GemunuLibre(): FontFamily {
    return FontFamily(
        Font(Res.font.GemunuLibre_VariableFont_wght, FontWeight.Normal, FontStyle.Normal),
    )
}

@Composable
fun SpaceMono(): FontFamily {
    return FontFamily(
        Font(Res.font.SpaceMono_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.SpaceMono_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.SpaceMono_Italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.SpaceMono_BoldItalic, FontWeight.Bold, FontStyle.Italic),
    )
}

@Composable
fun GreatVibes(): FontFamily {
    return FontFamily(
        Font(Res.font.GreatVibes_Regular, FontWeight.Normal, FontStyle.Normal),
    )
}

@Composable
fun AppTypography(fontFamilyName: AvailableFontFamily = AvailableFontFamily.EPILOGUE) = Typography().run {
    val fontFamily = when (fontFamilyName) {
        AvailableFontFamily.EPILOGUE -> Epilogue()
        AvailableFontFamily.FREDRICKA_THE_GREAT -> FredrickaTheGreat()
        AvailableFontFamily.FUNNEL_DISPLAY -> FunnelDisplay()
        AvailableFontFamily.GEMUNU_LIBRE -> GemunuLibre()
        AvailableFontFamily.SPACE_MONO -> SpaceMono()
    }
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
