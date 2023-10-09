package com.example.fifthsemproject.presentation.screens.codeforces

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.screendata.UniversalColors
import com.example.fifthsemproject.presentation.viewmodels.CodeforcesViewModel
import com.google.type.DateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    codeforcesViewModel: CodeforcesViewModel = hiltViewModel()
){
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {

        var openContest by remember { mutableStateOf(false) }

        Text(
            text = "   Find a user by Handle",
            color = UniversalColors.codeforcesColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
        )
        val lfm = LocalFocusManager.current
        TextField(
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color(184, 192, 255, 255)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            value = codeforcesViewModel.handle,
            onValueChange = { codeforcesViewModel.handle = it },
            trailingIcon = {
                if (codeforcesViewModel.loading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else if(codeforcesViewModel.handle.isNotBlank()){
                    Icon(
                        painter = painterResource(id = R.drawable.remove_icon),
                        contentDescription = "remove",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                codeforcesViewModel.handle = ""
                            }
                    )
                }
                    
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = {
                codeforcesViewModel.tempRatingCount = 4
                codeforcesViewModel.ratingShowCount = minOf(
                        codeforcesViewModel.tempRatingCount,
                        codeforcesViewModel.userRating.result.size
                    )
                codeforcesViewModel.getUserInfoByHandle(context)
                lfm.clearFocus()
            })
        )
        if(codeforcesViewModel.incorrectHandle){
            Text(
                text = "Handle does not exist",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            )
        }
        if(codeforcesViewModel.handle.isBlank()){
            codeforcesViewModel.pastHandles.forEach {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(0.dp, 5.dp)
                        .fillMaxWidth()
                        .padding(10.dp,5.dp)
                ) {
                    Text(text = it, color = Color.White, fontSize = 15.sp, modifier = Modifier.clickable {
                        codeforcesViewModel.handle = it
                        codeforcesViewModel.tempRatingCount = 4
                        codeforcesViewModel.ratingShowCount = minOf(
                            codeforcesViewModel.tempRatingCount,
                            codeforcesViewModel.userRating.result.size
                        )
                        codeforcesViewModel.getUserInfoByHandle(context)
                        lfm.clearFocus()
                    })
                    Icon(
                        painter = painterResource(id = R.drawable.remove_icon),
                        contentDescription = "remove",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                codeforcesViewModel.removeHandle(it, context)
                                codeforcesViewModel.getHandles(context)
                            }
                    )
                }
            }
        }

        AnimatedVisibility(visible = codeforcesViewModel.userInfo.status=="OK" && !codeforcesViewModel.loading && codeforcesViewModel.handle.isNotBlank()) {
//            Text(text = codeforcesViewModel.data.result.toString(), color = Color.White)
            UserInfo(codeforcesViewModel)
        }

        if(openContest){
            AlertDialog(
                text = {
                       Text(
                           text = "Open Contest in Browser?",
                           color = UniversalColors.codeforcesColor,
                           fontSize = 16.sp
                       )
                },
                onDismissRequest = { openContest = false },
                confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0, 195, 255, 255)
                    ),
                    onClick = {
                    val url = "https://codeforces.com/contest/${codeforcesViewModel.contestToOpen}"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(context, intent, null)
                }) {
                    Text(text = "OPEN", color = UniversalColors.backgroundColor, fontSize = 16.sp)
                }
            })


        }


        if(codeforcesViewModel.userRating.result.isNotEmpty() && !codeforcesViewModel.loading && codeforcesViewModel.handle.isNotBlank()){
            Text(
                text = "Contest Ratings",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(0.dp, 5.dp)
                    .fillMaxWidth()
            )


            for (index in 0..codeforcesViewModel.ratingShowCount) {
                if(index>=codeforcesViewModel.userRating.result.size) {
                    continue
                }
                Row(
                    modifier = Modifier
                        .padding(5.dp, 0.dp)
                        .fillMaxWidth()
                        .background(
                            Color(41, 41, 41, 255),
                            shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)
                        )
                        .padding(5.dp)
                        .clickable {
                            codeforcesViewModel.contestToOpen =
                                codeforcesViewModel.userRating.result[index].contestId
                            openContest = true
                        }
                ) {
                    val rating = codeforcesViewModel.userRating.result[index].newRating-codeforcesViewModel.userRating.result[index].oldRating
                    Text(
                        text = (if(rating<0) "" else "+")+rating.toString()+"  ",
                        color = if (rating < 0) Color(255, 117, 117, 255) else Color(
                            142,
                            255,
                            111,
                            255
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = codeforcesViewModel.userRating.result[index].contestName,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(5.dp, 0.dp, 5.dp, 10.dp)
                        .fillMaxWidth()
                        .background(
                            Color(41, 41, 41, 255),
                            shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp)
                        )
                        .padding(5.dp)
                        .clickable {
                            codeforcesViewModel.contestToOpen =
                                codeforcesViewModel.userRating.result[index].contestId
                            openContest = true
                        }
                ) {
                    Text(
                        text = Date(codeforcesViewModel.userRating.result[index].ratingUpdateTimeSeconds*1000).toString(),
                        color = Color(255, 255, 255, 193),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Rank "+codeforcesViewModel.userRating.result[index].rank.toString(),
                        color = Color(255, 255, 255, 193),
                        fontSize = 14.sp
                    )
                }
            }


            Text(text = "Show more", color = Color.Cyan, fontSize = 14.sp, textAlign = TextAlign.End, modifier = Modifier
                .padding(0.dp, 5.dp)
                .fillMaxWidth()
                .clickable {
                    codeforcesViewModel.tempRatingCount += 10
                    codeforcesViewModel.ratingShowCount =
                        minOf(
                            codeforcesViewModel.tempRatingCount,
                            codeforcesViewModel.userRating.result.size
                        )
                })

        }




    Spacer(modifier = Modifier.height(200.dp))
    }



}


@Composable
fun UserInfo(codeforcesViewModel: CodeforcesViewModel) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(200.dp, 200.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = codeforcesViewModel.userInfo.result[0].firstName +" "+ codeforcesViewModel.userInfo.result[0].lastName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = codeforcesViewModel.userInfo.result[0].country+", "+codeforcesViewModel.userInfo.result[0].city,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp, 5.dp)
            )
            Text(
                text = "From: "+codeforcesViewModel.userInfo.result[0].organization,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp, 5.dp)
            )
            Text(
                text = codeforcesViewModel.userInfo.result[0].rank+" "+codeforcesViewModel.userInfo.result[0].rating.toString(),
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp, 5.dp)
            )
            Text(
                text = "Contributions: +"+codeforcesViewModel.userInfo.result[0].contribution.toString(),
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp, 5.dp)
            )


        }
        AsyncImage(
            filterQuality = FilterQuality.High,
            model = codeforcesViewModel.userInfo.result[0].avatar,
            contentDescription = "User Avatar",
            modifier = Modifier
                .padding(10.dp)
                .size(120.dp)

        )

        

    }
}













