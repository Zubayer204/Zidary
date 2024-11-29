package org.zcorp.zidary.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class Calendar: Screen {
    @Composable
    override fun Content() {
        Box {
            Text("Welcome to calendar page!")
        }
    }
}