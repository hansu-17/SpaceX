package com.homeo.spacex.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CrewDao {

    @Insert
    void insertCrew(CrewEntity... crewEntity);

    @Delete
    void deleteCrew(CrewEntity crewEntity);

    @Query("SELECT * FROM crewMember")
    List<CrewEntity> getAllCrew();

    @Query("DELETE FROM crewMember")
    void deleteAll();

}
