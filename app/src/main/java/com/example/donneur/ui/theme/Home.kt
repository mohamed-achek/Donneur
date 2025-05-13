package com.example.donneur.ui.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.donneur.R
import com.example.donneur.custom_fontFamily
import com.example.donneur.data.Post1


//Area wise bar
data class TabItem(
    val title: String,
    val selectedState: Boolean
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(){
    val tabItems = listOf(
        TabItem("Tunis",true),
        TabItem("Ariana",false),
        TabItem("Ben Arous",false),
        TabItem("La Marsa",false),
        TabItem("Nabeul",false),
        TabItem("Hammamet",false),
    )
    Box (
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var selectedTabIndex by remember {
                mutableIntStateOf(0)
            }
            //for horizontal Scroll
            val pagerState = rememberPagerState {
                tabItems.size
            }
            LaunchedEffect(selectedTabIndex){
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress){
                if (!pagerState.isScrollInProgress){
                    selectedTabIndex = pagerState.currentPage
                }
            }
            Column (
                modifier=Modifier
            ){
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabItems.forEachIndexed { index, tabItem ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = index
                            },
                            text = {
                                Text(text = tabItem.title,fontWeight = FontWeight.Bold, fontFamily = custom_fontFamily, fontSize = 15.sp)
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .height(40.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                                .shadow(100.dp, RoundedCornerShape(15.dp),true,Color.Green,Color(0xFF26A586)
                                ),
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {pageIndex ->
                    when (pageIndex) {
                        0 -> {
                            // Content for first tab
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                PostFeed()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostFeed() {
    Column(
        modifier = Modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(Datasource().postList()) { post ->
                PostCard(post = post)
            }
        }
    }
}


@Composable
fun PostCard(post: Post1) {
    val custom_fontFamily = FontFamily(
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_light, FontWeight.Light),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_semibold , FontWeight.SemiBold)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp,start = 10.dp,end = 10.dp)
            .shadow(1.dp, RoundedCornerShape(15.dp))
            .background(Color.White),
        ){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(Color.White)
            ){
                Image(
                    painter = painterResource(id = post.imageResourceId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                        .shadow(10.dp, CircleShape)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row {
                        Text(
                            text = post.name,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_fontFamily
                            ),
                            fontSize = 15.sp,
                            modifier = Modifier
                        )
                        Text(
                            text = " @" + post.username,
                            style = TextStyle(
                                fontWeight = FontWeight.Light ,
                                fontFamily = custom_fontFamily
                            ),
                            fontSize = 15.sp,
                            color = Color(0xFF687684)
                        )
                        Text(
                            text = " " + post.time.toString() + "h",
                            style = TextStyle(
                                fontWeight = FontWeight.Light,
                                fontFamily = custom_fontFamily
                            ),
                            fontSize = 15.sp,
                            color = Color(0xFF687684)
                        )
                    }
                    //next code to written from here
                    //Adding the Space in between
                    Spacer(modifier = Modifier.height(4.dp))
                    //Body of the Card
                    Text(
                        text = stringResource(id = post.content),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontFamily = custom_fontFamily
                        ),
                        fontSize = 15.sp
                    )
                    // Other tweet details can be added here
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, end = 50.dp)
                    ){
                        OutlinedButton(onClick = { /*TODO*/ }, modifier =Modifier) {
                            Text(
                                text = "Help By Donating",
                                style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = custom_fontFamily
                                ),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun display(){
    Home()
}
