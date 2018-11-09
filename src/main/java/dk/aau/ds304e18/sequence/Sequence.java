package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static dk.aau.ds304e18.sequence.MonteCarlo.findFastestSequence;
import static dk.aau.ds304e18.sequence.ParseSequence.unparseList;

public class Sequence {

    public static void sequenceTasks(Project project) {
        sequenceTasks(project, false);
    }

    public static void sequenceTasks(Project project, boolean findSequenceMontecarlo) {

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

        if (findSequenceMontecarlo)
            findFastestSequence(project);

        //So we don't change the task list in the project
        List<Task> tasks = new ArrayList<>(project.getTasks());

        //The sequence to return
        StringBuilder sequencedTasks = new StringBuilder();

        //Temporary list of tasks sequenced
        List<Task> tasksAlreadySequenced = new ArrayList<>();

        //Temporary lists, first to sort them and add them to sequencedTasks, second to remove the already sequenced tasks from the dependency list of each task
        List<Task> tasksToSort = new ArrayList<>();
        List<Task> tasksToRemove = new ArrayList<>();

        //As long as there are still tasks to be sorted
        while (tasks.size() != 0) {

            //For each task in the tasks to be sorted
            for (Task task : tasks) {
                if (!tasksAlreadySequenced.containsAll(task.getDependencies())) continue;
                //Add the task to the two temporary lists
                tasksToSort.add(task);
                tasksToRemove.add(task);
            }

            tasksAlreadySequenced.addAll(tasksToSort);

            //Remove the tasks from the tasks yet to be sorted
            tasks.removeAll(tasksToRemove);
            tasksToRemove = new ArrayList<>();

            //Add the sorted tasks to the sequencedTasks list
            List<Task> tasksToInsert = sortTasks(tasksToSort);
            sequencedTasks = unparseList(sequencedTasks, tasksToInsert, tasks.size());
            tasksToSort = new ArrayList<>();
        }

        //Set the list of sequenced tasks
        project.setSequence(sequencedTasks.toString());

        //Find the estimated time
        if (!findSequenceMontecarlo)
            MonteCarlo.estimateTime(project);
    }

    private static List<Task> sortTasks(List<Task> tasks) {

        //Sort tasks after their priority in descending order
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        //Return the sorted tasks
        return tasks;

    }

    public static String findRandomSequence(Project project) {

        int tasksLeft = project.getTasks().size();
        List<Task> tasksSequenced = new ArrayList<>();
        List<Task> tasksNotSequenced = new ArrayList<>(project.getTasks());
        List<Task> tasksToBeRemoved = new ArrayList<>();
        Collections.shuffle(tasksNotSequenced);

        //TODO: Optimize this to actually give relevant paths for multiple employees (etc. if there are 2 employees, the first 2 tasks shouldn't have dependencies if possible
        while (tasksLeft > 0) {
            for (Task task : tasksNotSequenced) {
                if (!tasksSequenced.containsAll(task.getDependencies())) continue;
                tasksSequenced.add(task);
                tasksToBeRemoved.add(task);
                tasksLeft--;
            }

            for (Task task : tasksToBeRemoved)
                tasksNotSequenced.remove(task);
            tasksToBeRemoved = new ArrayList<>();
        }

        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, tasksNotSequenced.size()).toString();

    }

}
