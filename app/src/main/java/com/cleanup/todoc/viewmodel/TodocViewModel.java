package com.cleanup.todoc.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repository.ProjectRepository;
import com.cleanup.todoc.repository.TaskRepository;
import com.cleanup.todoc.utils;

import java.util.ArrayList;
import java.util.List;

public class TodocViewModel extends ViewModel {

    // REPOSITORIES
    private TaskRepository tRepository;
    private ProjectRepository pRepository;

    // DATA AND VARS
    public static utils.SortMethod sortMethod;
    public static ArrayList<Task> sortedTasks = new ArrayList<>();

    public TodocViewModel(TaskRepository taskRepository, ProjectRepository projectRepository) {
        tRepository = taskRepository;
        pRepository = projectRepository;
    }

    //----------------
    // sorting methods
    //----------------

    public utils.SortMethod getFilter() {
        return sortMethod;
    }

    public void setFilter(utils.SortMethod sortingMethod) {
        sortMethod = sortingMethod;
    }

    //-----------
    // FOR TASKS
    //-----------

    public void insert(Task task) {
        tRepository.insert(task);
    }

    public void delete(Task task) {
        tRepository.delete(task);
    }

    public void deleteAllTasks() {
        tRepository.deleteAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return tRepository.getAllTasks();
    }

    //-----------
    // FOR PROJECTS
    //-----------

    public LiveData<Project> getProjectWithId(long projectId) {
        return pRepository.getProjectWithId(projectId);
    }

    public LiveData<List<Project>> getAllProjects() {
        return pRepository.getAllProjects();
    }
}
