import exceptions.DukeEmptyArgumentException;
import exceptions.DukeException;
import exceptions.DukeInvalidIndexException;
import exceptions.DukeUnknownCommandException;

import command.Command;

import task.Deadline;
import task.Event;
import task.Task;
import task.ToDo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A chatbot to keep track of your tasks.
 *
 * @author Andrew Daniel Janong
 */
public class Duke {

    /**
     * The array of tasks to keep track of the users' tasks.
     */
    private static final List<Task> tasks = new ArrayList<>();

    /**
     * A line used as a line break.
     */
    private static final String LINE = "____________________________________________________________";

    /**
     * Sends a greeting message to the user.
     */
    private static void greetingMessage() {
        System.out.println("\t" + Duke.LINE + "\n" +
                "\t Hello I'm ADJ\n" +
                "\t What can I do for you?\n\t" +
                Duke.LINE);
    }

    /**
     * Adds a task to the task list and sends a message of the task added.
     * A task can be a task.ToDo, task.Deadline, or task.Event.
     * @param command
     * @param taskInfo
     */
    private static void addTask(Command command, String taskInfo) {
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

        Duke.tasks.add(newTask);

        System.out.println("\t Got it. I've added this task:\n" +
                "\t\t" + newTask + "\n" +
                "\t Now you have " + Duke.tasks.size() + " tasks in your list. Good luck!");
    }

    /**
     * Edits a task in the list.
     * Editing a task can be deleting, marking, or unmarking a task.
     * @param command
     * @param taskIndex
     */
    private static void editTask (Command command, int taskIndex) {
        Task task = Duke.tasks.get(taskIndex - 1);

        if (command == Command.DELETE) {
            tasks.remove(taskIndex - 1);

            System.out.println("\t Noted. I've removed this task:\n" +
                    "\t\t" + task + "\n" +
                    "\t Now you have " + Duke.tasks.size() + " tasks in your list. Good luck!");
        } else if (command == Command.MARK) {
            task.markAsDone();

            System.out.println("\t Nice job! I've marked this task as done:");
            System.out.println("\t\t " + task);
        } else if (command == Command.UNMARK) {
            task.markAsNotDone();

            System.out.println("\t What happened? I've marked this task as not done yet:");
            System.out.println("\t\t " + task);
        }
    }

    /**
     * Executes single commands.
     * Single commands consists of: Listing all tasks, Printing goodbye message.
     * @param command
     */
    private static void executeSingleCommand(Command command) {
        if (command == Command.LIST) {
            System.out.println("\t Here are the tasks in your list:");
            for (int i = 0; i < Duke.tasks.size(); i++) {
                System.out.println("\t " + (i + 1) + "." + Duke.tasks.get(i));
            }
        } else if (command == Command.BYE) {
            System.out.println("\t Bye. Hope to see you again soon!");
        }
    }

    /**
     * Runs the command from the user input.
     * @param inputs
     * @return A boolean to stop the chatbot on "bye" command
     * @throws DukeException
     */
    private static boolean runCommand(String[] inputs) throws DukeException {
        String command = inputs[0].toLowerCase();

        System.out.println("\t" + Duke.LINE);

        if (command.equals("bye")) {
            Duke.executeSingleCommand(Command.BYE);
            System.out.println("\t" + Duke.LINE);
            return false;
        } else if (command.equals("todo") || command.equals("deadline") || command.equals("event")) {
            if (inputs.length == 1 || inputs[1] == "") {
                throw new DukeEmptyArgumentException(command);
            }

            if (command.equals("todo")) {
                Duke.addTask(Command.TODO, inputs[1]);
            } else if (command.equals(("deadline"))) {
                Duke.addTask(Command.DEADLINE, inputs[1]);
            } else {
                Duke.addTask(Command.EVENT, inputs[1]);
            }

        } else if (command.equals("delete")) {
            if (inputs.length == 1 || inputs[1] == "") {
                throw new DukeEmptyArgumentException(command);
            }

            try {
                if (Integer.parseInt(inputs[1]) <= 0 || Integer.parseInt(inputs[1]) > tasks.size()) {
                    throw new DukeInvalidIndexException(Integer.toString(tasks.size()));
                }
            } catch (NumberFormatException e) {
                throw new DukeInvalidIndexException(Integer.toString(tasks.size()));
            }

            Duke.editTask(Command.DELETE, Integer.parseInt(inputs[1]));
        } else if (command.equals("list")) {
            Duke.executeSingleCommand(Command.LIST);
        } else if (command.equals("mark")) {
            if (inputs.length == 1 || inputs[1] == "") {
                throw new DukeEmptyArgumentException(command);
            }

            try {
                if (Integer.parseInt(inputs[1]) <= 0 || Integer.parseInt(inputs[1]) > tasks.size()) {
                    throw new DukeInvalidIndexException(Integer.toString(tasks.size()));
                }
            } catch (NumberFormatException e) {
                throw new DukeInvalidIndexException(Integer.toString(tasks.size()));
            }

            Duke.editTask(Command.MARK, Integer.parseInt(inputs[1]));
        } else if (command.equals("unmark")) {
            if (inputs.length == 1 || inputs[1] == "") {
                throw new DukeEmptyArgumentException(command);
            }

            try {
                if (Integer.parseInt(inputs[1]) <= 0 || Integer.parseInt(inputs[1]) > tasks.size()) {
                    throw new DukeInvalidIndexException(Integer.toString(tasks.size()));
                }
            } catch (NumberFormatException e) {
                throw new DukeInvalidIndexException(Integer.toString(tasks.size()));
            }

            Duke.editTask(Command.UNMARK, Integer.parseInt(inputs[1]));
        } else {
            throw new DukeUnknownCommandException(command);
        }

        System.out.println("\t" + Duke.LINE);

        return true;
    }
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Duke.greetingMessage();

        while (true) {
            try {
                String userInput = sc.nextLine();
                String[] inputs = userInput.split(" ", 2);

                if (!Duke.runCommand(inputs)) break;

            } catch (Exception e) {
                System.out.println("\t " + e.getMessage());
                System.out.println("\t" + Duke.LINE);
            }
        }

    }
}
