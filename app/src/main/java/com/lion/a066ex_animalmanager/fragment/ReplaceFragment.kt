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
import com.lion.a066ex_animalmanager.databinding.FragmentReplaceBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ReplaceFragment : Fragment() {

    lateinit var fragmentReplaceBinding: FragmentReplaceBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentReplaceBinding = FragmentReplaceBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        settingToolbar()
        setttingStartText()
        return fragmentReplaceBinding.root
    }

    fun settingToolbar(){
        fragmentReplaceBinding.apply {
            materialToolbarReplace.isTitleCentered = true
            materialToolbarReplace.title = "동물 정보 수정"
            materialToolbarReplace.setNavigationIcon(R.drawable.arrow_back_24px)
            materialToolbarReplace.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.REPLACE_FRAGMENT)
            }
            materialToolbarReplace.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menuComplete -> {
                        replaceDone()
                    }
                }
                true
            }
        }
    }

    fun setttingStartText(){
        fragmentReplaceBinding.apply {
            val animalIdx = arguments?.getInt("animalIdx")

            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    AnimalRepository.selectAnimalDataByIdx(mainActivity, animalIdx!!)
                }
                val animalViewModel = work1.await()

                when(animalViewModel.animalType){
                    AnimalType.ANIMAL_TYPE_DOG -> {
                        typeGroupReplace.check(R.id.buttonDogReplace)
                    }
                    AnimalType.ANIMAL_TYPE_CAT -> {
                        typeGroupReplace.check(R.id.buttonCatReplace)
                    }
                    else -> {
                        typeGroupReplace.check(R.id.buttonParrotReplace)
                    }
                }

                textFieldNameReplace.editText?.setText(animalViewModel.animalName)
                textFieldAgeReplace.editText?.setText(animalViewModel.animalAge.toString())
                textFieldContentReplace.editText?.setText(animalViewModel.animalContent)
            }
        }
    }

    fun replaceDone(){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("동물 정보 수정")
        materialAlertDialogBuilder.setMessage("이전 정보로 복원 할 수 없습니다.")
        materialAlertDialogBuilder.setPositiveButton("수정"){ dialogInterface: DialogInterface, i: Int ->
            val animalIdx = arguments?.getInt("animalIdx")!!
            val animalType = when(fragmentReplaceBinding.typeGroupReplace.checkedButtonId){
                R.id.buttonDogReplace -> AnimalType.ANIMAL_TYPE_DOG
                R.id.buttonCatReplace -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT
            }
            val animalName = fragmentReplaceBinding.textFieldNameReplace.editText?.text.toString()
            val animalAge = fragmentReplaceBinding.textFieldAgeReplace.editText?.text.toString().toInt()
            val animalContent = fragmentReplaceBinding.textFieldContentReplace.editText?.text.toString()

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName, animalAge, animalContent)

            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
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