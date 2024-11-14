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

import android.app.Application
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.material.color.DynamicColors
import java.util.Locale

/**
 * <h1>SchedulePATC</h1>
 * <p>
 *      Программа представляет собой мобильную реализацию приложения расписания ПАТТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  1.0
 */
class ScheduleApp : Application(), OnSharedPreferenceChangeListener {
    private val BASE_URI = "https://patt.karelia.ru/students/schedule"
    private lateinit var preferences: SharedPreferences
    private lateinit var database: AppDatabase
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var notesRepository: NotesRepository

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key){
            "theme" -> {
                val theme = sharedPreferences?.getString(key, "")
                setTheme(theme)
            }
            "language" -> {
                val localeCode = sharedPreferences?.getString(key, "en")
                setLocale(localeCode)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        instance = this
        database = AppDatabase.getInstance(this)
        scheduleRepository = ScheduleRepository(this, database, NetworkService(this, BASE_URI,preferences))
        scheduleRepository.update()
    }

    fun getNotesRepository() : NotesRepository {
        return notesRepository
    }

    private fun setLocale(localeCode : String?) {
        val localeListCompat = if (localeCode == "system")
            LocaleListCompat.getEmptyLocaleList()
        else
            LocaleListCompat.create(localeCode?.let { Locale(it) })
        AppCompatDelegate.setApplicationLocales(localeListCompat)
    }

    private fun setTheme(theme : String?){
        when(theme){
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "night" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "day" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    companion object {
        lateinit var instance : ScheduleApp
        fun getInstance() : ScheduleApp{
            return instance
        }
    }
}