package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

    static List<Project> parseProjectsFromResultSet(ResultSet rs) {
        List<Project> projects = new ArrayList<>();
        try {
            if (rs == null) return null;
            while (rs.next()) {

                List<Double> possibleCompletions = new ArrayList<>();

                if (rs.getArray("possiblecompletions") != null)

                    possibleCompletions.addAll(Arrays.asList((Double[]) rs.getArray("possiblecompletions").getArray()));

                Project project = new Project(rs.getInt(1), rs.getString(2),
                        ProjectState.values()[rs.getInt(3)], rs.getString(4),
                        rs.getDouble(5), rs.getString(6), rs.getDouble(7), possibleCompletions);
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return projects;
    }
}
