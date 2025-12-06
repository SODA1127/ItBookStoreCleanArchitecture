package com.soda1127.itbookstorecleanarchitecture.screen.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackBarView(
    darkTheme: Boolean = isSystemInDarkTheme(),
    message: String
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors =
            CardDefaults.cardColors(containerColor = (if (darkTheme) Color.White else Color.Black).copy(alpha = 0.8f)),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(10.dp, 10.dp),
            textAlign = TextAlign.Center,
            color = if (darkTheme) Color.Black else Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSnackBarViewPreview() {
    CustomSnackBarView(
        true,
        message = "Custom Snack Bar Message"
    )
}
