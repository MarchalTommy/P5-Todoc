package com.cleanup.todoc.database;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cleanup.todoc.dao.ProjectDao;
import com.cleanup.todoc.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TodocDatabaseInstrumentedTest {
    // DATA
    private TaskDao taskDao;
    private ProjectDao projectDao;
    final List<Task> taskList = new ArrayList<>();
    final List<Project> projectList = new ArrayList<>();
    TodocDatabase database;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Starting the database and initialising values
     */
    @Before
    public void init() throws InterruptedException {
        // INITIALISING DATABASE
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(), TodocDatabase.class).build();
        taskDao = database.taskDao();
        projectDao = database.projectDao();

        // MAKING SURE TASK LIST IS EMPTY FOR TESTS
        taskDao.deleteAllTasks();

        // MAKING SURE PROJECT LIST IS NOT EMPTY
        if (LiveDataTestUtil.getValue(projectDao.getAllProjects()).isEmpty()) {
            projectDao.insert(new Project(1L, "Projet Tartampion", 0xFFEADAD1));
            projectDao.insert(new Project(2L, "Projet Lucidia", 0xFFB4CDBA));
            projectDao.insert(new Project(3L, "Projet Circus", 0xFFA3CED2));
        }
    }

    @After
    public void closingDb() {
        database.close();
    }

    /**
     * Testing the insertions of new tasks
     */
    @Test
    public void test_tasks_projects_database() throws InterruptedException {
        // CREATING TASKS
        taskDao.insert(new Task(1, "task 1", new Date().getTime()));
        taskDao.insert(new Task(2, "task 2", new Date().getTime()));
        taskDao.insert(new Task(3, "task 3", new Date().getTime()));
        taskDao.insert(new Task(4, "task 4", new Date().getTime()));

        // VERIFYING TASK LIST
        taskList.addAll(LiveDataTestUtil.getValue(this.taskDao.getAllTasks()));
        assertFalse(taskList.isEmpty());
        assertEquals(4, taskList.size());
    }

    /**
     * Testing the accessibility of the projects
     */
    @Test
    public void getting_projects_from_database() throws InterruptedException {
        // CREATING TASKS
        taskDao.insert(new Task(1, "task 1", new Date().getTime()));
        taskDao.insert(new Task(2, "task 2", new Date().getTime()));
        taskDao.insert(new Task(3, "task 3", new Date().getTime()));
        taskDao.insert(new Task(4, "task 4", new Date().getTime()));
        taskList.clear();
        taskList.addAll(LiveDataTestUtil.getValue(taskDao.getAllTasks()));

        // GETTING PROJECTS IDS FROM TASKS
        long p1Id = taskList.get(0).getProjectId();
        long p2Id = taskList.get(1).getProjectId();
        long p3Id = taskList.get(2).getProjectId();
        long p4Id = taskList.get(3).getProjectId();

        // GETTING THE PROJECT FROM THE DATABASE
        Project project1 = LiveDataTestUtil.getValue(this.projectDao.getProjectWithId(p1Id));
        Project project2 = LiveDataTestUtil.getValue(this.projectDao.getProjectWithId(p2Id));
        Project project3 = LiveDataTestUtil.getValue(this.projectDao.getProjectWithId(p3Id));
        Project project4 = LiveDataTestUtil.getValue(this.projectDao.getProjectWithId(p4Id));

        // ASSERTING THAT ONLY 3 PROJECTS EXISTS
        assertEquals("Projet Tartampion", project1.getName());
        assertEquals("Projet Lucidia", project2.getName());
        assertEquals("Projet Circus", project3.getName());
        assertNull(project4);
    }

    /**
     * Testing the deletion of all tasks at once
     */
    @Test
    public void delete_all_tasks() throws InterruptedException {
        // CREATING TASKS
        taskDao.insert(new Task(1, "task 1", new Date().getTime()));
        taskDao.insert(new Task(2, "task 2", new Date().getTime()));
        taskDao.insert(new Task(3, "task 3", new Date().getTime()));
        taskDao.insert(new Task(4, "task 4", new Date().getTime()));
        taskList.clear();
        taskList.addAll(LiveDataTestUtil.getValue(taskDao.getAllTasks()));

        assertFalse(taskList.isEmpty());

        // DELETING ALL TASKS
        taskDao.deleteAllTasks();
        taskList.clear();
        taskList.addAll(LiveDataTestUtil.getValue(this.taskDao.getAllTasks()));

        assertTrue(taskList.isEmpty());
    }

    /**
     * Testing the deletion of a project
     */
    @Test
    public void delete_project() throws InterruptedException {
        // DELETING A PROJECT
        Project projectToDelete = LiveDataTestUtil.getValue(projectDao.getProjectWithId(1));
        projectDao.delete(projectToDelete);
        projectList.clear();
        projectList.addAll(LiveDataTestUtil.getValue(this.projectDao.getAllProjects()));

        assertEquals(2, projectList.size());
    }

    /**
     * Testing the deletion of a task
     */
    @Test
    public void deletingTask() throws InterruptedException {
        // ADDING A NEW TASK TO TRY TO DELETE
        taskDao.insert(new Task(1, "task 5", new Date().getTime()));
        taskList.clear();
        taskList.addAll(LiveDataTestUtil.getValue(this.taskDao.getAllTasks()));
        Task taskToDelete = taskList.get(0);

        // DELETING THE TASK
        taskDao.delete(taskToDelete);

        // ASSERTING THE SUPPRESSION OF THE TASK
        taskList.clear();
        taskList.addAll(LiveDataTestUtil.getValue(this.taskDao.getAllTasks()));
        assertEquals(0, taskList.size());

        // VERIFYING PROJECT LIST
        List<Project> projectList = LiveDataTestUtil.getValue(this.projectDao.getAllProjects());
        assertFalse(projectList.isEmpty());
        assertEquals(3, projectList.size());
    }


}
