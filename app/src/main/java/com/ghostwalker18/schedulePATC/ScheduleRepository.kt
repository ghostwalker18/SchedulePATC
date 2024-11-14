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

/**
 * Этот класс представляет собой репозиторий данных прилвожения о расписании.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class ScheduleRepository(context : Context, db : AppDatabase, networkService : NetworkService){
    private var context: Context
    private var db : AppDatabase
    private var networkService : NetworkService

    init{
        this.context = context
        this.db = db
        this.networkService = networkService
    }
}