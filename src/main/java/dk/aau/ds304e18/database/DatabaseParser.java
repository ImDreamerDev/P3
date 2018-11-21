package dk.aau.ds304e18.database;

import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseParser {
    /**
     * Parses a ResultSet to a list of Employees.
     *
     * @param rs the ResultSet to parse.
     * @return list of Employees that got parsed or null.
     */
    public static List<Employee> parseEmployeesFromResultSet(ResultSet rs) {
        List<Employee> empList = new ArrayList<>();
        try {
            if (rs == null) return null;
            while (rs.next()) {
                Employee emp = new Employee(rs.getInt(1), rs.getString(2),
                        Arrays.asList((Integer[]) rs.getArray(3).getArray()));
                emp.setProjectId(rs.getInt(4));
                empList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empList;
    }

    /**
     * Parses a ResultSet to Projects.
     *
     * @param rs the ResultSet to parse.
     * @return a list of Projects that got passed or null.
     */
    public static List<Project> parseProjectsFromResultSet(ResultSet rs) {
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

    /**
     * Parses Project manager from the result set set.
     *
     * @param rs - the result set to parse
     * @return ProjectManagers - a list of project managers.
     */
    public static List<ProjectManager> parseProjectManagersFromResultSet(ResultSet rs) {

        List<ProjectManager> projectManagers = new ArrayList<>();

        try {
            if (rs == null) return null;
            while (rs.next()) {
                List<Integer> currentProjects = new ArrayList<>();
                if (rs.getArray(4) != null) currentProjects = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                ProjectManager projectManager;
                if (rs.getArray(5) != null)
                    projectManager = new ProjectManager(rs.getInt(1), rs.getString(2),
                            currentProjects, Arrays.asList((Integer[]) rs.getArray(5).getArray()));
                else {
                    projectManager = new ProjectManager(rs.getInt(1), rs.getString(2),
                            currentProjects, null);
                }
                projectManagers.add(projectManager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectManagers;
    }


    /**
     * Parses a ResultSet to Task(s).
     *
     * @param rs the ResultSet to parse.
     * @return a list of Task that got parsed.
     */
    public static List<Task> parseTasksFromResultSet(ResultSet rs) {

        List<Task> tasks = new ArrayList<>();

        try {
            if (rs == null) return null;

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                double estimatedTime = rs.getDouble(3);
                int priority = rs.getInt(5);
                int projectId = rs.getInt(6);

                List<Integer> dependenceIds = new ArrayList<>();
                List<Integer> employeeIds = new ArrayList<>();
                List<Probabilities> probabilities = new ArrayList<>();
                if (rs.getArray(4) != null) {
                    dependenceIds = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                }

                if (rs.getArray(7) != null) {
                    employeeIds = Arrays.asList((Integer[]) rs.getArray(7).getArray());
                }

                if (rs.getArray(8) != null) {
                    ResultSet rsw = rs.getArray(8).getResultSet();

                    while (rsw.next()) {
                        String[] probValues = rsw.getString(2).replaceAll("[/(/)]", "")
                                .split(",");
                        probabilities.add(new Probabilities(Double.parseDouble(probValues[0]),
                                Double.parseDouble(probValues[1])));
                    }
                }
                double startTime = -1d;
                if (rs.getDouble(9) > 0) startTime = rs.getDouble(9);

                Task task = new Task(id, name, estimatedTime, priority, dependenceIds, employeeIds,
                        projectId, probabilities, startTime);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;

    }

    /**
     * This method parses the information from the database which turns the probabilities into a string format.
     *
     * @param task - the task to parse.
     * @return Probability string - the probabilities turned into string format.
     */
    public static String parseProbabilities(Task task) {
        //Turns the Probabilities into a string in the following format
        //     * '{"(1.1,2.2)","(534.1,3123.2)"}'
        //     * '{"(duration,probability)"}'
        StringBuilder probabilitySQL = new StringBuilder("'{");
        task.getProbabilities().forEach(probabilities -> {
            probabilitySQL.append("\"(").append(probabilities.getDuration()).append(",").
                    append(probabilities.getProbability()).append(")\"");
            if (task.getProbabilities().indexOf(probabilities) != task.getProbabilities().size() - 1) {
                probabilitySQL.append(",");
            }
        });
        probabilitySQL.append("}'");
        return probabilitySQL.toString();
    }
}
