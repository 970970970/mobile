package com.boycott.app.di

import android.content.Context
import androidx.room.Room
import com.boycott.app.data.db.BrandDatabase
import com.boycott.app.data.db.BrandDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BrandDatabase {
        return Room.databaseBuilder(
            context,
            BrandDatabase::class.java,
            BrandDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideBrandDao(database: BrandDatabase): BrandDao {
        return database.brandDao()
    }
} 