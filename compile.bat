del bin\com\gui\*.class
del bin\com\server\*.class
javac -sourcepath src src/com/gui/gui.java -d bin -cp ".;lib/jackson-annotations-2.8.0.jar;lib/jackson-core-2.8.1.jar;lib/jackson-databind-2.8.5.jar"
copy "src\com\gui\host.fxml" "bin\com\gui\host.fxml"
copy "src\com\gui\main.fxml" "bin\com\gui\main.fxml"
pause