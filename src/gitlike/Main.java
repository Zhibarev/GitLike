package gitlike;

import gitlike.command.*;

import java.util.HashMap;
import java.util.Map;

/** Driver class for GitLike, the tiny version-control system.
 */
public class Main {

    /** Usage: java gitlike.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        Map<String, Command> commandMap = new HashMap<>();

        commandMap.put("add", new AddCommand());
        commandMap.put("branch", new BranchCommand());
        commandMap.put("checkout", new CheckoutCommand());
        commandMap.put("commit", new CommitCommand());
        commandMap.put("find", new FindCommand());
        commandMap.put("global-log", new GlobalLogCommand());
        commandMap.put("init", new InitCommand());
        commandMap.put("log", new LogCommand());
        commandMap.put("merge", new MergeCommand());
        commandMap.put("reset", new ResetCommand());
        commandMap.put("rm", new RmCommand());
        commandMap.put("rm-branch", new RmBranchCommand());
        commandMap.put("status", new StatusCommand());

        if (args.length == 0) {
            System.out.println("Please enter a GitLike command.");
            return;
        }

        String command = args[0];

        if (!commandMap.containsKey(command)) {
            System.out.println("No GitLike command with that name exists.");
            return;
        }

        try {
            String result = commandMap.get(command).execute(args);
            if (result != null)
                System.out.println(result);
        } catch (GitLikeException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
