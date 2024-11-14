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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Этот класс представляет собой репозиторий данных прилвожения о расписании.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class ScheduleRepository(context : Context, db : AppDatabase, networkService : NetworkService){
    private val context: Context
    private val db : AppDatabase
    private val preferences: SharedPreferences
    private val networkService : NetworkService
    private val mainSelector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody"
    private val status = MutableLiveData<Status>()
    private val updateExecutorService = Executors.newFixedThreadPool(4)
    private val updateFutures: MutableList<Future<*>> = ArrayList()

    init{
        this.context = context
        this.db = db
        this.networkService = networkService
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    data class Status(val text: String, val progress: Int)

    /**
     * Этот метод обновляет репозиторий приложения.
     * Метод использует многопоточность и может вызывать исключения в других потоках.
     * Требуется интернет соединение.
     */
    fun update(){
        val downloadFor = preferences.getString("downloadFor", "all")
        var allJobsDone = true
        for (future in updateFutures) {
            allJobsDone = allJobsDone and future.isDone
        }

        if (allJobsDone) {
            updateFutures.clear()
            if (downloadFor == "all" || downloadFor == "first") updateFutures.add(
                updateExecutorService.submit { updateFirstCorpus() })
            if (downloadFor == "all" || downloadFor == "second") updateFutures.add(
                updateExecutorService.submit { updateSecondCorpus() })
            if(downloadFor == "all" || downloadFor == "third")
            updateFutures.add(updateExecutorService.submit { updateThirdCorpus() })
        }
    }

    /**
     * Этот метод используется для получения состояния,
     * в котором находится процесс обновления репозитория.
     *
     * @return статус состояния
     */
    fun getStatus(): LiveData<Status>? {
        return status
    }

    /**
     * Этот метод возвращает все группы, упоминаемые в расписании.
     *
     * @return список групп
     */
    fun getGroups(): LiveData<Array<String>> {
        return db.lessonDao().getGroups()
    }

    /**
     * Этот метод позволяет получить имена всех преподавателей, упоминаемых в расписании.
     *
     * @return список учителей
     */
    fun getTeachers(): LiveData<Array<String>> {
        return db.lessonDao().getTeachers()
    }

    /**
     * Этот метод позволяет получить список всех предметов в расписании для заданной группы.
     * @param group группа
     * @return список предметов
     */
    fun getSubjects(group: String): LiveData<Array<String>> {
        return db.lessonDao().getSubjectsForGroup(group)
    }

    /**
     * Этот метод возращает список занятий в этот день у группы у данного преподавателя.
     * Если группа не указана, то возвращается список занятий у преподавателя в этот день.
     * Если учитель не указан, то возвращается список занятй у группы в этот день.
     *
     * @param date день
     * @param teacher преподаватель
     * @param group группа
     * @return список занятий
     */

    /**
     * Этот метод возвращает сохраненную группу.
     *
     * @return группа
     */
    fun getSavedGroup(): String? {
        return preferences.getString("savedGroup", null)
    }

    /**
     * Этот метод позволяет получить имя скачиваемого файла из ссылки на него.
     *
     * @param link ссылка на файл
     * @return имя файла
     */
    fun getNameFromLink(link: String): String? {
        val parts = link.split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return parts[parts.size - 1]
    }

    private fun updateFirstCorpus(){

    }

    private fun updateSecondCorpus(){

    }

    private fun updateThirdCorpus(){

    }
}