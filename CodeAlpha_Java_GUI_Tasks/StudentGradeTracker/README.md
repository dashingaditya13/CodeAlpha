# Student Grade Tracker (Java Swing GUI)

## Features
- Add students with unique roll numbers
- Enter marks as comma-separated values, e.g. `78, 85, 91`
- Calculates average, highest and lowest marks
- Displays PASS/FAIL using a 40% average threshold
- Delete selected student and view class report
- Input validation and class summary

## Requirements
- JDK 8 or later
- No external libraries required

## Run
```bash
javac -d out src/StudentGradeTracker.java
java -cp out StudentGradeTracker
```

Records are kept in memory for the current run; closing the app clears them.
