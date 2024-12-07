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

import java.util.Calendar

/**
 * В этом объекте содержаться различные вспомогательные методы, использующиеся по всему приложению.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
object Utils {
    /**
     * Этот метод используется для проверки, является ли заданная дата сегодняшним днем.
     * @param date дата для проверки
     * @return сегодня ли дата
     */
    fun isDateToday(date: Calendar): Boolean {
        val rightNow = Calendar.getInstance()
        return rightNow[Calendar.YEAR] == date[Calendar.YEAR]
                && rightNow[Calendar.MONTH] == date[Calendar.MONTH]
                && rightNow[Calendar.DAY_OF_MONTH] == date[Calendar.DAY_OF_MONTH]
    }

    /**
     * Этот метод используется для генерации даты для заголовка UI элемента.
     * @param date дата
     * @return представление даты в формате ХХ/ХХ
     */
    fun generateDateForTitle(date: Calendar): String {
        //Month is a number in 0 - 11
        val month = date[Calendar.MONTH] + 1
        //Formatting month number with leading zero
        var monthString = month.toString()
        if (month < 10) {
            monthString = "0$monthString"
        }
        val day = date[Calendar.DAY_OF_MONTH]
        var dayString = day.toString()
        //Formatting day number with leading zero
        if (day < 10) {
            dayString = "0$dayString"
        }
        return "$dayString/$monthString"
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
}