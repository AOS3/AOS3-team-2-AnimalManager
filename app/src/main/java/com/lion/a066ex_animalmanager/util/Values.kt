package com.lion.a066ex_animalmanager.util

enum class FragmentName(var number:Int, var str:String) {
    MAIN_FRAGMENT(1, "MainFragment"),
    INPUT_FRAGMENT(2, "InputFragment"),
    SHOW_FRAGMENT(3, "ShowFragment"),
    REPLACE_FRAGMENT(4, "ReplaceFragment"),
}

enum class AnimalType(var number:Int, var str:String) {
    ANIMAL_TYPE_DOG(1, "강아지"),
    ANIMAL_TYPE_CAT(2, "고양이"),
    ANIMAL_TYPE_PARROT(3, "앵무새"),
}