package com.homeo.spacex.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CrewEntity.class}, version = 2)
public abstract class CrewDatabase extends RoomDatabase {
    public abstract CrewDao getCrewDao();

    private static CrewDatabase instance;

    public static CrewDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CrewDatabase.class, "crew-db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
