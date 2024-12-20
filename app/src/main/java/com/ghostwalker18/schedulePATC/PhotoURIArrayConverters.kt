/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ghostwalker18.schedulePATC

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Этот класс используется для ORM.
 * Содержит методы для преобразования ArrayList of Uri в String для БД и наоборот
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class PhotoURIArrayConverters {
    /**
     * Этот метод преобразует ArrayList of Uri сущности в String для БД.
     *
     * @param uris  the entity attribute value to be converted
     * @return
     */
    @TypeConverter
    fun toString(uris: ArrayList<Uri?>?): String? {
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriJsonAdapter())
            .create()
        if (uris.isNullOrEmpty()) return null
        val listType = object : TypeToken<java.util.ArrayList<Uri?>?>() {}.type
        return gson.toJson(uris, listType)
    }

    /**
     * Этот метод преобразует String из БД в ArrayList of Uri сущности.
     *
     * @param uriString  the data from the database column to be converted
     * @return
     */
    @TypeConverter
    fun fromString(uriString: String?): ArrayList<Uri>? {
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriJsonAdapter())
            .create()
        if (uriString == null) return null
        val listType = object : TypeToken<java.util.ArrayList<Uri?>>() {}.type
        return gson.fromJson(uriString, listType)
    }

    /**
     * Этот класс используется для конвертации Uri в Json и наоборот.
     *
     * @author Ипатов Никита
     * @since 3.2
     */
    private inner class UriJsonAdapter : JsonSerializer<Uri?>,
        JsonDeserializer<Uri?> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
        ): Uri {
            return try {
                val uri = json.asString
                if (uri.isNullOrEmpty()) Uri.EMPTY
                else Uri.parse(uri)
            } catch (e: UnsupportedOperationException) {
                Uri.EMPTY
            }
        }

        override fun serialize(src: Uri?, typeOfSrc: Type, context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(src.toString())
        }
    }
}