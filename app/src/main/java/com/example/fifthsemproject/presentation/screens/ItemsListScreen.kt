package com.example.fifthsemproject.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.DefaultTintColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fifthsemproject.MainViewModel
import com.example.fifthsemproject.presentation.navigation.Screen
import com.example.fifthsemproject.presentation.screendata.ItemData

@Composable
fun ItemsListScreen(
    itemList:List<ItemData>,
    onClick:(screen: Screen) -> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(24, 24, 24, 255)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = Color(209, 209, 209, 139)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Available Tools", fontWeight = FontWeight.Light, color = color, fontSize = 11.sp)
        Spacer(modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(0.8f)
            .height(1.dp)
            .background(color)
        )
        itemList.forEach {
            SingleButton(itemData = it, onClickScreen = onClick) {
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SingleButton(
    itemData: ItemData,
    onClickScreen: (screen: Screen) -> Unit,
    onClick: () -> Unit
){
    Row(modifier = Modifier
        .padding(10.dp, 5.dp)
        .fillMaxWidth()
        .height(70.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(Color(37, 37, 37, 255))
        .clickable {
            onClick()
            onClickScreen(itemData.screen)
        }
        .padding(10.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = itemData.icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = itemData.iconColor ?: DefaultTintColor
        )
        Text(
            text = itemData.title,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 17.sp,
            modifier = Modifier.padding(start = 25.dp)
        )
    }
}
