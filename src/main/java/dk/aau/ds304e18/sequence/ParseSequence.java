package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.List;

public class ParseSequence {

    public static List<Task> parseToSingleList(Project project, boolean rec) {
        return parseToSingleList(project, rec, false, 0);
    }

    /**
     * Parses a sequence to a single list
     *
     * @param project Takes project as parameter so we can get all the tasks in it and the sequence
     * @return returns an ordered list from the sequence
     */
    public static List<Task> parseToSingleList(Project project, boolean rec, boolean random, int index) {

        //List to return
        List<Task> parsedList = new ArrayList<>();

        //Splits the sequence so we just need to find the corresponding taskIds in order
        String taskList;
        if (!random)
            if (rec) taskList = project.getRecommendedPath();
            else taskList = project.getSequence();
        else taskList = project.getPossibleSequences()[index];
        taskList = taskList.replaceAll("[/(/)|]", ",");
        String[] taskListSplit = taskList.split(",");

        //Gets the tasks in the project
        List<Task> tasksInProject = project.getTasks();

        //For each task id in the string
        for (String aTaskListSplit : taskListSplit) {

            int taskId;

            //Set the taskId to the parsed int from the string array
            try {
                taskId = Integer.parseInt(aTaskListSplit);
            } catch (NumberFormatException e) {
                continue;
            }

            //Find the task and assign it
            Task task = tasksInProject.stream().filter(task1 -> task1.getId() == taskId).findFirst().orElse(null);

            //If it is not already in the list (i.e. if it's a dependency) add it to the parsedList
            if (!parsedList.contains(task)) {
                parsedList.add(task);
            }
        }
        return parsedList;
    }

    public static List<List<Task>> parseToMultipleLists(Project project) {

        String taskList = project.getSequence();
        String[] taskListSplit = taskList.split("\\|");
        List<List<Task>> returnList = new ArrayList<>();

        for (String task : taskListSplit) {
            task = task.replaceAll("\\(([^\\)]+)\\)", "");
            String[] temp = task.split(",");
            List<Task> listToInsert = new ArrayList<>();

            for (String tempTask : temp) {
                int taskId;

                try {
                    taskId = Integer.parseInt(tempTask);
                } catch (NumberFormatException e) {
                    continue;
                }

                Task taskToInsert = project.getTasks().stream().filter(task1 -> task1.getId() == taskId).findFirst().
                        orElse(null);
                listToInsert.add(taskToInsert);
            }
            returnList.add(listToInsert);
        }
        return returnList;
    }

    //Why stringbuilder param (Dodo)
    public static StringBuilder unparseList(StringBuilder putInto, List<Task> takeFrom, int tasksSize) {
        StringBuilder putIntoStringBuilder = new StringBuilder(putInto);
        List<Task> takeFromList = new ArrayList<>(takeFrom);

        for (int i = 0; i < takeFromList.size(); i++) {
            if (i != takeFromList.size() - 1) {
                putIntoStringBuilder.append(takeFromList.get(i).getId());
                putIntoStringBuilder.append(appendDependencies(takeFromList.get(i)));
                putIntoStringBuilder.append(",");
            } else {
                putIntoStringBuilder.append(takeFromList.get(i).getId());
                putIntoStringBuilder.append(appendDependencies(takeFromList.get(i)));
                if (tasksSize != 0)
                    putIntoStringBuilder.append("|");
            }
        }

        return putIntoStringBuilder;
    }

    private static String appendDependencies(Task task) {
        if (task.getDependencies().size() == 0) return "";

        StringBuilder returnString = new StringBuilder();
        returnString.append("(");

        for (int i = 0; i < task.getDependencies().size(); i++) {
            if (i != task.getDependencies().size() - 1) {
                returnString.append(task.getDependencies().get(i).getId()).append(",");
            } else {
                returnString.append(task.getDependencies().get(i).getId());
            }
        }

        returnString.append(")");
        return returnString.toString();
    }
}
