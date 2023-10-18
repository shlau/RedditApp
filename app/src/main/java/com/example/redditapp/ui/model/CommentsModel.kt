package com.example.redditapp.ui.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID

@Serializable
data class CommentsModel(val data: CommentsDataModel)

@Serializable
data class CommentsDataModel(val children: List<CommentModel>)

@Serializable
data class CommentModel(
    @Serializable(with = CommentDataSerializer::class)
    val data: CommentDataModel,
    var depth: Int? = null,
    @Serializable(with = UUIDSerializer::class)
    var id: UUID?
)

@Serializable
data class CommentDataModel(
    val author: String? = null,
    val replies: CommentsModel? = null,
    val body: String? = null,
    @SerialName("body_html") val bodyHtml: String? = null,
    val ups: Int? = null,
    val title: String? = null,
    @SerialName("self_text") val selfText: String? = null,
)

object CommentDataSerializer :
    JsonTransformingSerializer<CommentDataModel>(CommentDataModel.serializer()) {
    // filter out replies with empty strings
    override fun transformDeserialize(element: JsonElement): JsonElement =
        JsonObject(element.jsonObject.filterNot { (k, v) ->
            k == "replies" && v !is JsonObject && v.jsonPrimitive.content == ""
        })
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}