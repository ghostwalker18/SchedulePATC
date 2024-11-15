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

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File

/**
 * Этот интерфейс описывет методы для преобразования pdf-файлов расписания ПАТТ в коллекции
 * элементов Lesson.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
interface IConverter {
    /**
     * Этот метод используется для обработки файла расписания первого корпуса на Первомайском пр.
     *
     * @param pdfFile эксель файл расписания для первого корпуса
     * @return лист объектов класса Lesson
     */
    fun convertFirstCorpus(pdfFile: XWPFDocument) : List<Lesson>

    /**
     * Этот метод используется для обработки файла расписания второго корпуса на ул.Советская.
     *
     * @param pdfFile эксель файл расписания для второго корпуса
     * @return лист объектов класса Lesson
     */
    fun convertSecondCorpus(pdfFile: XWPFDocument) : List<Lesson>

    /**
     * Этот метод используется для обработки файла расписания третьего корпуса на ул.Ленинградская.
     *
     * @param pdf эксель файл расписания для второго корпуса
     * @return лист объектов класса Lesson
     */
    fun convertThirdCorpus(pdfFile: XWPFDocument) : List<Lesson>
}