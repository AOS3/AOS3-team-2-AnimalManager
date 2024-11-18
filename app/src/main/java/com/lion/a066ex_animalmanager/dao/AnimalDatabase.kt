package com.lion.a066ex_animalmanager.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lion.a066ex_animalmanager.vo.AnimalVO

@Database(entities = [AnimalVO::class], version = 3, exportSchema = true)
abstract class AnimalDatabase : RoomDatabase() {
    abstract fun animalDAO(): AnimalDAO

    companion object{
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE AnimalTable ADD COLUMN animalContent TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // animalImage 컬럼을 추가하는 SQL 명령어
                database.execSQL("ALTER TABLE AnimalTable ADD COLUMN animalImage TEXT NOT NULL DEFAULT ''")
            }
        }

        var animalDatabase:AnimalDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : AnimalDatabase?{
            synchronized(AnimalDatabase::class){
                animalDatabase = Room.databaseBuilder(
                    context.applicationContext, AnimalDatabase::class.java,
                    "Animal.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
            }
            return animalDatabase
        }

        fun destroyInstance(){
            animalDatabase = null
        }
    }
}