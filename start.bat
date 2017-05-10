@echo off
color 6
echo Starting explorviz ui backend!
echo Press ctrl+c to terminate the program.
mvn exec:java -Dexec.classpathScope=compile
