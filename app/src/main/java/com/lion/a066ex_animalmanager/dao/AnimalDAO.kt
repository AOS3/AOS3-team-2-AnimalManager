package com.lion.a066ex_animalmanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lion.a066ex_animalmanager.vo.AnimalVO

@Dao
interface AnimalDAO {
    @Insert
    fun insertAnimalData(AnimalVO: AnimalVO)

    @Query("""
        select * from AnimalTable
        order by animalIdx desc""")
    fun selectAnimalDataAll() : List<AnimalVO>

    @Query("""
        select * from AnimalTable
        where animalIdx = :animalIdx
    """)
    fun selectAnimalDataByAnimalIdx(animalIdx:Int):AnimalVO

    @Update
    fun replaceAnimalData(AnimalVO: AnimalVO)

    @Delete
    fun deleteAnimalData(AnimalVO: AnimalVO)
}