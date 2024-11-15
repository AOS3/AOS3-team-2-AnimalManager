package com.lion.a066ex_animalmanager.viewmodel

import com.lion.a066ex_animalmanager.util.AnimalType

data class AnimalViewModel (
    var animalIdx: Int,
    var animalType: AnimalType,
    var animalName: String,
    var animalAge: Int,
    var animalContent: String,
)