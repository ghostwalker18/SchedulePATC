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

import android.content.Context
import android.content.SharedPreferences

/**
 * Этот класс используется для предоставления приложению услуг доступа к сети.
 *
 * @author Ipatov Nikita
 * @since 1.0
 */
class NetworkService(context: Context, baseUri : String, preferences: SharedPreferences) {
    private val SIZE_OF_CACHE = 10 * 1024 * 1024
}