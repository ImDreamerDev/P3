package dk.aau.ds304e18.database;

import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseParserTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void testParseEmployeesFromResultSet() {
        int empCount = 0;
        ResultSet resultSet = DatabaseManager.query("SELECT COUNT(*) FROM employees");
        try {
            Objects.requireNonNull(resultSet).next();
            empCount = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = DatabaseManager.query("select * from employees");
        List<Employee> employees = DatabaseParser.parseEmployeesFromResultSet(rs);
        assertNotNull(employees);
        assertEquals(empCount, employees.size());
    }

    @Test
    void testParseProjectsFromResultSet() {
        int projectsCount = 0;
        ResultSet resultSet = DatabaseManager.query("SELECT COUNT(*) FROM projects");
        try {
            Objects.requireNonNull(resultSet).next();
            projectsCount = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = DatabaseManager.query("select * from projects");
        List<Project> projects = DatabaseParser.parseProjectsFromResultSet(rs);
        assertNotNull(projects);
        assertEquals(projectsCount, projects.size());

    }

    @Test
    void testParseTasksFromResultSet() {
        int tasksCount = 0;
        ResultSet resultSet = DatabaseManager.query("SELECT COUNT(*) FROM tasks");
        try {
            Objects.requireNonNull(resultSet).next();
            tasksCount = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = DatabaseManager.query("select * from tasks");
        List<Task> tasks = DatabaseParser.parseTasksFromResultSet(rs);
        assertNotNull(tasks);
        assertEquals(tasksCount, tasks.size());
    }

    @Test
    void testParseProjectManagersFromResultSet() {
        int projectManagerCount = 0;
        ResultSet resultSet = DatabaseManager.query("SELECT COUNT(*) FROM projectmanagers");
        try {
            Objects.requireNonNull(resultSet).next();
            projectManagerCount = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = DatabaseManager.query("select * from projectmanagers");
        List<ProjectManager> projectManagers = DatabaseParser.parseProjectManagersFromResultSet(rs);

        assertNotNull(projectManagers);
        assertEquals(projectManagerCount, projectManagers.size());
    }

    @Test
    void testParseProbabilities() {
        ProjectManager projectManager = new ProjectManager("Tg", "whoknows");
        Project project = new Project("Tasd", projectManager);
        Task task = new Task("30", 20, 0, project);
        task.getProbabilities().add(new Probabilities(30, 60));
        DatabaseManager.updateTask(task);
        DatabaseParser.parseProbabilities(task);
        ResultSet rs = DatabaseManager.query("select * from tasks where id =" + task.getId());

        Task dbTask = DatabaseParser.parseTasksFromResultSet(rs).get(0);
        assertEquals(dbTask.getProbabilities().get(0).getDuration(), task.getProbabilities().get(0).getDuration());
        assertEquals(dbTask.getProbabilities().get(0).getProbability(), task.getProbabilities().get(0).getProbability());

    }

    @Test
    void testParseProbabilities02() {
        ProjectManager projectManager = new ProjectManager("Tg", "whoknows");
        Project project = new Project("Tasd", projectManager);
        Task task = new Task("30", 20, 0, project);
        task.getProbabilities().add(new Probabilities(30, 60));
        task.getProbabilities().add(new Probabilities(534, 50));
        DatabaseManager.updateTask(task);
        DatabaseParser.parseProbabilities(task);
        ResultSet rs = DatabaseManager.query("select * from tasks where id =" + task.getId());

        Task dbTask = DatabaseParser.parseTasksFromResultSet(rs).get(0);
        assertEquals(dbTask.getProbabilities().get(0).getDuration(), task.getProbabilities().get(0).getDuration());
        assertEquals(dbTask.getProbabilities().get(0).getProbability(), task.getProbabilities().get(0).getProbability());
        assertEquals(dbTask.getProbabilities().get(1).getDuration(), task.getProbabilities().get(1).getDuration());
        assertEquals(dbTask.getProbabilities().get(1).getProbability(), task.getProbabilities().get(1).getProbability());
    }
}
