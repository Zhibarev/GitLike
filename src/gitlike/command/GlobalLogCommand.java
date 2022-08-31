package gitlike.command;

import gitlike.GitLikeException;
import gitlike.Repository;
import gitlike.object.Commit;

import java.util.List;

/**
 * Command to get information about all commits
 * usage: global-log
 */
public class GlobalLogCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 1;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        return globalLog(repository);
    }

    static String globalLog(Repository repository) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        StringBuilder result = new StringBuilder();
        List<Commit> allCommits = repository.getCommitManager().getAllCommits();
        for (int i = 0; i < allCommits.size(); i++) {
            result.append(allCommits.get(i));
            if (i != allCommits.size() - 1)
                result.append(System.lineSeparator());
        }

        return result.toString();
    }

}
