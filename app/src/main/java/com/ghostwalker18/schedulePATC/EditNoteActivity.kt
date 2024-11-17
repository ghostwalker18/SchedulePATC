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

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ghostwalker18.schedulePATC.databinding.ActivityEditNoteBinding
import java.io.File
import java.util.Calendar
import java.util.Random

/**
 * Этот класс представляет собой экран редактирования или добавления новой заметки
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private var photoUri: Uri? = null
    private var isSaved = false
    private val nameSuffixGenerator = Random()
    private lateinit var model: EditNoteModel
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()) {
        model.setPhotoID(photoUri)
        MediaScannerConnection.scanFile(
            this,
            arrayOf(photoUri!!.encodedPath),
            arrayOf("image/jpeg"),
            null
        )
    }
    private val galleryPickLauncher = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent()) { model.setPhotoID(it) }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).absoluteFile, "ScheduleNotes"
            )
            if (!directory.exists()) directory.mkdirs()
            var newFile = File(directory, makeNotePhotoName())
            while (newFile.exists())
                newFile = File(directory, makeNotePhotoName())
            photoUri = Uri.fromFile(newFile)
            val contentUri =
                FileProvider.getUriForFile(
                    this,
                    "com.ghostwalker18.schedulePATC.filesprovider", newFile
                )
            takePhotoLauncher.launch(contentUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        model = ViewModelProvider(this)[EditNoteModel::class.java]

        val bundle = intent.extras
        bundle?.run {
            if (bundle.getInt("noteID") != 0) {
                model.setNoteID(bundle.getInt("noteID"))
                actionBar?.setTitle(R.string.edit_note)
            } else {
                actionBar?.setTitle(R.string.add_note)
            }
            if (bundle.getString("group") != null)
                model.setGroup(bundle.getString("group"))
            if (bundle.getString("date") != null)
                model.setDate(DateConverters().fromString(bundle.getString("date")))
        }


        model.getDate().observe(this){ binding.date.text = DateConverters().toString(it) }

        model.getTheme().observe(this){ binding.theme.setText(it)}
        model.getThemes().observe(this){
            val adapter = ArrayAdapter(this, R.layout.autocomplete_item_layout, it)
            binding.theme.setAdapter(adapter)
        }

        model.getText().observe(this){ binding.text.setText(it) }

        model.getGroup().observe(this){ binding.group.setText(it) }
        model.getGroups().observe(this){
            val adapter = ArrayAdapter(this, R.layout.autocomplete_item_layout, it)
            binding.group.setAdapter(adapter)
        }
        binding.group.setOnItemClickListener { _, _, _, _ ->
            model.setGroup(binding.group.text.toString())
        }

        binding.imageClear.setOnClickListener {
            model.setPhotoID(null)
            binding.imageClear.visibility = View.GONE
        }

        model.getPhotoID().observe(this){
            binding.photoPreview.setImageURI(it)
            binding.imageClear.visibility = View.VISIBLE
        }

        binding.groupClear.setOnClickListener { model.setGroup("") }
        binding.themeClear.setOnClickListener { model.setTheme("") }
        binding.textClear.setOnClickListener { model.setText("") }
        binding.discard.setOnClickListener { exitActivity() }
        binding.save.setOnClickListener { saveNote() }
        binding.setDate.setOnClickListener { showDateDialog() }
        binding.takePhoto.setOnClickListener { takePhoto() }
        binding.choosePhoto.setOnClickListener { galleryPickLauncher.launch("images/*") }
    }

    override fun onDestroy() {
        super.onDestroy()
        photoUri = model.getPhotoID().value
        if (photoUri?.encodedPath != null && !isSaved) {
            val photoFile = File(photoUri!!.encodedPath!!)
            photoFile.delete()
        }
    }

    /**
     * Этот метод сохраняет заметку в репозитории и закрывает активность.
     */
    private fun saveNote() {
        model.setTheme(binding.theme.text.toString())
        model.setText(binding.text.toString())
        model.saveNote()
        isSaved = true
        finish()
    }

    /**
     * Этот метод позволяет сгенерировать имя для сделанного фото для заметки.
     * @return имя файла для фото
     */
    private fun makeNotePhotoName(): String {
        var res = ""
        res = res + DateConverters.dateFormatPhoto.format(model.getDate().value!!.time) + "_"
        res += nameSuffixGenerator.nextInt(10000)
        res += ".jpg"
        return res
    }

    /**
     * Этот метод позволяет закрыть активность и освободить ресурсы.
     */
    private fun exitActivity() {
        photoUri = model.getPhotoID().value
        photoUri?.encodedPath?.run {
            val photoFile = File(photoUri!!.encodedPath!!)
            photoFile.delete()
        }
        finish()
    }

    /**
     * Этот метод открывает окно для выбора и установки даты.
     */
    private fun showDateDialog() {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePicker")
    }

    /**
     * Этот метод открывает камеру устройств, чтобы сделать фото для заметки.
     */
    private fun takePhoto() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                val toast = Toast.makeText(this,
                    resources.getText(R.string.permission_for_photo), Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    /**
     * Этот класс отвечает за окно выбора и установки даты.
     */
    class DatePickerFragment : DialogFragment(), OnDateSetListener {
        private lateinit var model: EditNoteModel

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            model = ViewModelProvider(requireActivity())[EditNoteModel::class.java]
            // Use the current date as the default date in the picker.
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]
            // Create a new instance of DatePickerDialog and return it.
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val c = Calendar.getInstance()
            c[year, month] = day
            model.setDate(c)
        }
    }

}