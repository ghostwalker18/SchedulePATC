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
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ghostwalker18.schedulePATC.databinding.ActivityPhotoViewBinding

/**
 * Этот класс используется для просмотра фото заметки в отдельном экране.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class PhotoViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle = intent.extras
        if (bundle == null) {
            bundle = savedInstanceState!!
        }
        photoUri = Uri.parse(bundle.getString("photo_uri"))
        binding.photo.setImageURI(photoUri)
        binding.backButton.setOnClickListener { finishAfterTransition() }
        binding.shareButton.setOnClickListener { sharePhoto() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("photo_uri", photoUri.toString())
        super.onSaveInstanceState(outState)
    }

    /**
     * Этот метод используетсяя чтобы поделиться отображаемым фото.
     */
    private fun sharePhoto() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("image/*")
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri)
        startActivity(Intent.createChooser(shareIntent, null))
    }
}