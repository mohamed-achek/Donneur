package com.example.donneur

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.donneur.ui.theme.BloodBondTheme
import com.example.donneur.ui.theme.Home
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val fragment: @Composable () -> Unit //Fragment content
)

data class NavigationItems(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val badgeCount: Int? = null
)



val custom_fontFamily = FontFamily(
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_extralight, FontWeight.ExtraLight),
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold , FontWeight.SemiBold)
)

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BloodBondTheme {
                MainScreen()
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    val items1 = listOf(
        NavigationItems(
            title = "Home",
            selectedIcon = painterResource(id = R.drawable.home_filled),
            unselectedIcon = painterResource(id = R.drawable.home_outlined),
        ),
        NavigationItems(
            title = "Profile",
            selectedIcon = painterResource(id = R.drawable.profile_filled),
            unselectedIcon = painterResource(id = R.drawable.profile_outlined),
        ),
        NavigationItems(
            title = "Chats",
            selectedIcon = painterResource(id = R.drawable.chat_filled),
            unselectedIcon = painterResource(id = R.drawable.chat_outlined),
            badgeCount = 45
        )
    )
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = painterResource(id = R.drawable.home_filled),
            unselectedIcon = painterResource(id = R.drawable.home_outlined),
            hasNews = false,
            fragment ={
                Column {
                    Home()
                    // Show posts below the Home content
                    PostsList() // This will show the posts in the Home tab
                }
            }
        ) ,
        BottomNavigationItem(
            title = "Add Post",
            selectedIcon = painterResource(id = R.drawable.add_post_filled),
            unselectedIcon = painterResource(id = R.drawable.add_post_outlined),
            hasNews = false,
            fragment = { Post() }
        ),
        BottomNavigationItem(
            title = "Chats",
            selectedIcon = painterResource(id = R.drawable.chat_filled),
            unselectedIcon = painterResource(id = R.drawable.chat_outlined),
            hasNews = true,
            fragment = { Chats()}
        ),
        BottomNavigationItem(
            title = "Blood Checker", // <-- Add a new tab for the checker
            selectedIcon = painterResource(id = R.drawable.ic_blood_outlined), // You need to provide this icon
            unselectedIcon = painterResource(id = R.drawable.ic_blood_outlined), // Use same icon for simplicity
            hasNews = false,
            fragment = { BloodTypeCompatibilityChecker() }
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = painterResource(id = R.drawable.profile_filled),
            unselectedIcon = painterResource(id = R.drawable.profile_outlined),
            hasNews = false,
            fragment = { Profile() } // Use the enriched Profile composable
        )
    )
    // A surface container using the 'background' color from the theme

    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        //this may create a confusion we have these values to establish the navigation drawer
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selectedItemsIndex by rememberSaveable {
            mutableStateOf(0)
        }
        val context = androidx.compose.ui.platform.LocalContext.current
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    LazyColumn(
                        modifier = Modifier
                    ) {
                        item{
                            Column(
                                modifier = Modifier.padding(25.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.profile2),
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp)
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Mohamed Achek",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = custom_fontFamily
                                    ),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                )
                                Text(
                                    text = "@" + "med_6",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Light ,
                                        fontFamily = custom_fontFamily
                                    ),
                                    fontSize = 16.sp,
                                    color = Color(0xFF687684)
                                )
                            }

                        }
                        item {
                            items1.forEachIndexed { index, item ->
                                NavigationDrawerItem(
                                    label = {
                                        Text(text = item.title, fontFamily = custom_fontFamily, fontWeight = FontWeight.SemiBold,fontSize = 16.sp)
                                    },
                                    selected = index == selectedItemIndex,
                                    onClick = {
                                        //basically we are applying that when we click on this drawer item its is selected than the navigation bar closes and the state has its value
                                        //part of applying the navigation drawer
                                        //navController.navigate(items.routine)  - if want to establish the screen to be opened
                                        selectedItemIndex = index
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painter = if (index== selectedItemIndex){
                                                item.selectedIcon
                                            }else item.unselectedIcon,
                                            contentDescription = item.title ,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    badge = {
                                        item.badgeCount?.let {
                                            Text(text = item.badgeCount.toString(),fontFamily = custom_fontFamily, fontSize = 16.sp)
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        item {
                            // Divider between the blocks
                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = Color.Gray
                            )
                        }
                        item {
                            Column (
                                modifier = Modifier.padding(25.dp)
                            ){
                                Text(
                                    text = "Settings and Privacy",
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = custom_fontFamily
                                    ),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Help Center",
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = custom_fontFamily
                                    ),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                // --- Sign Out Button ---
                                androidx.compose.material3.Button(
                                    onClick = {
                                        FirebaseAuth.getInstance().signOut()
                                        val intent = Intent(context, SignIn::class.java)
                                        context.startActivity(intent)
                                        if (context is ComponentActivity) {
                                            context.finish()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Text(
                                        text = "Sign Out",
                                        fontFamily = custom_fontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                        }
                    }
                }
            },
            drawerState = drawerState
        ) {
            val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehaviour.nestedScrollConnection),
                topBar = {
                    Surface(
                        shadowElevation = 2.dp
                    ) {
                        CenterAlignedTopAppBar (
                            title = {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp))
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = null
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.profile_photo),
                                        contentDescription = null,
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                            },
                            scrollBehavior = scrollBehaviour,
                        )
                    }

                },
//                bottomBar = {
//                    Card(
//                        modifier = Modifier
//                            .width(350.dp)
//                            .height(70.dp)
//                            .padding(start = 40.dp, bottom = 10.dp),
//                    ) {
//                        Box (
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            contentAlignment = Alignment.Center,
//                        ){
//                            NavigationBar(
//                                modifier = Modifier
//                                    .width(350.dp),
//                                tonalElevation =10.dp,
////                            containerColor = Color.White
//                            ) {
//                                items.forEachIndexed { index, item ->
//                                    this@NavigationBar.NavigationBarItem(
//                                        selected = selectedItemIndex == index,
//                                        onClick = { selectedItemIndex =index },
////                                        label = { Text(text = item.title) },
//                                        alwaysShowLabel = false,
//                                        icon = {
//                                            BadgedBox(
//                                                badge = {
//                                                    if(item.badgeCount != null){
//                                                        Badge{
//                                                            Text(text = item.badgeCount.toString())
//                                                        }
//                                                    }else if(item.hasNews){
//                                                        Badge()
//                                                    }
//                                                }
//                                            ) {
//                                                Icon(
//                                                    painter = if (index == selectedItemIndex) {
//                                                        item.selectedIcon
//                                                    } else item.unselectedIcon,
//                                                    contentDescription = item.title,
//                                                    modifier = Modifier
//                                                        .size(25.dp)
//                                                )
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                },
            ){values ->
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ){
                    items[selectedItemIndex].fragment()

                    Box(
                        modifier = Modifier
                            .padding(bottom = 15.dp) // Adjust padding as needed
                            .align(Alignment.BottomCenter)
                            .shadow(0.5.dp, RoundedCornerShape(14.dp))
                    ) {

                        Card(
                            modifier = Modifier
                                .background(Color.White)
                                .width(350.dp)
                                .height(65.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                NavigationBar(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    tonalElevation = 10.dp,
                                ) {
                                    items.forEachIndexed { index, item ->
                                        this@NavigationBar.NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = { selectedItemIndex = index },
                                            alwaysShowLabel = false,
                                            icon = {
                                                BadgedBox(
                                                    badge = {
                                                        if (item.badgeCount != null) {
                                                            Badge {
                                                                Text(text = item.badgeCount.toString())
                                                            }
                                                        } else if (item.hasNews) {
                                                            Badge()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = if (index == selectedItemIndex) {
                                                            item.selectedIcon
                                                        } else item.unselectedIcon,
                                                        contentDescription = item.title,
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar() {
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
    CenterAlignedTopAppBar (
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null)
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Image(
                    painter = painterResource(id = R.drawable.profile2),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehaviour
    )
}


@Preview
@Composable
fun display(){
    MainScreen()
}
