package com.cleanup.todoc;

import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.viewmodel.TodocViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cleanup.todoc.viewmodel.TodocViewModel.sortedTasks;

public final class utils {

    public static ArrayList<Task> sortTasks(List<Task> tasks) {
        switch (TodocViewModel.sortMethod) {
            case ALPHABETICAL:
                Collections.sort(tasks, new Task.TaskAZComparator());
                break;
            case ALPHABETICAL_INVERTED:
                Collections.sort(tasks, new Task.TaskZAComparator());
                break;
            case RECENT_FIRST:
                Collections.sort(tasks, new Task.TaskRecentComparator());
                break;
            case OLD_FIRST:
                Collections.sort(tasks, new Task.TaskOldComparator());
                break;
        }
        sortedTasks.clear();
        sortedTasks.addAll(tasks);
        return sortedTasks;
    }

    /**
     * List of all possible sort methods for task
     */
    public static enum SortMethod {
        /**
         * Sort alphabetical by name
         */
        ALPHABETICAL,
        /**
         * Inverted sort alphabetical by name
         */
        ALPHABETICAL_INVERTED,
        /**
         * Lastly created first
         */
        RECENT_FIRST,
        /**
         * First created first
         */
        OLD_FIRST,
        /**
         * No sort
         */
        NONE
    }
}
