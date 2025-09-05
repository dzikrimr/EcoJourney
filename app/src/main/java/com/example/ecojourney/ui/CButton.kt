package com.example.ecojourney.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily

@Composable
fun CButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    text: String,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3F6B1B)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PJakartaFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
fun CButton2(text: String) {
    Button(
        onClick = { },
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8F8F8F)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = PJakartaFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
fun CButtonOutlined(
    onClick: () -> Unit = {},
    text: String,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(40.dp),
        border = BorderStroke(2.dp, Color(0xFF3F6B1B)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF3F6B1B)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F6B1B)
                )
            )
        }
    }

@Composable
fun CButtonOutlinedG(
    onClick: () -> Unit = {},
    text: String,
    iconResId: Int? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(40.dp),
        border = BorderStroke(1.dp, Color(0xFF3F6B1B)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF3F6B1B)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            iconResId?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Unspecified, // Pertahankan warna asli ikon
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 20.dp) // Add left padding here
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F6B1B)
                ),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun CButtonOutlinedF(
    onClick: () -> Unit = {},
    text: String,
    iconResId: Int? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(40.dp),
        border = BorderStroke(1.dp, Color(0xFF3F6B1B)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF3F6B1B)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            iconResId?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Unspecified, // Pertahankan warna asli ikon
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 10.dp) // Add left padding here
                )
            }
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F6B1B)
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CButtonPreview() {
    CButton(
        text = "",
        onClick = { /* Handle click */ }
    )
}

@Preview(showBackground = true)
@Composable
fun CButtonPreview2() {
    CButton2(
        text = ""
    )
}

@Preview
@Composable
fun CButtonOutlined() {
    CButtonOutlined(
        text = "",
        onClick = { /* Handle click */ }
    )
}

@Preview(showBackground = true)
@Composable
fun CButtonOutlinedGPreview() {
    CButtonOutlinedG(
        text = "ada",
        onClick = { /* Handle click */ },
        iconResId = R.drawable.google_ic // Replace with your PNG icon resource
    )
}

@Preview(showBackground = true)
@Composable
fun CButtonOutlinedFPreview() {
    CButtonOutlinedF(
        text = "ada",
        onClick = { /* Handle click */ },
        iconResId = R.drawable.fcb_ic // Replace with your PNG icon resource
    )
}