package com.lion.a066ex_animalmanager.fragment

import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.R
import com.lion.a066ex_animalmanager.databinding.FragmentReplaceBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ReplaceFragment : Fragment() {

    lateinit var fragmentReplaceBinding: FragmentReplaceBinding
    lateinit var mainActivity: MainActivity
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReplaceBinding = FragmentReplaceBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        createAlbumLauncher()
        settingToolbar()
        settingStartText()
        settingReplaceImage()

        return fragmentReplaceBinding.root
    }

    // 이미지 변경
    fun settingReplaceImage() {
        fragmentReplaceBinding.buttonImageReplace.setOnClickListener {
            val albumIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    // 이미지 타입을 설정한다.
                    type = "image/*"
                    // 선택할 파일의 타입을 지정(안드로이드 OS가 사전 작업을 할 수 있도록)
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                }
            // 액티비티 실행
            albumLauncher.launch(albumIntent)
        }
    }

    // 런처를 생성하는 메서드
    fun createAlbumLauncher() {
        albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                it.data?.data?.let { uri ->
                    // 선택된 이미지 Uri를 저장
                    selectedImageUri = uri
                    // android 10 버전 이상이라면
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)
                        val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)
                        fragmentReplaceBinding.imageViewReplace.setImageBitmap(bitmap)
                    } else {
                        // ContentProvider를 통해 사진 데이터를 가져온다.
                        val cursor = mainActivity.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                // 이미지의 경로를 가져온다.
                                val idx = it.getColumnIndex(MediaStore.Images.Media.DATA)
                                val path = it.getString(idx)
                                // 이미지 경로를 Uri로 변환
                                selectedImageUri = Uri.parse(path)
                                // 이미지를 생성한다.
                                val bitmap = BitmapFactory.decodeFile(path)
                                fragmentReplaceBinding.imageViewReplace.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    // 외부 저장소의 이미지를 내부 저장소로 복사하는 메서드
    fun copyImageToInternalStorage(uri: Uri, context: Context): Uri? {
        try {
            // 외부 저장소의 파일에 접근
            val contentResolver: ContentResolver = context.contentResolver
            // 주어진 URI에 해당하는 이미지 파일을 입력 스트림으로 열고 URI를 통해 파일을 열 수 없으면 null을 반환합니다.
            val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            // 내부 저장소에 이미지를 저장할 파일을 생성합니다.
            // 파일명은 "copied_image_" 뒤에 현재 시간을 붙여 고유하게 생성합니다.
            val file = File(context.filesDir, "copied_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            // 비트맵을 JPEG 형식으로 압축하여 출력 스트림에 저장
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            // 저장된 파일의 URI를 반환합니다. 이를 통해 내부 저장소에 저장된 파일을 참조할 수 있습니다.
            return Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // 입력 요소 초기설정
    fun settingStartText() {
        fragmentReplaceBinding.apply {
            // 동물 번호를 가져온다.
            val animalIdx = arguments?.getInt("animalIdx")

            // 동물 데이터를 가져온다.
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    AnimalRepository.selectAnimalDataByIdx(mainActivity, animalIdx!!)
                }
                val animalViewModel = work1.await()

                // 기존 동물 데이터 세팅
                when (animalViewModel.animalType) {
                    AnimalType.ANIMAL_TYPE_DOG -> typeGroupReplace.check(R.id.buttonDogReplace)
                    AnimalType.ANIMAL_TYPE_CAT -> typeGroupReplace.check(R.id.buttonCatReplace)
                    else -> typeGroupReplace.check(R.id.buttonParrotReplace)
                }

                textFieldNameReplace.editText?.setText(animalViewModel.animalName)
                textFieldAgeReplace.editText?.setText(animalViewModel.animalAge.toString())
                textFieldContentReplace.editText?.setText(animalViewModel.animalContent)

                // 내부 저장소 URI 설정
                selectedImageUri = animalViewModel.animalImage.toUri()
                fragmentReplaceBinding.imageViewReplace.setImageURI(Uri.parse(selectedImageUri.toString()))
            }
        }
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentReplaceBinding.apply {
            // 타이틀 중앙정렬
            materialToolbarReplace.isTitleCentered = true
            // 타이틀
            materialToolbarReplace.title = "동물 정보 수정"
            // 네비게이션
            materialToolbarReplace.setNavigationIcon(R.drawable.arrow_back_24px)
            materialToolbarReplace.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.REPLACE_FRAGMENT)
            }

            // 메뉴
            materialToolbarReplace.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menuComplete -> {
                        replaceDone()
                    }
                }
                true
            }
        }
    }

    // 수정 처리 메서드
    fun replaceDone() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("동물 정보 수정")
        materialAlertDialogBuilder.setMessage("이전 정보로 복원 할 수 없습니다.")
        materialAlertDialogBuilder.setPositiveButton("수정") { dialogInterface: DialogInterface, i: Int ->
            // 수정할 데이터
            val animalIdx = arguments?.getInt("animalIdx")!!
            val animalType = when (fragmentReplaceBinding.typeGroupReplace.checkedButtonId) {
                R.id.buttonDogReplace -> AnimalType.ANIMAL_TYPE_DOG
                R.id.buttonCatReplace -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT
            }
            val animalName = fragmentReplaceBinding.textFieldNameReplace.editText?.text.toString()
            val animalAge = fragmentReplaceBinding.textFieldAgeReplace.editText?.text.toString().toInt()
            val animalContent = fragmentReplaceBinding.textFieldContentReplace.editText?.text.toString()

            // 내부 저장소에 이미지 복사 및 URI 설정
            val animalImageUri = selectedImageUri?.let { uri ->
                copyImageToInternalStorage(uri, mainActivity)?.toString() ?: ""
                // 선택된 이미지가 없으면 빈 문자열 처리
            } ?: ""

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName, animalAge, animalContent, animalImageUri)

            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    AnimalRepository.replaceAnimalData(mainActivity, animalViewModel)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.REPLACE_FRAGMENT)
            }
        }
        materialAlertDialogBuilder.setNeutralButton("취소", null)
        materialAlertDialogBuilder.show()
    }
}