package org.zcorp.zidary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.zcorp.zidary.view.components.navigations.CalendarTab
import org.zcorp.zidary.view.components.navigations.HomeTab
import org.zcorp.zidary.view.components.navigations.SettingsTab
import org.zcorp.zidary.view.components.navigations.SyncTab
import org.zcorp.zidary.view.components.navigations.TabNavigationItem
import org.zcorp.zidary.view.screens.AuthenticationWrapper
import org.zcorp.zidary.view.theme.AppTheme
import org.zcorp.zidary.viewModel.SettingsManager

@Composable
@Preview
fun App() {
    AuthenticationWrapper {
        TabNavigator(HomeTab) {
            val settingsManager = koinInject<SettingsManager>()
            val appearanceSettings by settingsManager.appearanceSettings.collectAsState()

            key(appearanceSettings) {
                AppTheme {
                    Scaffold (
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Column {
                                NavigationBar (contentColor = MaterialTheme.colorScheme.surfaceContainer, ) {
                                    TabNavigationItem(HomeTab)
                                    TabNavigationItem(CalendarTab)
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
    }
}
