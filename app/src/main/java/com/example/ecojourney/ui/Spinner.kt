package com.example.ecojourney.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily

@Composable
fun CustomSpinnerDropdown(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    hint: String = "",
    width: Dp = 140.dp, // Default width value
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor = if (selectedItem.isEmpty()) Color(0xFFA5A5A5) else Color(0xFF3F6B1B)

    Box(
        modifier = modifier
            .width(width) // Use the provided width
            .height(35.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display hint if no item is selected
                Text(
                    text = if (selectedItem.isEmpty()) hint else selectedItem,
                    color = if (selectedItem.isEmpty()) Color(0xFFA5A5A5) else Color(0xFF3F6B1B),
                    fontSize = 13.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = if (selectedItem.isEmpty()) FontWeight.Medium else FontWeight.Bold,
                    fontStyle = if (selectedItem.isEmpty()) FontStyle.Italic else FontStyle.Normal,
                    modifier = Modifier
                        .weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.droparrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                )
            }

            // DropdownMenu with custom styling
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(width) // Match the width of the dropdown
                    .background(Color.White)
                    .clip(RoundedCornerShape(8.dp)) // Match the shape of the dropdown
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onItemSelected(item)
                        },
                        modifier = Modifier.padding(4.dp) // Add padding to each item
                    ) {
                        Text(
                            text = item,
                            fontSize = 14.sp,
                            fontFamily = PJakartaFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F6B1B) // Adjust the color as needed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    text: String,
    hint: String = "",
    label: String = "",
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // Add enabled parameter with default value true
) {
    val borderColor = if (text.isEmpty()) Color(0xFFA5A5A5) else Color(0xFF3F6B1B)

    Box(
        modifier = modifier
            .width(140.dp)
            .height(35.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            // Hint Text
            if (text.isEmpty() && hint.isNotEmpty()) {
                Text(
                    text = hint,
                    color = Color(0xFFA5A5A5), // Hint text color
                    fontSize = 13.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            // Actual TextField
            BasicTextField(
                value = text,
                onValueChange = { newValue ->
                    // Ensure the newValue is a number and max length is 8
                    val newText = newValue.filter { it.isDigit() }.take(8)
                    if (newText != text) {
                        onTextChanged(newText)
                    }
                },
                enabled = enabled, // Apply the enabled parameter
                textStyle = TextStyle(
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color(0xFF3F6B1B) else Color(0xFFA5A5A5), // Dim text when disabled
                    fontSize = 13.sp
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp) // Remove extra padding to overlay hint text
            )
        }

        // Label Text
        Text(
            text = label,
            color = if (enabled) Color(0xFF3F6B1B) else Color(0xFFA5A5A5), // Dim label when disabled
            fontSize = 10.sp,
            fontFamily = PJakartaFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    CustomTextField(
        text = "",
        hint = "",
        onTextChanged = { /* Handle text change */ }
    )
}



@Preview(showBackground = true)
@Composable
fun CustomSpinnerDropdownPreview() {
    val items = listOf("Motor", "Mobil", "Truk", "Bus")
    var selectedItem by remember { mutableStateOf(items.first()) }

    CustomSpinnerDropdown(
        items = items,
        hint = "",
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    )
}
