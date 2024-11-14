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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import java.util.Calendar

/**
 * Этот класс используется для описания единичной сущности расписания - урока.
 * Используется в ORM.
 * Содержит поля для даты, порядкового номера, номера(названия) кабинета,
 * времени проведения, группы, преподавателя, предмета.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Entity(tableName = "tblSchedule",
    primaryKeys = ["lessonDate", "lessonNumber", "groupName", "subjectName"])
data class Lesson(
    @TypeConverters(DateConverters::class)
    @ColumnInfo(name = "lessonDate") val date : Calendar,
    @ColumnInfo(name = "lessonNumber") val lessonNumber : String,
    @ColumnInfo(name="roomNumber") val roomNumber : String?,
    @ColumnInfo(name = "lessonTimes") val times : String?,
    @ColumnInfo(name = "groupName") val groupName : String,
    @ColumnInfo(name = "subjectName") val subject : String,
    @ColumnInfo(name = "teacherName") val teacher: String?
){
    override fun toString(): String {
        val resources = ScheduleApp.getInstance().resources
        var res = ""
        res = res + resources.getString(R.string.number) + ": " + lessonNumber + "\n"
        res = res + resources.getString(R.string.subject) + ": " + subject + "\n"
        if (teacher != "") res = res + resources.getString(R.string.teacher) + ": " + teacher + "\n"
        if (roomNumber != "") res =
            res + resources.getString(R.string.room) + ": " + roomNumber + "\n"
        return res
    }
}