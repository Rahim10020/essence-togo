package com.example.essence_togo.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
@Preview
fun NavGraphPreview(){
    NavGraph()
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = BottomNavDestination.Home.route,
){
    NavHost(navController = navController, startDestination = startDestination) {
        // ecran d'acceuil
        composable(BottomNavDestination.Home.route) {

        }
    }