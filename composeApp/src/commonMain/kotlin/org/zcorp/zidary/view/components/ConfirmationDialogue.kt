package org.zcorp.zidary.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DeleteConfirmationDialog(
    onDismissButtonClick: () -> Unit,
    confirmButtonOnClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissButtonClick,
        title = { Text("Delete Entry") },
        text = { Text("Are you sure you want to delete this entry? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = confirmButtonOnClick
            ) {
                Text(
                    "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text("Cancel")
            }
        }
    )
}
