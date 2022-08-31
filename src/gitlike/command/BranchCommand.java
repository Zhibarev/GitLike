package gitlike.command;

import gitlike.Repository;
import gitlike.object.Branch;
import gitlike.GitLikeException;

/**
 * Command to create a new branch
 * args: branch [branch name]
 */
public class BranchCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String branchName = args[1];

        branch(repository, branchName);

        return null;
    }

    static void branch(Repository repository, String branchName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        if (repository.getBranchManager().containsBranch(branchName))
            throw new GitLikeException("A branch with that name already exists.");

        Branch branch = new Branch(branchName, repository.getCurrentCommit().getId());
        repository.getBranchManager().saveBranch(branch);
    }

}
