# sudoku-solver

This application has been built with [Spring Boot](https://spring.io/projects/spring-boot), 
with the requests handled by the "Controller" classes.
The HTML view has been built with [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html).  

## Development

PuzzleController is the main resource controller.

## Building and Running the Web Application

Run the web application as follows:

    ./mvnw spring-boot:run

Alternatively, build a standalone JAR and execute it:

    ./mvnw clean package
    java -jar target/sudoku-solver-0.0.1-SNAPSHOT.jar

The service will accept requests at http://localhost:8080/, with "puzzles" as 
the main resource. E.g. http://localhost:8080/puzzles?level=easy.
