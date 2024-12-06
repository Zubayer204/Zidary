package org.zcorp.zidary.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.arjunjadeja.texty.DisplayStyle
import com.arjunjadeja.texty.ListDisplayStyle
import com.arjunjadeja.texty.Repeat
import com.arjunjadeja.texty.Texty
import com.arjunjadeja.texty.TransitionStyle

@Composable
fun TextEntryAnimation(
    headlineText: String,
    subheadlineTextList: List<String>,
    headlineTextStyle: TextStyle,
    subheadlineTextStyle: TextStyle,
    modifier: Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Texty(
            text = headlineText,
            displayStyle = DisplayStyle.Typing(80L),
            textStyle = headlineTextStyle,
        )
        Texty(
            textList = subheadlineTextList,
            displayStyle = ListDisplayStyle.OneByOne(
                TransitionStyle.TYPING,
                displayDuration = 1500L,
                transitionInDuration = 500L,
                transitionOutDuration = 500L,
                repeat = Repeat.Continuous
            ),
            textStyle = subheadlineTextStyle
        )
    }
}