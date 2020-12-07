package com.cleanup.todoc.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.dao.ProjectDao;
import com.cleanup.todoc.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.Date;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, Project.class}, version = 1)
public abstract class TodocDatabase extends RoomDatabase {

    private static TodocDatabase instance;

    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();

    public static synchronized TodocDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TodocDatabase.class, "todoc_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                getInstance(context.getApplicationContext())
                                        .projectDao().insert(new Project(1L, "Projet Tartampion", 0xFFEADAD1));
                                getInstance(context.getApplicationContext())
                                        .projectDao().insert(new Project(2L, "Projet Lucidia", 0xFFB4CDBA));
                                getInstance(context.getApplicationContext())
                                        .projectDao().insert(new Project(3L, "Projet Circus", 0xFFA3CED2));

                                getInstance(context.getApplicationContext())
                                        .taskDao().insert(new Task(1L, "Tache N°1", new Date().getTime()));
                                getInstance(context.getApplicationContext())
                                        .taskDao().insert(new Task(2L, "Tache N°2", new Date().getTime()));
                                getInstance(context.getApplicationContext())
                                        .taskDao().insert(new Task(3L, "Tache N°3", new Date().getTime()));
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }
}
