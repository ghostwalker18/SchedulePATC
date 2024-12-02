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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.ghostwalker18.schedulePATC.databinding.ActivityNotesBinding
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap

/**
 * Этот классс представляет собой экран приложения, на котором отображаются заметки к занятиям.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class NotesActivity : AppCompatActivity() {
    private var selectedNotes: MutableMap<Int, Note> = ConcurrentHashMap()
    private val repository = ScheduleApp.getInstance().getNotesRepository()
    private val listener = object : NoteAdapter.OnNoteClickListener {
        override fun onNoteSelected(note: Note, position: Int) {
            selectedNotes[position] = note
            binding.selectionPanel.visibility = View.VISIBLE
            binding.searchBar.visibility = View.GONE
            binding.selectedCount.text = selectedNotes.size.toString()
            decideMenuOptions()
        }

        override fun onNoteUnselected(note: Note, position: Int) {
            selectedNotes.remove(position, note)
            if (selectedNotes.isEmpty()) {
                binding.selectionPanel.visibility = View.GONE
                binding.searchBar.visibility = View.VISIBLE
            }
            binding.selectedCount.text = selectedNotes.size.toString()
            decideMenuOptions()
        }
    }
    private var group: String? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var isEditAvailable = false
    private var isDeleteAvailable = false
    private var isShareAvailable = false
    private lateinit var binding : ActivityNotesBinding
    private lateinit var model: NotesModel
    private lateinit var filter: NotesFilterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        model = ViewModelProvider(this)[NotesModel::class.java]
        val bundle = intent.extras
        if (bundle != null) {
            group = bundle.getString("group")
            startDate = DateConverters().fromString(bundle.getString("date"))
            endDate = startDate
            if (savedInstanceState == null) {
                model.setGroup(group)
                model.setStartDate(startDate)
                model.setEndDate(endDate)
            }
        }
        model.getNotes().observe(this) {
            notes -> binding.notes.adapter = NoteAdapter(notes!!, listener)
        }
        filter = NotesFilterFragment()
        binding.filter.setOnClickListener{ openFilterFragment() }
        binding.editNote.setOnClickListener{ openAddNote() }
        binding.search.apply {
            addTextChangedListener {
                doAfterTextChanged {
                    var keyword: String? = it.toString().trim { it <= ' ' }
                    if (keyword == "") keyword = null
                    model.setKeyword(keyword)
                }
            }
        }

        binding.selectionCancel.setOnClickListener {
            for (position in selectedNotes.keys) {
                val item = binding.notes.findViewHolderForAdapterPosition(position) as NoteAdapter.ViewHolder
                item?.isSelected = false
                listener.onNoteUnselected(selectedNotes[position]!!, position)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notes_activity, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_edit).setVisible(isEditAvailable)
        menu.findItem(R.id.action_delete).setVisible(isDeleteAvailable)
        menu.findItem(R.id.action_share).setVisible(isShareAvailable)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_share -> shareNotes()
            R.id.action_delete -> deleteNotes()
            R.id.action_edit -> openEditNote()
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Этот метод открывает активность для редактирования или добавления заметки.
     */
    private fun openAddNote() {
        val intent = Intent(this, EditNoteActivity::class.java)
        val bundle = Bundle()
        bundle.putString("group", group)
        if (startDate != null) {
            bundle.putString("date", DateConverters().toString(startDate))
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

    /**
     * Этот метод окрывает панель фильтра.
     */
    private fun openFilterFragment() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            .replace(R.id.notes_container, filter)
            .commit()
    }

    /**
     * Этот метод позволяет поделиться выбранными заметками.
     * @return
     */
    private fun shareNotes(): Boolean {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        var notes = ""
        for (note in selectedNotes.values) {
            notes += note.toString() + "\n"
        }
        intent.putExtra(Intent.EXTRA_TEXT, notes)
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
        selectedNotes = java.util.HashMap()
        decideMenuOptions()
        return true
    }

    /**
     * Этот метод позволяет удалить выбранные заметки.
     * @return
     */
    private fun deleteNotes(): Boolean {
        repository.deleteNotes(selectedNotes.values)
        selectedNotes = java.util.HashMap()
        decideMenuOptions()
        return true
    }

    /**
     * Этот метод позволяет, если выбранна одна заметка,
     * открыть экран приложения для ее редактирования.
     * @return
     */
    private fun openEditNote(): Boolean {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("noteID", selectedNotes.entries.iterator().next().value.id)
        startActivity(intent)
        selectedNotes = java.util.HashMap()
        decideMenuOptions()
        return true
    }

    /**
     * Этот метод позволяет определить, какие опции должны быть в меню.
     */
    private fun decideMenuOptions() {
        isEditAvailable = selectedNotes.size == 1
        isShareAvailable = selectedNotes.isNotEmpty()
        isDeleteAvailable = selectedNotes.isNotEmpty()
        invalidateMenu()
    }
}