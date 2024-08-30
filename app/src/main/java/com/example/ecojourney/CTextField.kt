package com.example.ecojourney

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CTextField(
    onValueChange: (String) -> Unit = {},
    hint: String,
    value: String,
    isPassword: Boolean = false,
    title: String = "",
    isReadOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default // Added textStyle parameter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (!isReadOnly) {
                    onValueChange(it)
                }
            },
            placeholder = {
                Text(
                    text = hint,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFFBEC2C2),
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent,
                focusedBorderColor = Color(0xFF3F6B1B),
                unfocusedBorderColor = Color(0xFF787878),
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = textStyle, // Apply the passed textStyle
            readOnly = isReadOnly
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CTextFieldPass(
    onValueChange: (String) -> Unit = {},
    hint: String,
    value: String,
    isPassword: Boolean = false,
    title: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = hint,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFFBEC2C2),
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent,
                focusedBorderColor = Color(0xFF3F6B1B),
                unfocusedBorderColor = Color(0xFF787878),
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp), // Rounded corners
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            textStyle = TextStyle.Default.copy(fontSize = 14.sp),
            trailingIcon = {
                val imageResource = if (passwordVisible) R.drawable.eyetoggle else R.drawable.eyetoggle
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CTextFieldPreview() {
    CTextField(
        hint = "Enter text",
        value = "",
        isPassword = false,
        title = ""
    )
}

@Preview(showBackground = true)
@Composable
fun CTextFieldPassPreview() {
    CTextFieldPass(
        hint = "Enter password",
        value = "",
        title = ""
    )
}
