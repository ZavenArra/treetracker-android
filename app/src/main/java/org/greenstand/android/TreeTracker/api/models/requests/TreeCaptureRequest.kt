package org.greenstand.android.TreeTracker.api.models.requests

import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


class RawJsonAdapter: TypeAdapter<String>() {
    override fun write(out: JsonWriter?, value: String?) {
        out?.jsonValue(value)
    }
    override fun read(reader: JsonReader?): String {
        return JsonParser().parse(reader).toString()
    }
}

class TreeCaptureRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("capture_id")
    val treeId: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("note")
    val note: String?,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("abs_step_count")
    val stepCount: Long?,
    @SerializedName("delta_step_count")
    val deltaStepCount: Long?,
    @SerializedName("rotation_matrix")
    val rotationMatrix: String?,
    @SerializedName("extra_attributes")
    @JsonAdapter(RawJsonAdapter::class)
    val extraAttributes: String,
)