package com.lion.a066ex_animalmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.R
import com.lion.a066ex_animalmanager.databinding.FragmentInputBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class InputFragment : Fragment() {

    lateinit var fragmentInputBinding: FragmentInputBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentInputBinding = FragmentInputBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        settingToolbar()
        return fragmentInputBinding.root
    }

    fun settingToolbar(){
        fragmentInputBinding.apply {
            materialToolbarInput.isTitleCentered = true
            materialToolbarInput.title = "동물 등록"
            materialToolbarInput.setNavigationIcon(R.drawable.arrow_back_24px)
            materialToolbarInput.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
            }

            materialToolbarInput.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menuComplete -> {

                        val animalType = when(typeGroup.checkedButtonId) {
                            R.id.buttonDog -> AnimalType.ANIMAL_TYPE_DOG
                            R.id.buttonCat -> AnimalType.ANIMAL_TYPE_CAT
                            else -> AnimalType.ANIMAL_TYPE_PARROT
                        }

                        val animalName = textFieldName.editText?.text.toString()
                        val animalAge = textFieldAge.editText?.text.toString().toInt()
                        val animalContent = textFieldContent.editText?.text.toString()

                        val animalViewModel = AnimalViewModel(0, animalType, animalName, animalAge, animalContent)

                        CoroutineScope(Dispatchers.Main).launch {
                            val work1 = async(Dispatchers.IO){
                                AnimalRepository.insertAnimalData(mainActivity, animalViewModel)
                            }
                            work1.join()
                            mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
                        }
                    }
                }
                true
            }
        }
    }
}