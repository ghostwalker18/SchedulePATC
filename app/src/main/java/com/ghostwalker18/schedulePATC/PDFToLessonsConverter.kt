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

import org.apache.poi.xwpf.usermodel.XWPFDocument

/**
 * Этот класс содержит в себе методы для работы с файлами расписания ПАТТ.
 *
 * @author  Ипатов Никита
 */
class PDFToLessonsConverter : IConverter {
    override fun convertFirstCorpus(pdfFile: XWPFDocument): List<Lesson> {
        TODO("Not yet implemented")
    }

    override fun convertSecondCorpus(pdfFile: XWPFDocument): List<Lesson> {
        TODO("Not yet implemented")
    }

    override fun convertThirdCorpus(pdfFile: XWPFDocument): List<Lesson> {
        TODO("Not yet implemented")
    }
}