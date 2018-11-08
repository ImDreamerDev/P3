package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.math.CalculateLambda;
import dk.aau.ds304e18.math.InverseGaussian;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.*;
import java.util.concurrent.*;

public class MonteCarlo {

    public static void findFastestSequence(Project project) {

        //Calls the function with the default value 100
        //If it finds a significant difference within the first 100, it will try another 100 etc.
        findFastestSequence(project, 100);

    }

    public static void findFastestSequence(Project project, int monteCarloRepeats) {

        int i = 0;
        String bestSequence = "";
        String worstSequence = "";
        double bestTime = -1;
        double worstTime = -1; //May be used in the future

        while (i < monteCarloRepeats) {

            project.setRecommendedPath(findRandomSequence(project));
            estimateTime(project, true);

            if(project.getDuration() > worstTime || worstTime == -1) {
                worstSequence = project.getRecommendedPath();
                worstTime = project.getDuration();
            }

            if (project.getDuration() < bestTime || bestTime == -1) {
                //Set the best sequences and best times
                bestSequence = project.getRecommendedPath();
                bestTime = project.getDuration();
            }

            i++;
        }

        project.setRecommendedPath(bestSequence);
        project.setDuration(bestTime);

    }

    private static String findRandomSequence(Project project) {

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

    public static void estimateTime(Project project) {

        //Calls the function with the default value 10000
        estimateTime(project, false, 10000);

    }

    public static void estimateTime(Project project, boolean rec) {

        //Calls the function with the default value 10000
        estimateTime(project, rec, 10000);

    }

    /**
     * Estimates the time assuming only one task can be done at a time from a project and an amount of time to repeat the tasks
     *
     * @param project           the project where we want to find the estimated duration
     * @param monteCarloRepeats how many times we want to repeat the project schedule (Higher number will be more accurate but will take longer time)
     */
    public static void estimateTime(Project project, boolean rec, int monteCarloRepeats) {

        //Gets the task list from the project
        List<Task> taskList = ParseSequence.parseToSingleList(project, rec);

        //The duration that will be counted up and then divided by the amount of repeats we have
        double duration = 0.0;

        //For each task in taskList
        for (Task task : taskList) {

            //If the task does not have a lambda yet
            if (task.getLambda() == -1) {
                //Calculate the lambda and optimize the mu value
                List<Double> temp = CalculateLambda.calculateLambda(task.getEstimatedTime(), task.getProbabilities());
                task.setEstimatedTime(temp.get(0));
                task.setLambda(temp.get(1));
            }

        }

        //Get number of threads
        int numOfThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        //create a list to hold the Future object associated with Callable
        List<Future<Double>> list = new ArrayList<Future<Double>>();
        //Create MyCallable instance
        Callable<Double> callable = new EstimateTimeCallable(taskList, project.getEmployees().size(), numOfThreads, monteCarloRepeats);
        for (int i = 0; i < numOfThreads; i++) {
            //submit Callable tasks to be executed by thread pool
            Future<Double> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }

        for (Future<Double> fut : list) {
            try {
                //print the return value of Future, notice the output delay in console
                // because Future.get() waits for task to get completed
                duration = duration + fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
        //Set the duration of the project to the duration divided by amount of times it has been repeated (The average of all the tries)
        project.setDuration(duration / monteCarloRepeats);
    }


}

class EstimateTimeCallable implements Callable<Double> {
    private List<Task> taskList;
    private int amountEmployees;
    private int numOfThreads;
    private int numOfMonte;

    public EstimateTimeCallable(List<Task> taskList, int amountEmployees, int numOfThreads, int numOfMonte) {
        this.taskList = taskList;
        this.amountEmployees = amountEmployees;
        this.numOfThreads = numOfThreads;
        this.numOfMonte = numOfMonte;
    }

    @Override
    public Double call() throws Exception {
        Random r = new Random();
        double duration = 0.0;
        int repeats = numOfMonte / numOfThreads;
        //Repeat repeats time
        for (int i = 0; i < repeats; i++) {

            if(amountEmployees > 1) {

                List<Task> tasksDone = new ArrayList<>();
                List<Double> durations = new ArrayList<>();
                HashMap<Task, Double> taskDoneAt = new HashMap<>();

                for(int j = 0; j < amountEmployees; j++){
                    durations.add(0d);
                }

                //TODO: Consider instead of checking if they're the same size, deleting tasks from a local copy of taskList
                while(taskList.size() != tasksDone.size()){

                    for(int j = 0; j < amountEmployees; j++) {

                        if(!(0 + durations.get(j) == 0 + Collections.min(durations))) continue;

                        boolean temp = false;
                        for (Task task : taskList) {
                            if (tasksDone.contains(task)) continue;

                            if (tasksDone.containsAll(task.getDependencies())) {

                                boolean skip = false;

                                for(Task dependency : task.getDependencies()){
                                    if(taskDoneAt.get(dependency) > durations.get(j))
                                        skip = true;
                                }

                                if(skip)
                                    continue;

                                temp = true;

                                //Create a random double between 0 and 100
                                double rand = r.nextDouble() * 100;

                                //Create an inverse gaussian distribution for the task
                                InverseGaussian invG = new InverseGaussian(task.getEstimatedTime(), task.getLambda());

                                //Calculate the duration at the given random value and add that to duration
                                durations.set(j, durations.get(j) + invG.getDuration(rand));

                                taskDoneAt.put(task, durations.get(j));
                                tasksDone.add(task);

                                if (!(durations.indexOf(Collections.min(durations)) == j))
                                    break;

                            }

                        }

                        if(!temp) {
                            int temp2 = durations.indexOf(Collections.min(durations));
                            List<Double> tempDurations = new ArrayList<>();
                            for (Double d : durations){
                                if (d > durations.get(temp2))
                                    tempDurations.add(d);
                            }
                            if(tempDurations.size() != 0)
                                durations.set(temp2, 0 + Collections.min(tempDurations));
                        }

                    }

                }

                duration += durations.get(durations.indexOf(Collections.max(durations)));

            }else {

                //For each task in the taskList
                for (Task task : taskList) {

                    //Create a random double between 0 and 100
                    double rand = r.nextDouble() * 100;

                    //Create an inverse gaussian distribution for the task
                    InverseGaussian invG = new InverseGaussian(task.getEstimatedTime(), task.getLambda());

                    //Calculate the duration at the given random value and add that to duration
                    duration += invG.getDuration(rand);

                }

            }

        }
        return duration;
    }
}