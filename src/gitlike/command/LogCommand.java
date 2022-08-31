package gitlike.command;

import gitlike.GitLikeException;
import gitlike.Repository;
import gitlike.object.Commit;
import gitlike.manager.CommitManager;

/**
 * Command to get information about the head commit and all its ancestors
 * usage: log
 */
public class LogCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 1;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();

        return log(repository);
    }

    static String log(Repository repository) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        StringBuilder result = new StringBuilder();

        CommitManager commitManager = repository.getCommitManager();
        Commit commit = repository.getCurrentCommit();

        while (commit.getParentId() != null) {
            result.append(commit).append(System.lineSeparator());
            commit = commitManager.getCommit(commit.getParentId());
        }
        result.append(commit);

        return result.toString();
    }
}
