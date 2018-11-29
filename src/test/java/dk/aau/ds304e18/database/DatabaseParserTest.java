package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class DatabaseParserTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }
    
  /*  @Test
    static void testParseEmployeesFromResultSet() {
        DatabaseManager.query();
        List<Employee> empList = new ArrayList<>();
        try {
            if (rs == null) return null;
            while (rs.next()) {
                Employee emp = new Employee(rs.getInt(1), rs.getString(2));
                emp.setProjectId(rs.getInt(3));
                empList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empList;
    }*/
}
