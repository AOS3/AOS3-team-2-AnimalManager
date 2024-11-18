package com.lion.a066ex_animalmanager.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.R
import com.lion.a066ex_animalmanager.databinding.FragmentShowBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ShowFragment : Fragment() {

    lateinit var fragmentShowBinding: FragmentShowBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentShowBinding = FragmentShowBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        settingToolbar()
        settingText()
        return fragmentShowBinding.root
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentShowBinding.apply {
            // 타이틀 중앙정렬
            materialToolbarShow.isTitleCentered = true
            // 타이틀
            materialToolbarShow.title = "동물 정보 보기"
            // 네비게이션
            materialToolbarShow.setNavigationIcon(R.drawable.arrow_back_24px)
            materialToolbarShow.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
            // 메뉴
            materialToolbarShow.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menuReplace -> {
                        val dataBundle = Bundle()
                        dataBundle.putInt("animalIdx", arguments?.getInt("animalIdx")!!)
                        mainActivity.replaceFragment(FragmentName.REPLACE_FRAGMENT, true, dataBundle
                        )
                    }

                    R.id.menuDelete -> {
                        deleteDone()
                    }
                }
                true
            }
        }
    }

    // 요소 설정
    fun settingText() {
        fragmentShowBinding.textViewShowType.text = ""
        fragmentShowBinding.textViewShowName.text = ""
        fragmentShowBinding.textViewShowAge.text = ""
        fragmentShowBinding.textViewShowContent.text = ""
        fragmentShowBinding.imageViewShowAnimal.setImageURI(null)

        // 동물 번호를 가져온다.
        val animalIdx = arguments?.getInt("animalIdx")
        // 동물 데이터를 가져온다.
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO){
                AnimalRepository.selectAnimalDataByIdx(mainActivity, animalIdx!!)
            }
            val animalViewModel = work1.await()

            fragmentShowBinding.textViewShowType.text = animalViewModel.animalType.str
            fragmentShowBinding.textViewShowName.text = animalViewModel.animalName
            fragmentShowBinding.textViewShowAge.text = "${animalViewModel.animalAge}"
            fragmentShowBinding.textViewShowContent.text = animalViewModel.animalContent
            // animalImage가 null이 아닌 경우, uriString에 값이 들어온다.
            animalViewModel.animalImage?.let { uriString ->
                // URI 문자열을 Uri 객체로 변환한다.
                val uri = android.net.Uri.parse(uriString)
                // 이미지 뷰에 URI를 사용하여 이미지 설정한다.
                fragmentShowBinding.imageViewShowAnimal.setImageURI(uri)
            }
        }
    }

    // 동물정보를 삭제하는 메서드
    fun deleteDone(){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("동물 정보 삭제")
        materialAlertDialogBuilder.setMessage("삭제를 하실경우 정보 복구가 불가능합니다\n" +
                "삭제 하시겠습니까?")
        materialAlertDialogBuilder.setNeutralButton("취소", null)
        materialAlertDialogBuilder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    val animalIdx = arguments?.getInt("animalIdx")
                    AnimalRepository.deleteAnimalDataByIdx(mainActivity, animalIdx!!)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
        }
        materialAlertDialogBuilder.show()
    }
}