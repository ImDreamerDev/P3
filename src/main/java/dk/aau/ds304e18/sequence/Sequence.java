package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sequence {

    public static List<Task> sequenceTasks(List<Task> taskList) {

        //So we don't change the task list in the project
        List<Task> tasks = new ArrayList<>(taskList);

        //The list to return
        List<Task> sequencedTasks = new ArrayList<>();

        //Temporary lists, first to sort them and add them to sequencedTasks, second to remove the already sequenced tasks from the dependency list of each task
        List<Task> tasksToSort = new ArrayList<>();
        List<Task> tasksToRemove = new ArrayList<>();

        //As long as there are still tasks to be sorted
        while(tasks.size() != 0){

            //For each task in the tasks to be sorted
            for(Task task : tasks){

                //If there are no dependencies left unsorted for the task
                if(task.getAmountDependenciesLeft() == 0){

                    //Add the task to the two temporary lists
                    tasksToSort.add(task);
                    tasksToRemove.add(task);

                }

            }

            //Remove the tasks from the tasks yet to be sorted
            tasks.removeAll(tasksToRemove);
            tasksToRemove = new ArrayList<>();

            //Add the sorted tasks to the sequencedTasks list
            sequencedTasks.addAll(sortTasks(tasksToSort));
            tasksToSort = new ArrayList<>();

            //For each task in the yet to be sorted list
            for(Task task : tasks){

                //For each dependency of the tasks
                for(Task dependency : task.getDependencies()){

                    //If the dependency is already sequenced
                    if(sequencedTasks.contains(dependency)){

                        //Count down the amount of dependencies left so we know that if they're at 0, it's ready to be sequenced
                        task.setAmountDependenciesLeft(task.getAmountDependenciesLeft() - 1);

                    }

                }

            }

        }

        //Return the list of sequenced tasks
        return sequencedTasks;

    }

    private static List<Task> sortTasks(List<Task> tasks){

        //Sort tasks after their priority in descending order
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        //Return the sorted tasks
        return tasks;

    }

}
