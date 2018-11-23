@echo off
cls
set day=%date:~,2%
set month=%date:~3,2%
set hour=%time:~,2%
set min=%time:~3,2%
mkdir "tests\%computername%"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=1 --numOfEmps=2  > "tests\%computername%\p1-emp2-D-%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=1 --numOfEmps=10 > "tests\%computername%\p1-emp10-D-%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=2  > "tests\%computername%\p2-emp2-D-%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=10 > "tests\%computername%\p2-emp10-D-%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=20 > "tests\%computername%\p2-emp20-D-%day%-%month%-T-%hour%-%min%.txt"