package com.qali.vision.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qali.vision.helpers.Converters
import com.qali.vision.interfaces.AppLaunchersDao
import com.qali.vision.interfaces.HiddenIconsDao
import com.qali.vision.interfaces.HomeScreenGridItemsDao
import com.qali.vision.models.AppLauncher
import com.qali.vision.models.HiddenIcon
import com.qali.vision.models.HomeScreenGridItem

@Database(
    entities = [AppLauncher::class, HomeScreenGridItem::class, HiddenIcon::class],
    version = 5
)
@TypeConverters(Converters::class)
abstract class AppsDatabase : RoomDatabase() {

    abstract fun AppLaunchersDao(): AppLaunchersDao

    abstract fun HomeScreenGridItemsDao(): HomeScreenGridItemsDao

    abstract fun HiddenIconsDao(): HiddenIconsDao

    companion object {
        private var db: AppsDatabase? = null

        fun getInstance(context: Context): AppsDatabase {
            if (db == null) {
                synchronized(AppsDatabase::class) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            context.applicationContext,
                            AppsDatabase::class.java,
                            "apps.db"
                        ).build()
                    }
                }
            }
            return db!!
        }
    }
}
