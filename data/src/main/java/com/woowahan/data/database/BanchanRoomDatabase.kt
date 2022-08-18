package com.woowahan.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.woowahan.data.dao.CartDao
import com.woowahan.data.dao.RecentViewedDao
import com.woowahan.data.entity.table.*

@Database(
    entities = [
        BanchanItemTableEntity::class,
        CartTableEntity::class,
        RecentViewedTableEntity::class,
        OrderTableEntity::class,
        OrderItemTableEntity::class
    ],
    version = 1,
    exportSchema = true, // Room 의 Schema 구조를 폴더로 Export 할 수 있습니다. 데이터베이스의 버전 히스토리를 기록할 수 있다는 점에서 true로 설정하는 것이 좋습니다
)
abstract class BanchanRoomDatabase: RoomDatabase() {
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: BanchanRoomDatabase? = null

        fun getDatabase(context: Context): BanchanRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BanchanRoomDatabase::class.java,
                    "banchan_app_1.db"
                )
                    //.allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    abstract fun cartDao(): CartDao
    abstract fun recentViewedDao(): RecentViewedDao
}

