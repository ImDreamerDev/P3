package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.List;

public class ParseSequence {

    public static List<Task> parseToList(String taskList){

        List<Task> parsedList = new ArrayList<>();

        return parsedList;

    }

    public static String unparseList(StringBuilder putInto, List<Task> takeFrom, int tasksSize){

        StringBuilder putIntoStringBuilder = new StringBuilder(putInto);
        List<Task> takeFromList = new ArrayList<>(takeFrom);

        for(int i = 0; i < takeFromList.size(); i++){
            if(i != takeFromList.size()-1){
                putIntoStringBuilder.append(takeFromList.get(i).getId());
                putIntoStringBuilder.append(appendDependencies(takeFromList.get(i)));
                putIntoStringBuilder.append(",");
            } else {
                putIntoStringBuilder.append(takeFromList.get(i).getId());
                putIntoStringBuilder.append(appendDependencies(takeFromList.get(i)));
                if(tasksSize != 0)
                    putIntoStringBuilder.append("|");
            }
        }

        return putIntoStringBuilder.toString();

    }

    private static String appendDependencies(Task task) {

        if(task.getDependencies().size() == 0) return "";

        StringBuilder returnString = new StringBuilder();

        returnString.append("(");

        for(int i = 0; i < task.getDependencies().size(); i++){
            if(i != task.getDependencies().size()-1){
                returnString.append(task.getDependencies().get(i).getId()).append(",");
            }else{
                returnString.append(task.getDependencies().get(i).getId());
            }
        }

        returnString.append(")");

        return returnString.toString();

    }

}
