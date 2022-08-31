package gitlike.command;

import gitlike.GitLikeException;
import gitlike.Repository;
import gitlike.object.Commit;

/**
 * Find all commits with a given log message
 * usage: find [log message]
 */
public class FindCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String logMessage = args[1];

        return find(repository, logMessage);
    }

    static String find(Repository repository, String logMessage) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        StringBuilder resultBuilder = new StringBuilder();

        for (Commit commit: repository.getCommitManager().getAllCommits()) {
            if (commit.getLogMessage().equals(logMessage))
                resultBuilder.append(commit.getId()).append(System.lineSeparator());
        }

        String result = resultBuilder.toString();

        if (result.isBlank())
            throw new GitLikeException("Found no commit with that message.");

        return result;
    }

}
