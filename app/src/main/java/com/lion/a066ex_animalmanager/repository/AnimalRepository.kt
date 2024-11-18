package com.lion.a066ex_animalmanager.repository

import android.content.Context
import android.util.Log
import com.lion.a066ex_animalmanager.dao.AnimalDatabase
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import com.lion.a066ex_animalmanager.vo.AnimalVO

class AnimalRepository {

    companion object{
        fun insertAnimalData(context: Context, animalViewModel: AnimalViewModel){
            val animalDatabase = AnimalDatabase.getInstance(context)
            val animalType = animalViewModel.animalType.number
            val animalName = animalViewModel.animalName
            val animalAge = animalViewModel.animalAge
            val animalContent = animalViewModel.animalContent
            val animalImage = animalViewModel.animalImage

            val animalVO = AnimalVO(animalType = animalType, animalName = animalName, animalAge = animalAge, animalContent = animalContent, animalImage = animalImage)

            Log.d("AnimalRepository", "Animal Image URI: $animalImage")

            animalDatabase?.animalDAO()?.insertAnimalData(animalVO)
        }

        fun selectAnimalDataAll(context: Context) : MutableList<AnimalViewModel>{
            val animalDatabase = AnimalDatabase.getInstance(context)
            val animalVoList = animalDatabase?.animalDAO()?.selectAnimalDataAll()
            val animalViewModelList = mutableListOf<AnimalViewModel>()

            animalVoList?.forEach {
                val animalType = when(it.animalType){
                    AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                    AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                    else -> AnimalType.ANIMAL_TYPE_PARROT
                }

                val animalName = it.animalName
                val animalAge = it.animalAge
                val animalIdx = it.animalIdx
                val animalContent = it.animalContent
                val animalImage = it.animalImage

                val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName, animalAge, animalContent, animalImage)

                animalViewModelList.add(animalViewModel)
            }
            return animalViewModelList
        }

        fun selectAnimalDataByIdx(context: Context, animalIdx: Int): AnimalViewModel{
            val animalDatabase = AnimalDatabase.getInstance(context)
            val animalVo = animalDatabase?.animalDAO()?.selectAnimalDataByAnimalIdx(animalIdx)

            val animalType = when(animalVo?.animalType){
                AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT
            }

            val animalName = animalVo?.animalName
            val animalAge = animalVo?.animalAge
            val animalContent = animalVo?.animalContent
            val animalImage = animalVo?.animalImage

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName!!, animalAge!!, animalContent!!, animalImage!!)

            return animalViewModel
        }

        fun deleteAnimalDataByIdx(context: Context, animalIdx: Int){
            val animalDatabase = AnimalDatabase.getInstance(context)
            val animalVo = AnimalVO(animalIdx = animalIdx)

            animalDatabase?.animalDAO()?.deleteAnimalData(animalVo)
        }

        fun replaceAnimalData(context: Context, animalViewModel: AnimalViewModel){
            val animalDatabase = AnimalDatabase.getInstance(context)

            val animalIdx = animalViewModel.animalIdx
            val animalType = animalViewModel.animalType.number
            val animalName = animalViewModel.animalName
            val animalAge = animalViewModel.animalAge
            val animalContent = animalViewModel.animalContent
            val animalImage = animalViewModel.animalImage

            val animalVO = AnimalVO(animalIdx, animalType, animalName, animalAge, animalContent, animalImage)

            animalDatabase?.animalDAO()?.replaceAnimalData(animalVO)
        }
    }
}