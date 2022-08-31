package gitlike.command;

import gitlike.Repository;
import gitlike.object.Branch;
import gitlike.GitLikeException;

/**
 * Check out all the files tracked by the given commit. Also moves the current branch's head to that commit node.
 * usage: reset [commit id]
 */
public class ResetCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String commitId = args[1];

        reset(repository, commitId);

        return null;
    }

    static void reset(Repository repository, String commitId) {
        CheckoutCommand.checkoutCommit(repository, commitId);

        Branch branch = repository.getCurrentBranch();
        repository.getBranchManager().saveBranch(new Branch(branch.getName(), commitId));
    }

}
