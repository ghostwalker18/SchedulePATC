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

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ghostwalker18.schedulePATC.databinding.ActivityMainBinding

/**
 * Этот класс представляет собой основной экран приложения.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var daysFragment: DaysFragment

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_share -> return shareSchedule()
            R.id.action_download -> return downloadScheduleFile()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Этот метод используется для того, чтобы поделиться расписанием из открытых элементов
     * в доступных приложениях.
     */
    private fun shareSchedule(): Boolean {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        var schedule = ""
        for (day in daysFragment.getDays()) {
            if (day.isOpened) {
                schedule += day.getSchedule()
            }
        }
        if (schedule == "") {
            Toast.makeText(this, R.string.nothing_to_share, Toast.LENGTH_SHORT).show()
            return true
        }
        intent.putExtra(Intent.EXTRA_TEXT, schedule)
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
        return true
    }

    /**
     * Этот метод используется для скачивания файлов расписания и помещения их в
     * папку загрузок.
     */
    private fun downloadScheduleFile(): Boolean {
        Thread {
            val linksForFirstCorpusSchedule =
                ScheduleApp.getInstance()
                    .getScheduleRepository()
                    .getLinksForFirstCorpusSchedule()
            val linksForSecondCorpusSchedule =
                ScheduleApp.getInstance()
                    .getScheduleRepository()
                    .getLinksForSecondCorpusSchedule()
            val links: MutableList<String> = ArrayList()
            links.addAll(linksForFirstCorpusSchedule)
            links.addAll(linksForSecondCorpusSchedule)
            val downloadManager = application.getSystemService(DownloadManager::class.java)
            for (link in links) {
                val request =
                    DownloadManager.Request(Uri.parse(link))
                        .setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setTitle(getString(R.string.schedule))
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            ScheduleRepository.getNameFromLink(link)
                        )
                downloadManager.enqueue(request)
            }
        }.start()
        return true
    }
}