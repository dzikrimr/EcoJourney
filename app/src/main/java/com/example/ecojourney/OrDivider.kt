package com.example.ecojourney

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OrDivider(
    color: Color = Color(0xFF8F8F8F),
    textColor: Color = Color(0xFF8F8F8F)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = color,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Atau",
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.body2
        )
        Divider(
            color = color,
            modifier = Modifier.weight(1f)
        )
    }
}


@Preview
@Composable
fun OrDividerPreview() {
    OrDivider()
}

