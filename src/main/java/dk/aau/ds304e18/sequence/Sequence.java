package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static dk.aau.ds304e18.math.MonteCarlo.calculateProjectInformation;
import static dk.aau.ds304e18.sequence.ParseSequence.unparseList;

public class Sequence {

    /**
     * Finds the sequence of the project and calculates everything related to the project
     *
     * @param project The project to sequence and calculate
     * @param fast    If a fast sequencing is wanted - Less accurate
     */
    public static void sequenceAndCalculateProject(Project project, boolean fast, int monteCarloRepeats) {

        int amountSeq;

        if (fast)
            amountSeq = 200;
        else
            amountSeq = 1000;
        //Find the best sequence with amountSeq sequences
        calculateProjectInformation(project, amountSeq, fast, monteCarloRepeats);

        //The sequence to return
        String sequencedTasks = makeSequenceString(project);

        //Set the list of sequenced tasks
        project.setSequence(sequencedTasks);
    }

    /**
     * Makes the sequence into a string so it can be sent everywhere and be understood
     *
     * @param project The project to make the sequence for
     * @return The string of the sequence
     */
    private static String makeSequenceString(Project project) {

        //The sequence to set
        StringBuilder sequencedTasks = new StringBuilder();

        //So we don't change the task list in the project
        List<Task> tasks = new ArrayList<>(project.getTasks());

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

        return sequencedTasks.toString();

    }

    /**
     * Sorts the tasks by priority
     *
     * @param tasks the tasks to sort
     * @return A sorted (by priority) list of tasks
     */
    private static List<Task> sortTasks(List<Task> tasks) {

        //Sort tasks after their priority in descending order
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        //Return the sorted tasks
        return tasks;

    }

    /**
     * Finds a random sequence with a project
     *
     * @param project The project where we want a random sequence
     * @param fast    If you want to do fast calculation or slow
     * @return Returns a string with the sequence
     */
    public static String findRandomSequence(Project project, boolean fast) {

        int tasksLeft = project.getTasks().size();
        List<Task> tasksNotSequenced = new ArrayList<>(project.getTasks());
        List<Task> tasksWithoutDependencies;

        //Shuffles the list
        Collections.shuffle(tasksNotSequenced);

        //If we're not going fast, just plug all of the tasks in, in a legal way and return that
        if (!fast) {
            return simpleSequenceFinder(project, tasksNotSequenced);
        }

        //Sorts the tasks in prioritized order
        sortTasks(tasksNotSequenced);

        //If we're going fast but there's less than 2 employees
        if (project.getNumberOfWorkGroups() < 2) {
            return simpleSequenceFinder(project, tasksNotSequenced);
        }

        //Add all the tasks without dependencies to a list
        tasksWithoutDependencies = findTasksWithoutDeps(tasksNotSequenced);

        //Check every task to check if they have a task without dependency as a dependency, 
        //in which case we will do that task first to get a higher chance of having a better sequence
        List<Task> tasksSequenced = new ArrayList<>(findUsefulTasksWithoutDeps(tasksWithoutDependencies, tasksNotSequenced));

        //Add enough tasks at the start so every work group has something to do,
        // if there's not enough tasks without dependencies for everyone just add everyone
        sequenceWorkGroups(project, tasksSequenced, tasksNotSequenced, tasksWithoutDependencies);

        //Count down tasksLeft so we don't end up in an infinite loop
        tasksLeft -= tasksSequenced.size();

        //While there are still tasksLeft to plug into the sequence, just sequence them normally
        simpleSequencing(tasksLeft, tasksSequenced, tasksNotSequenced);

        //Return the list
        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, 0).toString();
    }

    /**
     * Finds a legal sequence, makes no effort to optimize
     *
     * @param project The project to find a sequence for
     * @param tasksNotSequenced The tasks not yet sequenced
     * @return Returns a legal sequence
     */
    private static String simpleSequenceFinder(Project project, List<Task> tasksNotSequenced) {
        //Amount of tasks to sequence
        int tasksLeft = project.getTasks().size();

        //The final list of sequenced tasks
        List<Task> tasksSequenced = new ArrayList<>();

        //Sequence the tasks
        simpleSequencing(tasksLeft, tasksSequenced, tasksNotSequenced);

        //Return the string for the sequence
        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, 0).toString();
    }

    /**
     * Sequences a list of tasks and puts them into a list in a legal order
     *
     * @param tasksToSequence The amount of tasks to sequence
     * @param putInto         The list to put into
     * @param takeFrom        The list of tasks to take from
     */
    private static void simpleSequencing(int tasksToSequence, List<Task> putInto, List<Task> takeFrom) {
        //While there are still tasks to sequence
        while (tasksToSequence > 0) {

            //Go through all the tasks
            for (int i = 0; i < tasksToSequence; i++) {

                //If the current tasks dependencies have not yet been sequenced, skip the task, it'll be sequenced later
                if (!putInto.containsAll(takeFrom.get(i).getDependencies())) continue;

                //Add the task to the list with the already sequenced tasks
                putInto.add(takeFrom.get(i));

                //Remove the task from the list with the tasks not yet sequenced
                takeFrom.remove(takeFrom.get(i));

                //Count down the tasks that need to be sequenced
                tasksToSequence--;
            }
        }
    }

    /**
     * Finds tasks in a list that doesn't have dependencies
     *
     * @param listToSearch The tasks to search
     * @return The list of tasks without dependencies
     */
    private static List<Task> findTasksWithoutDeps(List<Task> listToSearch) {
        List<Task> result = new ArrayList<>();

        for (Task task : listToSearch) {
            if (task.getDependencies().size() == 0)
                result.add(task);
        }

        return result;
    }

    /**
     * Finds the tasks without dependencies that other tasks are dependent on
     *
     * @param tasksWithoutDependencies The tasks without dependencies
     * @param listToLookThrough        The full list of tasks
     * @return The tasks without dependencies that other tasks are dependent on
     */
    private static List<Task> findUsefulTasksWithoutDeps(List<Task> tasksWithoutDependencies, List<Task> listToLookThrough) {
        List<Task> result = new ArrayList<>();

        for (Task tasksWithoutDep : tasksWithoutDependencies) {
            for (Task task : listToLookThrough)
                if (task.getDependencies().contains(tasksWithoutDep)) {
                    result.add(tasksWithoutDep);
                    listToLookThrough.remove(tasksWithoutDep);
                    break;
                }
        }

        tasksWithoutDependencies.removeAll(result);

        return result;

    }

    /**
     * Sequences tasks at the start so every work group has something to do at the start if possible
     *
     * @param project                  The project to sequence
     * @param tasksSequenced           The tasks that has been sequenced
     * @param tasksNotSequenced        The tasks that has yet to be sequenced
     * @param tasksWithoutDependencies The tasks without dependencies which will be put at the start
     */
    private static void sequenceWorkGroups(Project project, List<Task> tasksSequenced, List<Task> tasksNotSequenced, List<Task> tasksWithoutDependencies) {
        int amountEmployees = (int) project.getNumberOfWorkGroups();

        if (amountEmployees >= tasksWithoutDependencies.size() + tasksSequenced.size()) {
            tasksSequenced.addAll(tasksWithoutDependencies);
            tasksNotSequenced.removeAll(tasksWithoutDependencies);
        } else {
            for (int i = 0; i < amountEmployees - tasksSequenced.size(); i++) {
                tasksSequenced.add(tasksWithoutDependencies.get(i));
                tasksNotSequenced.remove(tasksWithoutDependencies.get(i));
            }
        }
    }

}
