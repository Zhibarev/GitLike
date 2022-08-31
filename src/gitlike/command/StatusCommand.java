package gitlike.command;

import gitlike.GitLikeException;
import gitlike.Repository;
import gitlike.object.Branch;

import java.util.List;

/**
 * Get information about branches and stage area
 * usage: status
 */
public class StatusCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 1;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();

        return status(repository);
    }

    static String status(Repository repository) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append(String.format("=== Branches ===%n"));
        Branch currentBranch = repository.getCurrentBranch();
        for (Branch branch: repository.getBranchManager().getAllBranches()) {
            if (branch.equals(currentBranch))
                resultBuilder.append("*");
            resultBuilder.append(String.format("%s%n", branch.getName()));
        }

        resultBuilder.append(String.format("%n=== Staged Files ===%n"));
        List<String> additionFiles = repository.getStageManager().getAllAdditionFiles();
        for (String fileName: additionFiles) {
            resultBuilder.append(String.format("%s%n", fileName));
        }

        resultBuilder.append(String.format("%n=== Removed Files ===%n"));
        List<String> removalFiles = repository.getStageManager().getAllRemovalFiles();
        for (String fileName: removalFiles) {
            resultBuilder.append(String.format("%s%n", fileName));
        }

        resultBuilder.append(String.format("%n=== Modifications Not Staged For Commit ===%n"));

        resultBuilder.append(String.format("%n=== Untracked Files ===%n"));

        return resultBuilder.toString();
    }
}
