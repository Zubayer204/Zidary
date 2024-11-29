package org.zcorp.zidary.view.components.navigations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.SlideTransition
import org.zcorp.zidary.view.screens.Sync
import zidary.composeapp.generated.resources.Res
import zidary.composeapp.generated.resources.sync

object SyncTab: Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Sync"
            val icon = rememberVectorPainter(Icons.Default.Share)

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
        Navigator(screen = Sync()) {navigator: Navigator ->
            FadeTransition(navigator)
        }
    }
}
