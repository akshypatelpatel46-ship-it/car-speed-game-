package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NitroCyan
import com.example.ui.theme.NitroGlow
import com.example.ui.theme.SpeedGold

enum class MotoSpeedNav(val title: String) {
    BIKE_PC("Superbike (PC)"),
    CAR_RACE("Car Speed"),
    GARAGE("Garage"),
    SCENERY("High-Level Scenery"),
    RECORDS("Records")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MotoSpeedMainContainer()
            }
        }
    }
}

@Composable
fun MotoSpeedMainContainer() {
    var currentScreen by remember { mutableStateOf(MotoSpeedNav.BIKE_PC) }
    var selectedBike by remember { mutableStateOf(SuperbikeCatalog.ALL_BIKES.first()) }
    var selectedCar by remember { mutableStateOf(CarCatalog.ALL_CARS.first()) }
    var totalCoinsBank by remember { mutableIntStateOf(200) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == MotoSpeedNav.BIKE_PC,
                    onClick = { currentScreen = MotoSpeedNav.BIKE_PC },
                    icon = { Icon(Icons.Default.TwoWheeler, contentDescription = "Superbike PC") },
                    label = { Text("Superbike (PC)", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NitroGlow,
                        indicatorColor = NitroGlow,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_bike_pc")
                )

                NavigationBarItem(
                    selected = currentScreen == MotoSpeedNav.SCENERY,
                    onClick = { currentScreen = MotoSpeedNav.SCENERY },
                    icon = { Icon(Icons.Default.Panorama, contentDescription = "High-Level Scenery") },
                    label = { Text("Scenery", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NitroCyan,
                        indicatorColor = NitroCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_scenery")
                )

                NavigationBarItem(
                    selected = currentScreen == MotoSpeedNav.GARAGE,
                    onClick = { currentScreen = MotoSpeedNav.GARAGE },
                    icon = { Icon(Icons.Default.TwoWheeler, contentDescription = "Garage") },
                    label = { Text("Garage", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = SpeedGold,
                        indicatorColor = SpeedGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_garage")
                )

                NavigationBarItem(
                    selected = currentScreen == MotoSpeedNav.CAR_RACE,
                    onClick = { currentScreen = MotoSpeedNav.CAR_RACE },
                    icon = { Icon(Icons.Default.ElectricCar, contentDescription = "Car Speed") },
                    label = { Text("Car Speed", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = GoldLight,
                        indicatorColor = GoldLight,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_car_speed")
                )

                NavigationBarItem(
                    selected = currentScreen == MotoSpeedNav.RECORDS,
                    onClick = { currentScreen = MotoSpeedNav.RECORDS },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Records") },
                    label = { Text("Records", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = SpeedGold,
                        indicatorColor = SpeedGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_records")
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            MotoSpeedNav.BIKE_PC -> {
                MotoSpeedGameScreen(
                    currentBike = selectedBike,
                    totalCoinsBank = totalCoinsBank,
                    onCoinsUpdated = { totalCoinsBank = it },
                    onGoToGarage = { currentScreen = MotoSpeedNav.GARAGE },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            MotoSpeedNav.SCENERY -> {
                HighLevelSceneryScreen(
                    onSelectAndRide = {
                        currentScreen = MotoSpeedNav.BIKE_PC
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            MotoSpeedNav.GARAGE -> {
                MotoGarageScreen(
                    currentBikeId = selectedBike.id,
                    totalCoins = totalCoinsBank,
                    onSelectBike = { selectedBike = it },
                    onCoinsUpdated = { totalCoinsBank = it },
                    onStartRide = { currentScreen = MotoSpeedNav.BIKE_PC },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            MotoSpeedNav.CAR_RACE -> {
                SpeedRaceGameScreen(
                    currentCar = selectedCar,
                    totalCoinsBank = totalCoinsBank,
                    onCoinsUpdated = { totalCoinsBank = it },
                    onGoToGarage = { currentScreen = MotoSpeedNav.GARAGE },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            MotoSpeedNav.RECORDS -> {
                SpeedRecordsScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
