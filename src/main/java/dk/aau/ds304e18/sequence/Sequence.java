package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.math.MonteCarlo;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static dk.aau.ds304e18.math.MonteCarlo.findFastestSequence;
import static dk.aau.ds304e18.sequence.ParseSequence.unparseList;

public class Sequence {

    public static void sequenceTasks(Project project, boolean fast) {

        //Find the best sequence
        findFastestSequence(project, fast);

        //The sequence to return
        String sequencedTasks = makeSequenceString(project);

        //Set the list of sequenced tasks
        project.setSequence(sequencedTasks);
    }

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

    private static List<Task> sortTasks(List<Task> tasks) {

        //Sort tasks after their priority in descending order
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        //Return the sorted tasks
        return tasks;

    }

    /**
     * Finds a random sequence with a project
     * TODO: Optimize this so we only find relevant sequences when going fast (Or close to every relevant sequence)
     * TODO: Still not good enough, there are significantly better sequences found if you do it slow rather than fast
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
            return simpleSequenceFinder(project);
        }

        //Sorts the tasks in prioritized order
        sortTasks(tasksNotSequenced);

        //If we're going fast but there's less than 2 employees
        if (project.getNumberOfEmployees() < 2) {
            return simpleSequenceFinder(project);
        }

        //Add all the tasks without dependencies to a list
        tasksWithoutDependencies = findTasksWithoutDeps(tasksNotSequenced);

        //Check every task to check if they have a task without dependency as a dependency, 
        //in which case we will do that task first to get a higher chance of having a better sequence
        List<Task> tasksSequenced = new ArrayList<>(findUsefulTasksWithoutDeps(tasksWithoutDependencies, tasksNotSequenced));

        //Add enough tasks at the start so every employee has something to do, 
        // if there's not enough tasks without dependencies for everyone just add everyone
        sequenceEmployees(project, tasksSequenced, tasksNotSequenced, tasksWithoutDependencies);

        //Count down tasksLeft so we don't end up in an infinite loop
        tasksLeft -= tasksSequenced.size();

        //While there are still tasksLeft to plug into the sequence, just sequence them normally
        simpleSequencing(tasksLeft, tasksSequenced, tasksNotSequenced);

        //Return the list
        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, 0).toString();
    }

    private static String simpleSequenceFinder(Project project) {
        int tasksLeft = project.getTasks().size();
        List<Task> tasksSequenced = new ArrayList<>();
        List<Task> tasksNotSequenced = new ArrayList<>(project.getTasks());

        simpleSequencing(tasksLeft, tasksSequenced, tasksNotSequenced);

        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, 0).toString();
    }

    private static void simpleSequencing(int tasksToSequence, List<Task> putInto, List<Task> takeFrom) {
        while (tasksToSequence > 0) {
            for (int i = 0; i < tasksToSequence; i++) {
                if (!putInto.containsAll(takeFrom.get(i).getDependencies())) continue;
                putInto.add(takeFrom.get(i));
                takeFrom.remove(takeFrom.get(i));
                tasksToSequence--;
            }
        }
    }

    private static List<Task> findTasksWithoutDeps(List<Task> listToSearch){
        List<Task> result = new ArrayList<>();

        for (Task task : listToSearch) {
            if (task.getDependencies().size() == 0)
                result.add(task);
        }

        return result;
    }

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

    private static void sequenceEmployees(Project project, List<Task> tasksSequenced, List<Task> tasksNotSequenced, List<Task> tasksWithoutDependencies) {
        int amountEmployees = (int) project.getNumberOfEmployees();

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
