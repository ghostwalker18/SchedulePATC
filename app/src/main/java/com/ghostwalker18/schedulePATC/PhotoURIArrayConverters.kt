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
        return if (uris == null || uris.size == 0) null else com.google.gson.Gson().toJson(uris)
    }

    /**
     * Этот метод преобразует String из БД в ArrayList of Uri сущности.
     *
     * @param uriString  the data from the database column to be converted
     * @return
     */
    @TypeConverter
    fun fromString(uriString: String?): ArrayList<Uri>? {
        if (uriString == null) return null
        val listType: Type = object : com.google.gson.reflect.TypeToken<ArrayList<Uri?>?>() {}.type
        return com.google.gson.Gson().fromJson<ArrayList<Uri>>(uriString, listType)
    }
}