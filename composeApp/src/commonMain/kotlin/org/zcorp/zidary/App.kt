package org.zcorp.zidary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.zcorp.zidary.db.ZidaryDatabase
import org.zcorp.zidary.model.data.JournalFactory
import org.zcorp.zidary.view.components.navigations.CalendarTab
import org.zcorp.zidary.view.components.navigations.HomeTab
import org.zcorp.zidary.view.components.navigations.SettingsTab
import org.zcorp.zidary.view.components.navigations.SyncTab
import org.zcorp.zidary.view.components.navigations.TabNavigationItem
import org.zcorp.zidary.view.theme.AppTheme
import org.zcorp.zidary.viewModel.CalendarVM
import org.zcorp.zidary.viewModel.HomeVM
import org.zcorp.zidary.viewModel.JournalComposeVM

@Composable
@Preview
fun App(db: ZidaryDatabase) {
    val journalFactory = JournalFactory(db)
    val journalComposeVM = remember { JournalComposeVM(journalFactory) }
    val homeVM = remember { HomeVM(journalFactory) }
    val calendarVM = remember { CalendarVM(journalFactory) }
    TabNavigator(HomeTab(homeVM, journalComposeVM)) {
        AppTheme (darkTheme = true) {
            Scaffold (
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    Column {
                        BottomNavigation (backgroundColor = MaterialTheme.colorScheme.surfaceContainer, ) {
                            TabNavigationItem(HomeTab(homeVM, journalComposeVM))
                            TabNavigationItem(CalendarTab(calendarVM))
                            TabNavigationItem(SyncTab)
                            TabNavigationItem(SettingsTab)
                        }
                        Spacer(modifier = Modifier.fillMaxWidth().height(20.dp).background(MaterialTheme.colorScheme.surfaceContainer))
                    }
                },
                content = {
                    Column (
                        modifier = Modifier.padding(it)
                    ) {
                        CurrentTab()
                    }
                }
            )
        }
    }
}
