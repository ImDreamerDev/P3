package dk.aau.ds304e18.models;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

public class Worker {
    public String Name;
    public int WorkingHours;
    public Task CurrentTask;


    Worker(String name, int workingHours) {
        Name = name;
        WorkingHours = workingHours;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getWorkingHours() {
        return WorkingHours;
    }

    public void setWorkingHours(int workingHours) {
        WorkingHours = workingHours;
    }

}
