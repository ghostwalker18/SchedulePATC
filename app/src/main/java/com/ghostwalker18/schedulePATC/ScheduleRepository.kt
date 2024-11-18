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
import okhttp3.ResponseBody
import org.apache.poi.openxml4j.util.ZipSecureFile
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.concurrent.Callable
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
    private val api : ScheduleNetworkAPI
    private val parser: IConverter = PDFToLessonsConverter()
    private val status = MutableLiveData<Status>()
    private val updateExecutorService = Executors.newFixedThreadPool(4)
    private val updateFutures: MutableList<Future<*>> = ArrayList()

    init{
        this.context = context
        this.db = db
        api = networkService.getScheduleAPI()
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
    fun getStatus(): LiveData<Status> {
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

    fun getLessons(group: String?, teacher: String?, date: Calendar): LiveData<Array<Lesson>>{
        return if (teacher != null && group != null) db.lessonDao().getLessonsForGroupWithTeacher(
            date,
            group,
            teacher
        ) else if (teacher != null) db.lessonDao()
            .getLessonsForTeacher(date, teacher) else if (group != null) db.lessonDao()
            .getLessonsForGroup(date, group) else MutableLiveData(
            arrayOf()
        )
    }

    /**
     * Этот метод предназначен для сохранения последней выбранной группы перед закрытием приложения.
     *
     * @param group группа для сохранения
     */
    fun saveGroup(group: String?) {
        preferences.edit()
            .putString("savedGroup", group)
            .apply()
    }

    /**
     * Этот метод возвращает сохраненную группу.
     *
     * @return группа
     */
    fun getSavedGroup(): String? {
        return preferences.getString("savedGroup", null)
    }

    /**
     * Этот метод получает ссылки с сайта ПАТТ,
     * по которым доступно расписание для корпуса на Первомайском пр.
     *
     * @return список ссылок
     */
    fun getLinksForFirstCorpusSchedule(): List<String> {
        val links: MutableList<String> = java.util.ArrayList()
        val document = api.getMainPage().execute().body()
        val linkElemens: Elements? = document
            ?.select("h2:contains(Учебный корпус № 1 (пр. Первомайский, 46)) + div a")
        if (linkElemens != null)
            for (linkElement in linkElemens)
                linkElement.attr("href")
                    ?.let { if (it.endsWith(".pdf")) links.add(ScheduleApp.baseUri + it) }
        return links
    }

    /**
     * Этот метод получает ссылки с сайта ПАТТ,
     * по которым доступно расписание для корпуса на ул.Советской.
     *
     * @return список ссылок
     */
    fun getLinksForSecondCorpusSchedule(): List<String> {
        val links: MutableList<String> = java.util.ArrayList()
        val document = api.getMainPage().execute().body()
        val linkElemens: Elements? = document
            ?.select("h2:contains(Учебный корпус № 2 (ул. Советская, 11)) + div a")
        if (linkElemens != null)
            for (linkElement in linkElemens)
                linkElement.attr("href")
                    ?.let { if (it.endsWith(".pdf")) links.add(ScheduleApp.baseUri + it) }
        return links
    }

    /**
     * Этот метод получает ссылки с сайта ПАТТ,
     * по которым доступно расписание для корпуса на ул.Ленинградской.
     *
     * @return список ссылок
     */
    fun getLinksForThirdCorpusSchedule(): List<String> {
        val links: MutableList<String> = java.util.ArrayList()
        val document = api.getMainPage().execute().body()
        val linkElemens: Elements? = document
            ?.select("h2:contains(Учебный корпус № 3 (ул. Ленинградская, 11)) + div a")
        if (linkElemens != null)
            for (linkElement in linkElemens)
                linkElement.attr("href")
                    ?.let { if (it.endsWith(".pdf")) links.add(ScheduleApp.baseUri + it) }
        return links
    }

    /**
     * Этот метод позволяет получить имя скачиваемого файла из ссылки на него.
     *
     * @param link ссылка на файл
     * @return имя файла
     */
    fun getNameFromLink(link: String): String {
        val parts = link.split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return parts[parts.size - 1]
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для первого корпуса
     */
    private fun updateFirstCorpus(){
        updateSchedule(this::getLinksForFirstCorpusSchedule){
            parser.convertFirstCorpus(it)
        }
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для второго корпуса
     */
    private fun updateSecondCorpus(){
        updateSchedule(this::getLinksForSecondCorpusSchedule){
            parser.convertSecondCorpus(it)
        }
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для третьего корпуса
     */
    private fun updateThirdCorpus(){
        updateSchedule(this::getLinksForThirdCorpusSchedule){
            parser.convertThirdCorpus(it)
        }
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями
     * @param linksGetter метод для получения ссылок на файлы расписания
     * @param parser парсер файлов расписания
     */
    private fun updateSchedule(linksGetter: Callable<List<String>>,
                               parser: (file: XWPFDocument) -> List<Lesson>) {
        var scheduleLinks: List<String> = java.util.ArrayList()
        try {
            scheduleLinks = linksGetter.call()
        } catch (ignored: Exception) { /*Not required*/ }
        if (scheduleLinks.isEmpty())
            status.postValue(Status(
                context.getString(R.string.schedule_download_error),
                0))
        for (link in scheduleLinks) {
            status.postValue(Status(
                    context.getString(R.string.schedule_download_status),
                10))
            api.getScheduleFile(link).enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>) {
                    if (response.body() != null) {
                        status.postValue(
                            Status(context.getString(R.string.schedule_parsing_status), 33))
                        ZipSecureFile.setMinInflateRatio(0.0075)
                        try {
                            XWPFDocument(response.body()!!.byteStream()).use { pdfFile ->
                                val lessons: List<Lesson> = parser.invoke(pdfFile)
                                db.lessonDao().insertMany(lessons)
                                status.postValue(
                                    Status(context.getString(R.string.processing_completed_status), 100))
                            }
                        } catch (e: Exception) {
                            status.postValue(
                                Status(context.getString(R.string.schedule_parsing_error), 0))
                        }
                        response.body()!!.close()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    status.postValue(
                        Status(context.getString(R.string.schedule_download_error), 0))
                }
            })
        }
    }

    companion object {
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
    }
}