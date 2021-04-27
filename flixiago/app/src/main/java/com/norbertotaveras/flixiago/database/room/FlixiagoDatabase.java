package com.norbertotaveras.flixiago.database.room;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.norbertotaveras.flixiago.database.room.daos.MovieDao;
import com.norbertotaveras.flixiago.database.room.daos.ShowDao;
import com.norbertotaveras.flixiago.database.room.daos.WatchedEpisodeDao;
import com.norbertotaveras.flixiago.database.room.entities.MovieEntity;
import com.norbertotaveras.flixiago.database.room.entities.ShowEntity;
import com.norbertotaveras.flixiago.database.room.entities.WatchedEpisodeEntity;

@Database(entities = {
        MovieEntity.class,
        ShowEntity.class,
        WatchedEpisodeEntity.class},
        version = 2,
        exportSchema = false)
public abstract class FlixiagoDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();
    public abstract ShowDao showDao();
    public abstract WatchedEpisodeDao watchedEpisodeDao();
    public static FlixiagoDatabase instance;

    @NonNull
    public static FlixiagoDatabase getInstance(@NonNull Context context) {
        if (instance != null)
            return instance;

        context = context.getApplicationContext();

        instance = Room.databaseBuilder(
                context,
                FlixiagoDatabase.class,
                "flxiago.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // nothing to do here
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // nothing to do here
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // nothing to do here
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // nothing to do here
        }
    };
}
