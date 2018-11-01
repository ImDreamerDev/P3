package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dk.aau.ds304e18.sequence.MonteCarlo.findFastestSequence;
import static dk.aau.ds304e18.sequence.ParseSequence.unparseList;

public class Sequence {

    public static String sequenceTasks(List<Task> taskList) {
        return sequenceTasks(taskList, false);
    }

    public static String sequenceTasks(List<Task> taskList, boolean fastSequence) {

        /*
        | indicates where it's supposed to be drawn
        taskId(taskId, taskId) indicates dependencies
        taskIds are separated by a comma

        ex:

        1,2|3(1),4(1,2)|5(2,3),6(4)|7(5)

        Would be that 1 and 2 can be done in parallel
        3 has a dependency on 1
        4 has a dependency on 1 and 2
        5 has a dependency on 2 and 3
        6 has a dependency on 4
        7 has a dependency on 5
         */

        if (!fastSequence)
            return findFastestSequence(taskList);

        //So we don't change the task list in the project
        List<Task> tasks = new ArrayList<>(taskList);

        //The sequence to return
        StringBuilder sequencedTasks = new StringBuilder();

        //Temporary lists, first to sort them and add them to sequencedTasks, second to remove the already sequenced tasks from the dependency list of each task
        List<Task> tasksToSort = new ArrayList<>();
        List<Task> tasksToRemove = new ArrayList<>();

        //As long as there are still tasks to be sorted
        while (tasks.size() != 0) {

            //For each task in the tasks to be sorted
            for (Task task : tasks) {

                //If there are no dependencies left unsorted for the task
                if (task.getAmountDependenciesLeft() == 0) {
                    //Add the task to the two temporary lists
                    tasksToSort.add(task);
                    tasksToRemove.add(task);
                }
            }

            //Remove the tasks from the tasks yet to be sorted
            tasks.removeAll(tasksToRemove);
            tasksToRemove = new ArrayList<>();

            //Add the sorted tasks to the sequencedTasks list
            List<Task> tasksToInsert = sortTasks(tasksToSort);
            sequencedTasks.append(unparseList(sequencedTasks, tasksToInsert, tasks.size()));
            tasksToSort = new ArrayList<>();

            //For each task in the yet to be sorted list
            for (Task task : tasks) {

                //For each dependency of the tasks
                for (Task dependency : task.getDependencies()) {

                    //If the dependency is already sequenced
                    if (!tasks.contains(dependency)) {
                        //Count down the amount of dependencies left so we know that if they're at 0, it's ready to be sequenced
                        task.setAmountDependenciesLeft(task.getAmountDependenciesLeft() - 1);
                    }
                }
            }
        }

        //Return the list of sequenced tasks
        return sequencedTasks.toString();
    }

    private static List<Task> sortTasks(List<Task> tasks) {

        //Sort tasks after their priority in descending order
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        //Return the sorted tasks
        return tasks;

    }

}
