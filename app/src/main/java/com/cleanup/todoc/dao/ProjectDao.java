package com.cleanup.todoc.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cleanup.todoc.model.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    @Insert
    void insert(Project project);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM project_table ORDER BY id ASC")
    LiveData<List<Project>> getAllProjects();

    @Query("SELECT * FROM project_table WHERE id = :projectId")
    LiveData<Project> getProjectWithId(long projectId);
}
