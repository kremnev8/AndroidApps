package com.kremnev8.electroniccookbook.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "timerCache",indices = @Index(value = {"id"},unique = true))
public class TimerCache {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "recipeId")
    public int recipeId;

    @ColumnInfo(name = "stepId")
    public int stepId;

    @ColumnInfo(name = "stopTimeInFuture")
    public long stopTimeInFuture;

    @ColumnInfo(name = "isRunning")
    public boolean isRunning;

    @ColumnInfo(name = "isPaused")
    public boolean isPaused;
}
