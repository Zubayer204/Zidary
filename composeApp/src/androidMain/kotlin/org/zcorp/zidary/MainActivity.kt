package org.zcorp.zidary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.vinceglb.filekit.core.FileKit
import org.zcorp.zidary.model.database.DriverFactory
import org.zcorp.zidary.model.database.createDatabase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileKit.init(this)

        val db = createDatabase(DriverFactory(this))

        setContent {
            App(db)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val db = createDatabase(DriverFactory(MockContext()))
    App(db)
}