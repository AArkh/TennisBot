package tennis.bot.mobile.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import tennis.bot.mobile.feed.ActivityBasicResponse
import tennis.bot.mobile.feed.PostData
import tennis.bot.mobile.feed.PostParent
import java.lang.reflect.Type


class PostDataConverterFactory(private val json: Json) : Converter.Factory() {

	override fun responseBodyConverter(
		type: Type,
		annotations: Array<out Annotation>,
		retrofit: Retrofit
	): Converter<ResponseBody, *>? {
		if (type == ActivityBasicResponse::class.java) {
			return ActivityBasicResponseConverter(json)
		}
		return null
	}

	class ActivityBasicResponseConverter(private val json: Json) : Converter<ResponseBody, ActivityBasicResponse> {

		override fun convert(value: ResponseBody): ActivityBasicResponse {
			val jsonValue = value.string()
			val jsonObject = json.decodeFromString(JsonObject.serializer(), jsonValue)
			val totalCount = jsonObject["totalCount"]?.jsonPrimitive?.int ?: 0
			val itemsJsonArray = jsonObject["items"]?.jsonArray ?: JsonArray(emptyList())

			val items = itemsJsonArray.map { itemJson ->
				val id = itemJson.jsonObject["id"]?.jsonPrimitive?.long ?: 0
				val postType = itemJson.jsonObject["postType"]?.jsonPrimitive?.int ?: 0
				val totalLikes = itemJson.jsonObject["totalLikes"]?.jsonPrimitive?.int ?: 0
				val liked = itemJson.jsonObject["liked"]?.jsonPrimitive?.boolean ?: false
				val postJson = itemJson.jsonObject["post"] ?: JsonObject(emptyMap())
				val addedAt = itemJson.jsonObject["addedAt"]?.jsonPrimitive?.content

				val postParent = when (postType) {
					1 -> json.decodeFromJsonElement(PostParent.NewPlayerPost.serializer(), postJson)
					2 -> json.decodeFromJsonElement(PostParent.MatchRequestPost.serializer(), postJson)
					3 -> json.decodeFromJsonElement(PostParent.ScorePost.serializer(), postJson)
					else -> throw IllegalArgumentException(
						"$postType is not a supported post type.",
					)
				}

				PostData(id, postType, totalLikes, liked, postParent, addedAt)
			}

			return ActivityBasicResponse(totalCount, items)
		}
	}
}