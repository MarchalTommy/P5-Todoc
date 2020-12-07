package com.cleanup.todoc.injection;

import android.content.Context;

import com.cleanup.todoc.database.TodocDatabase;
import com.cleanup.todoc.repository.ProjectRepository;
import com.cleanup.todoc.repository.TaskRepository;

public class Injection {

    public static TaskRepository provideTaskRep(Context context) {
        TodocDatabase database = TodocDatabase.getInstance(context);
        return new TaskRepository(database.taskDao());
    }

    public static ProjectRepository provideProjectRep(Context context) {
        TodocDatabase database = TodocDatabase.getInstance(context);
        return new ProjectRepository(database.projectDao());
    }

    public static TaskViewModelFactory provideViewModelFactory(Context context) {
        TaskRepository taskRepository = provideTaskRep(context);
        ProjectRepository projectRepository = provideProjectRep(context);
        return new TaskViewModelFactory(taskRepository, projectRepository);
    }
}
