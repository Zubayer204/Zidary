package org.zcorp.zidary.view.components.navigations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.zcorp.zidary.view.screens.Calendar
import org.zcorp.zidary.viewModel.CalendarVM
import org.zcorp.zidary.viewModel.JournalComposeVM

class CalendarTab(private val calendarVM: CalendarVM, private val journalComposeVM: JournalComposeVM): Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Calendar"
            val icon = rememberVectorPainter(Icons.Default.DateRange)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = Calendar(viewModel = calendarVM, journalComposeVM = journalComposeVM)) {navigator: Navigator ->
            SlideTransition(navigator)
        }
    }
}
