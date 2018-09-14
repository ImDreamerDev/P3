package dk.aau.ds304e18;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.models.Worker;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        new Main().SetUpTaskExample();
    }


    public void SetUpTaskExample() {
        Project project = new Project();
        Worker per = project.AddWorker("Per", 8);
        Task t1 = project.AddTask("Task1", Duration.ofHours(10));
        Task t2 = project.AddTask("Task2", Duration.ofHours(3));
        Task t4 = project.AddTask("Task3", Duration.ofHours(4));
        Task t5 = project.AddTask("Task4", Duration.ofHours(6));
        Task t3 = project.AddTask("Task5", Duration.ofHours(12), project.GetTask("Task1"), project.GetTask("Task2"));
        Task t6 = project.AddTask("Task6", Duration.ofHours(7), project.GetTask("Task3"), project.GetTask("Task4"));
        Task t7 = project.AddTask("Task7", Duration.ofHours(9), project.GetTask("Task5"), project.GetTask("Task6"));
        t1.Assign(per);
        t7.Assign(per);
    }

/*
dk.aau.ds304e18.models.Task-1 with Duration A
dk.aau.ds304e18.models.Task-2 with Duration B
dk.aau.ds304e18.models.Task-3 with Duration C
dk.aau.ds304e18.models.Task-4 with Duration D
dk.aau.ds304e18.models.Task-5 with Duration E
dk.aau.ds304e18.models.Task-6 with Duration F
dk.aau.ds304e18.models.Task-7 with Duration G

dk.aau.ds304e18.models.Task-1,-2,-3 and -4 are all independent.
dk.aau.ds304e18.models.Task-5 cannot start before dk.aau.ds304e18.models.Task-1 and -2 are complete
dk.aau.ds304e18.models.Task-6 cannot start before dk.aau.ds304e18.models.Task-3 and -4 are complete
dk.aau.ds304e18.models.Task-7 cannot start before dk.aau.ds304e18.models.Task-5 and -6 are complete
*/

}
