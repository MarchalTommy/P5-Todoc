package com.cleanup.todoc.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.todoc.repository.ProjectRepository;
import com.cleanup.todoc.repository.TaskRepository;
import com.cleanup.todoc.viewmodel.TodocViewModel;

public class TaskViewModelFactory implements ViewModelProvider.Factory {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskViewModelFactory(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TodocViewModel.class)) {
            return (T) new TodocViewModel(taskRepository, projectRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
