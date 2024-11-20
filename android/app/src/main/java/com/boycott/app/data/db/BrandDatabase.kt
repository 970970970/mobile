package com.boycott.app.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val country: String,
    val reason: String,
    val alternatives: String,
    val createdAt: String,
    val updatedAt: String
)

@Dao
interface BrandDao {
    @Query("SELECT * FROM brands")
    suspend fun getAllBrands(): List<BrandEntity>

    @Query("SELECT * FROM brands WHERE id = :id")
    suspend fun getBrandById(id: String): BrandEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity)
}

@Database(
    entities = [BrandEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BrandDatabase : RoomDatabase() {
    abstract fun brandDao(): BrandDao

    companion object {
        const val DATABASE_NAME = "brand_database"
    }
} 