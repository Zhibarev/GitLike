package gitlike.command;

import gitlike.GitLikeException;

/**
 * Basic interface for all commands
 */
public interface Command {

    /**
     * Execute command with given arguments and returns the result string on success
     * The string can be null if the command has nothing to report
     *
     * @throws GitLikeException if something goes wrong
     */
    String execute(String... args);

}
