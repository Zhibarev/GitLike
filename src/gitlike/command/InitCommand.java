package gitlike.command;

import gitlike.GitLikeException;
import gitlike.Repository;
import gitlike.object.Branch;
import gitlike.object.Commit;

/**
 * Command to create a new GitLike version-control system in the current directory
 * usage: init
 */
public class InitCommand implements Command {

    @Override
    public String execute(String... args) {

        final int NUM_ARGS = 1;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();

        init(repository);

        return null;
    }

    static void init(Repository repository) {
        if (repository.isInitialized())
            throw new GitLikeException("A GitLike version-control system already exists in the current directory.");

        repository.initialize();

        String logMessage = "initial commit";
        Commit initialCommit = new Commit(logMessage, null, null, null, 0);
        repository.getCommitManager().saveCommit(initialCommit);

        String branchName = "master";
        Branch masterBranch = new Branch(branchName, initialCommit.getId());
        repository.getBranchManager().saveBranch(masterBranch);
        repository.setCurrentBranch(masterBranch.getName());
    }

}
