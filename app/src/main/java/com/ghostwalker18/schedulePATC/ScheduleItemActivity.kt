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

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.ghostwalker18.schedulePATC.databinding.ActivityScheduleItemBinding

/**
 * Этот класс представляет собой экран приложения для отображения расписания на день.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class ScheduleItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleItemBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityScheduleItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}