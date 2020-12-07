package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cleanup.todoc.R;
import com.cleanup.todoc.databinding.ActivityMainBinding;
import com.cleanup.todoc.databinding.ItemTaskBinding;
import com.cleanup.todoc.injection.Injection;
import com.cleanup.todoc.injection.TaskViewModelFactory;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.utils;
import com.cleanup.todoc.viewmodel.TodocViewModel;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY
 * @modified by Tommy MARCHAL
 */
public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {

    private TodocViewModel todocViewModel;

    /**
     * List of all projects available in the application
     */
    public final ArrayList<Project> allProjects = new ArrayList<>();

    /**
     * List of all current tasks of the application
     */
    @NonNull
    private final ArrayList<Task> mTasks = new ArrayList<>();

    /**
     * The adapter which handles the list of tasks
     */
    private final TasksAdapter adapter = new TasksAdapter(this, allProjects, mTasks);

    /**
     * Dialog to create a new task
     */
    @Nullable
    public AlertDialog dialog = null;

    /**
     * EditText that allows user to set the name of a task
     */
    @Nullable
    private EditText dialogEditText = null;

    /**
     * Spinner that allows the user to associate a project to a task
     */
    @Nullable
    private Spinner dialogSpinner = null;

    /**
     * viewBinding to remove all the findViewByIds
     * Initialised in the onCreate methode
     */
    ActivityMainBinding binding;
    ItemTaskBinding itemBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        itemBinding = ItemTaskBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.listTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.listTasks.setAdapter(adapter);

        this.configureViewModel();

        // Setting up the projects list
        todocViewModel.getAllProjects().observe(this, projects -> {
            allProjects.clear();
            allProjects.addAll(projects);
        });

        // Setting up the filter system
        if (todocViewModel.getFilter() == null) {
            todocViewModel.setFilter(utils.SortMethod.NONE);
        }

        // Setting up the tasks list
        todocViewModel.getAllTasks().observe(this, tasks -> {
            mTasks.clear();
            if (!todocViewModel.getFilter().equals(utils.SortMethod.NONE)) {
                mTasks.addAll(utils.sortTasks(tasks));
            } else {
                mTasks.addAll(tasks);
            }
            adapter.updateTasks(mTasks);
            updateTasks();
        });

        binding.fabAddTask.setOnClickListener(view -> showAddTaskDialog());

    }

    /**
     * Setting up the ViewModel
     */
    private void configureViewModel() {
        this.todocViewModel = new ViewModelProvider(this, new TaskViewModelFactory(
                Injection.provideTaskRep(this),
                Injection.provideProjectRep(this)))
                .get(TodocViewModel.class);
    }

    /**
     * Menu Inflater
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    /**
     * Sorting System
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter_alphabetical) {
            todocViewModel.setFilter(utils.SortMethod.ALPHABETICAL);
        } else if (id == R.id.filter_alphabetical_inverted) {
            todocViewModel.setFilter(utils.SortMethod.ALPHABETICAL_INVERTED);
        } else if (id == R.id.filter_oldest_first) {
            todocViewModel.setFilter(utils.SortMethod.OLD_FIRST);
        } else if (id == R.id.filter_recent_first) {
            todocViewModel.setFilter(utils.SortMethod.RECENT_FIRST);
        }
        utils.sortTasks(mTasks);
        updateTasks();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Listener to delete a task
     *
     * @param task the task that needs to be deleted
     */
    @Override
    public void onDeleteTask(Task task) {
        todocViewModel.delete(task);
    }

    /**
     * Called when the user clicks on the positive button of the Create Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {


                Task task = new Task(
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );

                addTask(task);

                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else {
                dialogInterface.dismiss();
            }
        }
        // If dialog is already closed
        else {
            dialogInterface.dismiss();
        }
    }

    /**
     * Shows the Dialog for adding a Task
     */
    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    /**
     * Adds the given task to the list of created tasks.
     *
     * @param task the task to be added to the list
     */
    private void addTask(@NonNull Task task) {
        todocViewModel.insert(task);
    }

    /**
     * Updates the list of tasks in the UI
     */
    private void updateTasks() {
        if (adapter.getItemCount() == 0) {
            binding.lblNoTask.setVisibility(View.VISIBLE);
            binding.listTasks.setVisibility(View.GONE);
        } else {
            binding.lblNoTask.setVisibility(View.GONE);
            binding.listTasks.setVisibility(View.VISIBLE);
            adapter.updateTasks(mTasks);
        }
    }

    /**
     * Returns the dialog allowing the user to create a new task.
     *
     * @return the dialog allowing the user to create a new task
     */
    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(dialogInterface -> {
            dialogEditText = null;
            dialogSpinner = null;
            dialog = null;
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> onPositiveButtonClick(dialog));
        });

        return dialog;
    }

    /**
     * Sets the data of the Spinner with projects to associate to a new task
     */
    private void populateDialogSpinner() {
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }
}
