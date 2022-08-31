package gitlike.command;

import gitlike.Repository;
import gitlike.manager.BranchManager;
import gitlike.GitLikeException;

/**
 * Command to delete a branch
 * usage: rm-branch [branch name]
 */
public class RmBranchCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String branchName = args[1];

        rmBranch(repository, branchName);

        return null;
    }

    static void rmBranch(Repository repository, String branchName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        BranchManager branchManager = repository.getBranchManager();

        if (!branchManager.containsBranch(branchName))
            throw new GitLikeException("A branch with that name does not exist.");

        String currentBranchName = repository.getCurrentBranch().getName();
        if (currentBranchName.equals(branchName))
            throw new GitLikeException("Cannot remove the current branch.");

        branchManager.deleteBranch(branchName);
    }
}
