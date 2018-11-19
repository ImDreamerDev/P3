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
        sequenceTasks(project, false, true);
    }

    public static void sequenceTasks(Project project, boolean findSequenceMonteCarlo, boolean fast) {

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

        if (findSequenceMonteCarlo)
            findFastestSequence(project, fast);

        //So we don't change the task list in the project
        List<Task> tasks = new ArrayList<>(project.getTasks());

        //The sequence to return
        StringBuilder sequencedTasks = new StringBuilder();

        //Temporary list of tasks sequenced
        List<Task> tasksAlreadySequenced = new ArrayList<>();

        //Temporary lists, first to sort them and add them to sequencedTasks, 
        // second to remove the already sequenced tasks from the dependency list of each task
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
        if (!findSequenceMonteCarlo)
            MonteCarlo.estimateTime(project);
    }

    public static List<Task> sortTasks(List<Task> tasks) {

        //Sort tasks after their priority in descending order
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        //Return the sorted tasks
        return tasks;

    }

    /**
     * Finds a random sequence with a project
     * TODO: Optimize this so we only find relevant sequences when going fast (Or close to every relevant sequence)
     * TODO: Still not good enough, there are significantly better sequences found if you do it slow rather than fast
     * @param project The project where we want a random sequence
     * @param fast    If you want to do fast calculation or slow
     * @return Returns a string with the sequence
     */
    public static String findRandomSequence(Project project, boolean fast) {

        int tasksLeft = project.getTasks().size();
        List<Task> tasksSequenced = new ArrayList<>();
        List<Task> tasksNotSequenced = new ArrayList<>(project.getTasks());
        List<Task> tasksToBeRemoved = new ArrayList<>();
        int amountEmployees = (int) project.getNumberOfEmployees();
        List<Task> tasksWithoutDependencies = new ArrayList<>();
        Collections.shuffle(tasksNotSequenced);

        //If we're not going fast, just plug all of the tasks in, in a legal way and return that
        if (!fast) {
            while (tasksLeft > 0) {
                for (int i = 0; i < tasksLeft; i++) {
                    if (!tasksSequenced.containsAll(tasksNotSequenced.get(i).getDependencies())) continue;
                    tasksSequenced.add(tasksNotSequenced.get(i));
                    tasksNotSequenced.remove(tasksNotSequenced.get(i));
                    tasksLeft--;
                }
            }
            return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, tasksNotSequenced.size()).toString();
        }

        //sortTasks(tasksNotSequenced); //Might not make sense to put prioritised first - It can make the project longer than it should

        if(project.getNumberOfEmployees() < 2) {
            for(int i = 0; i < tasksLeft; i++) {
                if (!tasksSequenced.containsAll(tasksNotSequenced.get(i).getDependencies())) continue;
                tasksSequenced.add(tasksNotSequenced.get(i));
                tasksNotSequenced.remove(tasksNotSequenced.get(i));
                i = -1;
                tasksLeft--;
            }
            return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, tasksNotSequenced.size()).toString();
        }

        //Add all the tasks without dependencies to a list
        for (Task aTasksNotSequenced : tasksNotSequenced) {
            if (aTasksNotSequenced.getDependencies().size() == 0)
                tasksWithoutDependencies.add(aTasksNotSequenced);
        }

        //Check every task to check if they have a task without dependency as a dependency, 
        // in which case we will do that task first to get a higher chance of having a better sequence
        for (Task tasksWithoutDep : tasksWithoutDependencies) {
            for (Task task : tasksNotSequenced)
                if (task.getDependencies().contains(tasksWithoutDep)) {
                    tasksSequenced.add(tasksWithoutDep);
                    tasksNotSequenced.remove(tasksWithoutDep);
                    tasksToBeRemoved.add(tasksWithoutDep);
                    break;
                }
        }

        //Remove every task that has been added already from the tasksWithoutDependencies list
        for (Task task : tasksToBeRemoved)
            tasksWithoutDependencies.remove(task);
        tasksToBeRemoved = new ArrayList<>();

        //Add enough tasks at the start so every employee has something to do, 
        // if there's not enough tasks without dependencies for everyone just add everyone
        if (amountEmployees >= tasksWithoutDependencies.size() + tasksSequenced.size()) {
            tasksSequenced.addAll(tasksWithoutDependencies);
            tasksNotSequenced.removeAll(tasksWithoutDependencies);
        } else {
            for (int i = 0; i < amountEmployees - tasksSequenced.size(); i++) {
                tasksSequenced.add(tasksWithoutDependencies.get(i));
                tasksNotSequenced.remove(tasksWithoutDependencies.get(i));
            }
        }

        //Count down tasksLeft so we don't end up in an infinite loop
        tasksLeft -= tasksSequenced.size();

        //While there are still tasksLeft to plug into the sequence
        while (tasksLeft > 0) {

            //For each task in the tasksNotSequenced List
            for (Task task : tasksNotSequenced) {

                //Initialize a boolean to false
                boolean cont = false;

                //Skip the tasks without dependencies unless there are no other tasks left 
                // (This seems to give a better chance at good sequences)
                if (tasksWithoutDependencies.contains(task)) {
                    for (Task task1 : tasksNotSequenced) {
                        if (!tasksWithoutDependencies.contains(task1) && tasksSequenced.containsAll(task1.getDependencies()))
                            cont = true;
                    }
                }
                if (cont) continue;

                //If the tasks previously sequenced does not contains all the dependencies: skip
                if (!tasksSequenced.containsAll(task.getDependencies())) continue;

                //Add the task to the sequence and remove it from the other list
                tasksSequenced.add(task);
                tasksToBeRemoved.add(task);
                tasksLeft--;
            }

            //Remove the tasks that should be removed
            for (Task task : tasksToBeRemoved)
                tasksNotSequenced.remove(task);
            tasksToBeRemoved = new ArrayList<>();
        }

        //Return the list
        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, tasksNotSequenced.size()).toString();
    }
}
