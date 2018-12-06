package dk.aau.ds304e18.integration;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.MonteCarloExecutorService;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.Sequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests {

    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void integrationTest01() {
        ProjectManager projectManager = new ProjectManager("IntegrationTestProjectManager", "Easy");

        ProjectManager tempRet = DatabaseManager.logIn("IntegrationTestProjectManager", "Easy");

        if (tempRet == null || tempRet.getName().equals("Connection Error")) fail();

        Project project = new Project("IntegrationTestProject", projectManager);

        Task task1 = new Task("Task 1", 50, 5, project);
        Probabilities prob11 = new Probabilities(10, 25);
        Probabilities prob12 = new Probabilities(50, 50);
        task1.getProbabilities().addAll(Arrays.asList(prob11, prob12));

        Task task2 = new Task("Task 2", 25, 5, project);
        Probabilities prob21 = new Probabilities(5, 10);
        Probabilities prob22 = new Probabilities(20, 45);
        Probabilities prob23 = new Probabilities(45, 80);
        task2.getProbabilities().addAll(Arrays.asList(prob21, prob22, prob23));

        Task task3 = new Task("Task 3", 120, 7, project);
        Probabilities prob31 = new Probabilities(50, 25);
        Probabilities prob32 = new Probabilities(120, 50);
        task3.getProbabilities().addAll(Arrays.asList(prob31, prob32));
        task3.addDependency(Collections.singletonList(task2));

        Task task4 = new Task("Task 4", 20, 3, project);
        Probabilities prob41 = new Probabilities(20, 50);
        Probabilities prob42 = new Probabilities(70, 99);
        task4.getProbabilities().addAll(Arrays.asList(prob41, prob42));

        Employee employee1 = new Employee("IntegrationTestEmployee1", project);
        Employee employee2 = new Employee("IntegrationTestEmployee2", project);
        Employee employee3 = new Employee("IntegrationTestEmployee3", project);
        employee3.setProject(null);
        employee3.setProject(project);

        task1.addEmployee(employee1, employee2);
        task2.addEmployee(employee3);
        task3.addEmployee(employee1, employee2, employee3);
        task4.addEmployee(employee3);

        project.setNumberOfEmployees(2);

        MonteCarloExecutorService.init();
        int repeats = 10000 + MonteCarloExecutorService.getNumOfThreads() - 10000 % MonteCarloExecutorService.getNumOfThreads();

        Sequence.sequenceAndCalculateProject(project, false, repeats);

        List<Employee> forAssertion = new ArrayList<>(Arrays.asList(employee1, employee2, employee3));

        assertNotNull(project.getSequence());
        assertNotNull(project.getRecommendedPath());
        assertEquals(2, project.getNumberOfEmployees());
        assertEquals(243, project.getDuration(), 5);
        assertEquals(forAssertion, project.getEmployees());
        assertEquals(project.getRecommendedEmployees().getAmountEmployees(), new ArrayList<Integer>());
        assertEquals(project.getRecommendedEmployees().getEstimatedTime(), new ArrayList<Double>());

    }
}
