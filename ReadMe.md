# QuiZam

QuiZam is an interactive quiz application focused on Zambian general knowledge, history, geography, and civic education. It's built using Java Swing and provides an engaging user interface for users to test their knowledge about Zambia.

## Features

- Multiple quiz categories: General Knowledge, History, Geography, and Civic Education
- Randomized questions for each category
- Timed questions with a 10-second countdown
- Progress tracking during the quiz
- High score system for each category
- Pause and resume functionality
- Fun facts about Zambia on the welcome screen

## Requirements

- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE) for running the compiled application

## How to Run

1. Compile the Java file:
   ```
   javac Main.java
   ```

2. Run the compiled class:
   ```
   java Main
   ```

## How to Play

1. Launch the application.
2. On the welcome screen, choose "Play Quiz" to start a new game.
3. Select a category from the available options.
4. Answer the questions within the given time limit by clicking on the correct option.
5. After completing the quiz, you'll see your score and have the option to replay, change category, or end the quiz.

## High Scores

- High scores are automatically saved for each category.
- You can view the high scores by selecting "View High Scores" on the welcome screen.

## Additional Information

- The application uses a file named `high_score.txt` to store and retrieve high scores.
- Questions are randomized for each playthrough to provide a varied experience.
- The pause feature allows users to temporarily stop the quiz and resume later.

## Contributing

Feel free to fork this project and submit pull requests with improvements or additional features. Some ideas for enhancements include:

- Adding more categories and questions
- Implementing difficulty levels
- Creating a question editor interface
- Adding sound effects and background music

## License

This project is open-source and available under the MIT License.