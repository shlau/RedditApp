package com.example.redditapp.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.redditapp.Constants.Companion.REDDIT_API
import com.example.redditapp.ui.screens.auth.imgur.ImgurAuthScreen
import com.example.redditapp.ui.screens.auth.reddit.RedditAuthScreen
import com.example.redditapp.ui.screens.comments.CommentsScreen
import com.example.redditapp.ui.screens.comments.CommentsViewModel
import com.example.redditapp.ui.screens.subreddit.SubredditPage

enum class NavRoutes {
    IMGUR_AUTH,
    REDDIT_AUTH,
    LOGIN,
    COMMENTS,
    HOME
}

@Composable
fun RedditAppScreen(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val viewModel: RedditAppViewModel = viewModel()
    val imgurClientId = viewModel.imgurClientIdFlow.collectAsStateWithLifecycle(initialValue = "")
    val userToken = viewModel.userTokenFlow.collectAsStateWithLifecycle(initialValue = "")
    val tokenExpirationFlow =
        viewModel.tokenExpirationFlow.collectAsStateWithLifecycle(initialValue = -1)
    val tokenTimestampFlow =
        viewModel.tokenTimestampFlow.collectAsStateWithLifecycle(initialValue = -1)
    val redditAppUiState = viewModel.uiState.collectAsState()
    val commentsViewModel: CommentsViewModel = viewModel()

    NavHost(navController = navController, startDestination = NavRoutes.REDDIT_AUTH.name) {
        fun navToComments(url: String, permalink: String) {
            navController.navigate("${NavRoutes.COMMENTS.name}?url=$url&permalink=$permalink")
        }
        composable(
            route = "${NavRoutes.COMMENTS.name}?url={url}&permalink={permalink}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
            }, navArgument("permalink") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val url = navBackStackEntry.arguments?.getString("url")
            val permalink = navBackStackEntry.arguments?.getString("permalink")
            CommentsScreen(commentsViewModel, url ?: "", permalink ?: "")
        }
        composable(route = NavRoutes.HOME.name) {
            SubredditPage(navToComments = { url: String, permalink: String ->
                navToComments(
                    url,
                    permalink
                )
            })
        }
        composable(route = NavRoutes.REDDIT_AUTH.name) {
            Log.d(REDDIT_API, userToken.value)
            if (viewModel.isTokenExpired(tokenExpirationFlow.value, tokenTimestampFlow.value)) {
                LaunchedEffect(Unit) {
                    viewModel.refreshAccessToken()
                }
            } else if (userToken.value == "") {
                RedditAuthScreen()
            } else {
                SubredditPage(navToComments = { url: String, permalink: String ->
                    navToComments(
                        url,
                        permalink
                    )
                })
            }
        }
        composable(
            route = NavRoutes.LOGIN.name,
            deepLinks = listOf(navDeepLink {
                uriPattern = "cyan://reddit?{params}"
                action = Intent.ACTION_VIEW
            }),
            arguments = listOf(navArgument("params") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { navBackStackEntry ->
            if (userToken.value != "") {
                if (imgurClientId.value == "") {
                    ImgurAuthScreen()
                } else {
                    SubredditPage(navToComments = { url: String, permalink: String ->
                        navToComments(
                            url,
                            permalink
                        )
                    })
                }
            } else {
                val queryParamString = navBackStackEntry.arguments?.getString("params")
                val paramMapping = viewModel.getParamMapping(queryParamString ?: "")
                val code = paramMapping["code"]
                val clientId = paramMapping["state"]
                LaunchedEffect(Unit) {
                    if (code != null && clientId != null && redditAppUiState.value.code == "") {
                        viewModel.updateCode(code)
                        viewModel.getAccessResponse(code, clientId)
                    }
                }
            }
        }
    }
}
