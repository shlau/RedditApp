package com.example.redditapp.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redditapp.Constants.Companion.REDDIT_API
import com.example.redditapp.data.RedditAuthRepositoryImp
import com.example.redditapp.data.UserDataRepository
import com.example.redditapp.ui.model.AccessResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BEARER = "bearer"

@HiltViewModel
class RedditAppViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val authRepository: RedditAuthRepositoryImp
) : ViewModel() {
    val imgurClientIdFlow: Flow<String> = userDataRepository.imgurClientIdFlow
    val userTokenFlow: Flow<String> = userDataRepository.redditUserTokenFlow
    val tokenExpirationFlow: Flow<Long> = userDataRepository.redditTokenExpirationFlow
    val tokenTimestampFlow: Flow<Long> = userDataRepository.redditTokenTimestampFlow

    private val _uiState = MutableStateFlow(RedditAppUiState())
    val uiState: StateFlow<RedditAppUiState> = _uiState.asStateFlow()

    fun updateCode(code: String) {
        _uiState.value.code = code
    }

    fun getParamMapping(queryParamStr: String): HashMap<String, String> {
        val mapping = HashMap<String, String>()
        val params = queryParamStr.split("&")

        params.forEach {
            val parts = it.split("=")
            val paramKey = parts[0]
            val paramVal = parts[1]
            mapping[paramKey] = paramVal
        }

        return mapping
    }

    private suspend fun updateTokenData(res: AccessResponse) {
        userDataRepository.updateRedditUserToken("$BEARER ${res.accessToken}")
        userDataRepository.updateRedditRefreshToken(res.refreshToken)
        userDataRepository.updateRedditTokenExpiration(res.expiresIn)
        userDataRepository.updateRedditTokenTimestamp(System.currentTimeMillis())
    }

    fun getAccessResponse(code: String, clientId: String) {
        viewModelScope.launch {
            try {
                val clientSecret = ""
                val credentials: String = okhttp3.Credentials.basic(clientId, clientSecret)
                val res =
                    authRepository.getAccessToken(code = code, authorization = credentials)
                updateTokenData(res)
                userDataRepository.updateRedditClientId(clientId)
            } catch (e: Exception) {
                Log.d(REDDIT_API, e.toString())
            }
        }
    }

    fun isTokenExpired(tokenExpiration: Long, tokenTimestamp: Long): Boolean {
        if (tokenExpiration == null || tokenExpiration < 0 || tokenTimestamp == null || tokenTimestamp < 0) {
            return false
        }

        val currentTimestamp: Long = System.currentTimeMillis()
        val expiration: Long = tokenTimestamp + (tokenExpiration * 1000)
        return currentTimestamp > expiration
    }

    fun refreshAccessToken() {
        viewModelScope.launch {
            try {
                val clientId = userDataRepository.getRedditClientId().first() ?: ""
                val clientSecret = ""
                val credentials: String = okhttp3.Credentials.basic(clientId, clientSecret)
                val res =
                    authRepository.refreshAccessToken(
                        refreshToken = userDataRepository.getRedditRefreshToken().first() ?: "",
                        authorization = credentials
                    )
                updateTokenData(res)
            } catch (e: Exception) {
                Log.d(REDDIT_API, e.toString())
            }
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            userDataRepository.clearUserDataStore()
        }
    }
}
