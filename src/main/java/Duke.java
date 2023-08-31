import exceptions.*;

import command.Command;

import parser.Parser;
import storage.Storage;

import task.*;
import ui.Ui;

import java.io.IOException;
import java.util.Scanner;

/**
 * A chatbot to keep track of your tasks.
 *
 * @author Andrew Daniel Janong
 */
public class Duke {
    private static final Storage storage = new Storage();
    private static final TaskList tasks = new TaskList();
    private static final Parser parser = new Parser();

    /**
     * Sends a greeting message to the user.
     */
    private static void greetingMessage() {
        Ui.greetUser();
    }

    /**
     * Adds a task to the task list and sends a message of the task added.
     * A task can be a task.ToDo, task.Deadline, or task.Event.
     * @param command
     * @param taskInfo
     */
    private static void addTask(Command command, String taskInfo) throws DukeInvalidDateException {
        Task newTask;

        if (command == Command.TODO) {
            newTask = new ToDo(taskInfo);
        } else if (command == Command.DEADLINE) {
            String[] deadlineInfo = taskInfo.split(" /by ");
            newTask = new Deadline(deadlineInfo[0], deadlineInfo[1]);
        } else { // command == Command.EVENT
            String[] eventInfo = taskInfo.split(" /from ");
            String[] eventTime = eventInfo[1].split(" /to ");
            newTask = new Event(eventInfo[0], eventTime[0], eventTime[1]);
        }

        tasks.addTask(newTask);
    }

    /**
     * Edits a task in the list.
     * Editing a task can be deleting, marking, or unmarking a task.
     * @param command
     * @param taskIndex
     */
    private static void editTask (Command command, int taskIndex) {

        if (command == Command.DELETE) {
            tasks.deleteTask(taskIndex);
        } else if (command == Command.MARK) {
            tasks.markTask(taskIndex);
        } else if (command == Command.UNMARK) {
            tasks.unmarkTask(taskIndex);
        }
    }

    /**
     * Executes single commands.
     * Single commands consists of: Listing all tasks, Printing goodbye message.
     * @param command
     */
    private static void executeSingleCommand(Command command) {
        if (command == Command.LIST) {
            Ui.printLines(tasks.toString());
        } else if (command == Command.BYE) {
            Ui.printExitMessage();
        }
    }

    /**
     * Runs the command from the user input.
     * @param inputs
     * @return A boolean to stop the chatbot on "bye" command
     * @throws DukeException
     */
    private static boolean runCommand(Command command, String[] inputs) throws DukeException {
        if (command == Command.BYE) {
            Ui.printExitMessage();
            storage.writeTasks(tasks);
            return false;
        } else if (command == Command.TODO || command == Command.DEADLINE || command == Command.EVENT) {
            if (inputs.length == 1 || inputs[0].equals("")) {
                throw new DukeEmptyArgumentException("OOPS!!! Argument for this command is invalid.");
            }
            if (command == Command.TODO) {
                addTask(Command.TODO, inputs[1]);
            } else if (command == Command.DEADLINE) {
                addTask(Command.DEADLINE, inputs[1]);
            } else {
                addTask(Command.EVENT, inputs[1]);
            }
        } else if (command == Command.DELETE) {
            if (inputs.length == 1 || inputs[1].equals("")) {
                throw new DukeEmptyArgumentException("OOPS!!! Argument for this command is invalid.");
            }

            try {
                if (!tasks.isValidIndex(Integer.parseInt(inputs[1]))) {
                    throw new DukeInvalidIndexException(Integer.toString(tasks.getSize()));
                }
            } catch (NumberFormatException e) {
                throw new DukeInvalidIndexException(Integer.toString(tasks.getSize()));
            }

            editTask(Command.DELETE, Integer.parseInt(inputs[1]));
        } else if (command == Command.LIST) {
            executeSingleCommand(Command.LIST);
        } else if (command == Command.MARK) {
            if (inputs.length == 1 || inputs[1].equals("")) {
                throw new DukeEmptyArgumentException("OOPS!!! Argument for this command is invalid.");
            }

            try {
                if (!tasks.isValidIndex(Integer.parseInt(inputs[1]))) {
                    throw new DukeInvalidIndexException(Integer.toString(tasks.getSize()));
                }
            } catch (NumberFormatException e) {
                throw new DukeInvalidIndexException(Integer.toString(tasks.getSize()));
            }

            editTask(Command.MARK, Integer.parseInt(inputs[1]));
        } else if (command == Command.UNMARK) {
            if (inputs.length == 1 || inputs[1].equals("")) {
                throw new DukeEmptyArgumentException("OOPS!!! Argument for this command is invalid.");
            }

            try {
                if (!tasks.isValidIndex(Integer.parseInt(inputs[1]))) {
                    throw new DukeInvalidIndexException(Integer.toString(tasks.getSize()));
                }
            } catch (NumberFormatException e) {
                throw new DukeInvalidIndexException(Integer.toString(tasks.getSize()));
            }

            Duke.editTask(Command.UNMARK, Integer.parseInt(inputs[1]));
        } else {
            throw new DukeUnknownCommandException("OOPS!!! Argument for this command is invalid.");
        }

        return true;
    }
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Ui.greetUser();

        try {
            storage.getTasksFromData(tasks);
        } catch (IOException error) {
            Ui.printLines("Shutting down...");
            return;
        }

        while (true) {
            try {
                String userInput = sc.nextLine();
                Command command = parser.parseInput(userInput);
                String[] inputs = userInput.split(" ", 2);

                if (!Duke.runCommand(command, inputs)) break;
                storage.writeTasks(tasks);

            } catch (Exception e) {
                Ui.printLines(e.getMessage());
            }
        }
    }
}
