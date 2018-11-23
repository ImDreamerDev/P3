@echo off
cls
set day=%date:~,2%
set month=%date:~3,2%
set hour=%time:~,2%
set min=%time:~3,2%
mkdir "tests\%computername%\p1\emp2"
mkdir "tests\%computername%\p1\emp5"
mkdir "tests\%computername%\p1\emp8"
mkdir "tests\%computername%\p1\emp10"

mkdir "tests\%computername%\p2\emp2"
mkdir "tests\%computername%\p2\emp5"
mkdir "tests\%computername%\p2\emp8"
mkdir "tests\%computername%\p2\emp10"
mkdir "tests\%computername%\p2\emp15"

mkdir "tests\%computername%\p3\emp2"
mkdir "tests\%computername%\p3\emp5"
mkdir "tests\%computername%\p3\emp8"
mkdir "tests\%computername%\p3\emp10"
mkdir "tests\%computername%\p3\emp15"

echo P1

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=1 --numOfEmps=2  > "tests\%computername%\p1\emp2\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=1 --numOfEmps=5 > "tests\%computername%\p1\emp5\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=1 --numOfEmps=8 > "tests\%computername%\p1\emp8\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=1 --numOfEmps=10 > "tests\%computername%\p1\emp10\%day%-%month%-T-%hour%-%min%.txt"


echo P2

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=2  > "tests\%computername%\p2\emp2\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=5 > "tests\%computername%\p2\emp5\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=8 > "tests\%computername%\p2\emp8\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=10 > "tests\%computername%\p2\emp10\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=2 --numOfEmps=15 > "tests\%computername%\p2\emp15\%day%-%month%-T-%hour%-%min%.txt"


echo P3

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=3 --numOfEmps=2  > "tests\%computername%\p3\emp2\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=3 --numOfEmps=5 > "tests\%computername%\p3\emp5\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=3 --numOfEmps=8 > "tests\%computername%\p3\emp8\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=3 --numOfEmps=10 > "tests\%computername%\p3\emp10\%day%-%month%-T-%hour%-%min%.txt"

java -jar --module-path "%PATH_TO_FX%" --add-modules=javafx.controls --add-modules=javafx.fxml target/P3-1.0-SNAPSHOT-jar-with-dependencies.jar --username=ct --password=1 --projectId=3 --numOfEmps=15 > "tests\%computername%\p3\emp15\%day%-%month%-T-%hour%-%min%.txt"