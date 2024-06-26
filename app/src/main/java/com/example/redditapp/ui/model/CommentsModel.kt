package com.example.redditapp.ui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class CommentsModel(val data: CommentsDataModel)

@Serializable
data class CommentsDataModel(val children: List<CommentModel>)

@Serializable
data class CommentModel(
    @Serializable(with = CommentDataSerializer::class)
    var data: CommentDataModel,
    val kind: String
)

@Serializable
data class CommentDataModel(
    val author: String? = null,
    val replies: CommentsModel? = null,
    val body: String? = null,
    @SerialName("body_html") val bodyHtml: String? = null,
    val ups: Int? = null,
    val title: String? = null,
    @SerialName("selftext") val selfText: String? = null,
    val id: String,
    @SerialName("parent_id") val parentId: String? = null,
    var depth: Int? = null,
    val children: List<String>? = null,
    val name: String? = null,
    val thumbnail: String? = null,
    @SerialName(value = "url_overridden_by_dest") val destUrl: String?,
)

@Serializable
data class MoreChildrenModel(val things: List<CommentModel>)

@Serializable
data class MoreChildrenJson(val data: MoreChildrenModel)

@Serializable
data class MoreChildrenResponse(
    val json: MoreChildrenJson
)

object CommentDataSerializer :
    JsonTransformingSerializer<CommentDataModel>(CommentDataModel.serializer()) {
    // filter out replies with empty strings
    override fun transformDeserialize(element: JsonElement): JsonElement =
        JsonObject(element.jsonObject.filterNot { (k, v) ->
            k == "replies" && v !is JsonObject && v.jsonPrimitive.content == ""
        })
}