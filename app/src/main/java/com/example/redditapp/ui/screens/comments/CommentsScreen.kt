package com.example.redditapp.ui.screens.comments

import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.example.redditapp.Constants.Companion.OAUTH_BASE_URL
import com.example.redditapp.ui.model.CommentDataModel
import com.example.redditapp.ui.model.CommentModel

enum class CommentKind {
    more
}

@Composable
fun CommentsScreen(
    viewModel: CommentsViewModel,
    url: String,
    permalink: String,
    modifier: Modifier = Modifier
) {
    val commentsUiState = viewModel.uiState.collectAsState()
    val originalPostData = commentsUiState.value.originalPost?.data
    LaunchedEffect(Unit) {
        if (commentsUiState.value.permalink != permalink) {
            viewModel.getComments("$OAUTH_BASE_URL$permalink.json")
            viewModel.updatePermalink(permalink)
        }
    }
    if (originalPostData != null) {
        Column {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = originalPostData.title ?: "")
                val selfText: String? = originalPostData.selfText
                if (selfText != null) {
                    val postBody: Spanned =
                        HtmlCompat.fromHtml(
                            originalPostData.selfText,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                    AndroidView(
                        factory = { it -> TextView(it) },
                        update = { it -> it.text = postBody },
                    )
                }
            }
            CommentsContainer(
                viewModel,
                commentsUiState.value.comments, modifier
            )
        }
    }
}

@Composable
fun CommentsContainer(
    viewModel: CommentsViewModel,
    comments: List<CommentModel>,
    modifier: Modifier = Modifier
) {
    val commentsUiState = viewModel.uiState.collectAsState()
    LazyColumn {
        commentNodes(
            null,
            { node: CommentModel?, loadNode: CommentModel, idx: Int ->
                viewModel.loadMoreComments(
                    node,
                    loadNode,
                    idx
                )
            },
            comments,
            0,
            commentsUiState.value.commentColors,
            { node: CommentModel -> viewModel.isExpanded(node) }
        ) { node: CommentModel -> viewModel.toggleExpandedComments(node) }
    }
}

fun LazyListScope.commentNodes(
    parentNode: CommentModel?,
    loadMoreComments: (node: CommentModel?, loadNode: CommentModel, idx: Int) -> Unit,
    nodes: List<CommentModel>,
    depth: Int,
    commentColors: List<Color>,
    isExpanded: (node: CommentModel) -> Boolean,
    toggleExpanded: (node: CommentModel) -> Unit
) {
    nodes.forEachIndexed { idx, node ->
        commentNode(
            parentNode,
            loadMoreComments,
            node,
            depth,
            commentColors,
            isExpanded,
            toggleExpanded,
            idx
        )
    }
}

fun LazyListScope.commentNode(
    parentNode: CommentModel?,
    loadMoreComments: (node: CommentModel?, loadNode: CommentModel, idx: Int) -> Unit,
    node: CommentModel,
    depth: Int,
    commentColors: List<Color>,
    isExpanded: (node: CommentModel) -> Boolean,
    toggleExpanded: (node: CommentModel) -> Unit,
    idx: Int
) {
    val commentData: CommentDataModel = node.data
    val kind: String = node.kind
    item {
        val bodyHtml: String? = commentData.bodyHtml
        if (bodyHtml != null) {
            val decodedComment = Html.fromHtml(bodyHtml, Html.FROM_HTML_MODE_LEGACY).toString()
            val commentBody: Spanned =
                HtmlCompat.fromHtml(decodedComment, HtmlCompat.FROM_HTML_MODE_COMPACT)
            Box(
                modifier = Modifier
                    .border(width = 1.dp, color = Color.Black)
                    .padding(start = 10.dp)
                    .padding(start = (depth * 10).dp)
                    .clickable { toggleExpanded(node) }
            ) {
                AndroidView(
                    factory = { it ->
                        val tv = TextView(it)
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        return@AndroidView tv
                    },
                    update = { it -> it.text = commentBody },
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = commentColors[depth % 7]
                        )
                        .padding(10.dp)
                )
            }
        }
        if (kind == CommentKind.more.name) {
            Box(
                modifier = Modifier
                    .border(width = 1.dp, color = Color.Cyan)
                    .padding(start = 10.dp)
                    .padding(start = (depth * 10).dp)
                    .clickable {
                        loadMoreComments(parentNode, node, idx)
                    }
            ) {
                val numReplies: Int = commentData.children!!.size
                Text(text = "Load more... ($numReplies replies)", color = Color.Blue)
            }
        }
    }
    val replies: List<CommentModel>? = (commentData.replies?.data?.children)
    if (replies != null && isExpanded(node)) {
        commentNodes(
            node,
            loadMoreComments,
            replies,
            depth + 1,
            commentColors,
            isExpanded,
            toggleExpanded
        )
    }
}

