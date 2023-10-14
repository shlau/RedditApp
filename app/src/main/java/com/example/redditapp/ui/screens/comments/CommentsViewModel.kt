package com.example.redditapp.ui.screens.comments

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redditapp.Constants.Companion.OAUTH_BASE_URL
import com.example.redditapp.Constants.Companion.REDDIT_API
import com.example.redditapp.data.RedditAuthRepositoryImp
import com.example.redditapp.data.UserDataRepository
import com.example.redditapp.ui.model.CommentModel
import com.example.redditapp.ui.model.CommentsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val authRepository: RedditAuthRepositoryImp,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(CommentsUiState(comments = emptyList<CommentModel>()))
    val uiState = _uiState.asStateFlow()
    val url = checkNotNull(savedStateHandle.get<String>("url"))
    val permalink = checkNotNull(savedStateHandle.get<String>("permalink"))

    private fun getComments(url: String) {
        viewModelScope.launch {
            val commentsResponse: List<CommentsModel> = authRepository.getComments(url)
            val comments: List<CommentModel> = commentsResponse[1].data.children
            _uiState.value.comments = comments
        }
    }

    init {
        getComments("$OAUTH_BASE_URL$permalink.json")
    }
}