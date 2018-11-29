package dk.aau.ds304e18.database;

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

    }
}
