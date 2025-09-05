package com.example.ecojourney.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@Composable
fun ClickableTabs(
    selectedItem: Int,
    tabsList: List<String>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedItemIndex = remember { mutableStateOf(selectedItem) }

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(15.dp))
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            tabsList.forEachIndexed { index, s ->
                val isSelected = index == selectedItemIndex.value
                TabItem(
                    isSelected = isSelected,
                    text = s,
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        selectedItemIndex.value = index
                        onClick.invoke(selectedItemIndex.value)
                    },
                    shape = getTabShape(index, selectedItemIndex.value, tabsList.size)
                )
            }
        }
    }
}



@Composable
fun TabItem(
    isSelected: Boolean,
    text: String,
    modifier: Modifier,
    onClick: () -> Unit,
    shape: RoundedCornerShape
) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF4C4C4C),
        animationSpec = tween(easing = LinearEasing),
        label = ""
    )

    val background: Color by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF3F6B1B) else Color(0xFFA7BB97),
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = ""
    )

    val fontFamily = if (isSelected) PJakartaFontFamily else PJakartaSansFontFamily
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(background, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            color = tabTextColor
        )
    }
}

fun getTabShape(index: Int, selectedIndex: Int, tabCount: Int): RoundedCornerShape {
    return when {
        // Case for the left tab being selected
        index == selectedIndex && index == 0 -> {
            // Left tab selected: rounded left, straight right
            RoundedCornerShape(
                topStart = 15.dp,
                topEnd = 0.dp,
                bottomStart = 15.dp,
                bottomEnd = 0.dp
            )
        }
        // Case for the right tab being selected
        index == selectedIndex && index == tabCount - 1 -> {
            // Right tab selected: rounded right, straight left
            RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 15.dp,
                bottomStart = 0.dp,
                bottomEnd = 15.dp
            )
        }
        // Case for non-selected left tab
        index == 0 -> {
            // Left tab non-selected: rounded left, straight right
            RoundedCornerShape(
                topStart = 15.dp,
                topEnd = 0.dp,
                bottomStart = 15.dp,
                bottomEnd = 0.dp
            )
        }
        // Case for non-selected right tab
        index == tabCount - 1 -> {
            // Right tab non-selected: rounded right, straight left
            RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 15.dp,
                bottomStart = 0.dp,
                bottomEnd = 15.dp
            )
        }
        // Default case for other tabs (in case of more than 2 tabs)
        else -> {
            RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        }
    }
}


@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    diameter: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(diameter)
            .background(color = Color.White, shape = CircleShape)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(14.dp)
                .align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Tabs() {
    Column(modifier = Modifier.padding(20.dp)) {
        ClickableTabs(selectedItem = 0, tabsList = listOf("List Item 1", "List Item 2"), onClick = {})
    }
}

@Preview
@Composable
fun BackButtonPreview() {
    BackButton()
}