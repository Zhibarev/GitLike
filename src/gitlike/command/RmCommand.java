package gitlike.command;

import gitlike.GitLikeException;
import gitlike.Repository;
import gitlike.manager.StageManager;
import gitlike.object.Commit;

import java.io.File;

/**
 * Command to unstage files
 * usage: rm [file name]
 */
public class RmCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String fileName = args[1];

        rm(repository, fileName);

        return null;
    }

    static void rm(Repository repository, String fileName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        Commit currentCommit = repository.getCurrentCommit();
        StageManager stageManager = repository.getStageManager();

        boolean isStaged = stageManager.containAdditionFile(fileName);
        boolean isTracked = currentCommit.containsFile(fileName);

        if (!isStaged && !isTracked)
            throw new GitLikeException("No reason to remove the file.");

        if (isStaged)
            stageManager.removeAdditionFile(fileName);

        if (isTracked) {
            File file = new File(fileName);
            file.delete();
            stageManager.addRemovalFile(fileName);
        }
    }
}
