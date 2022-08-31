package gitlike.command;

import gitlike.Utils;
import gitlike.GitLikeException;
import gitlike.manager.StageManager;
import gitlike.object.Blob;
import gitlike.Repository;
import gitlike.object.Commit;

import java.io.File;

/**
 * The command for adding a file to the staging area.
 * args: add [file name]
 */
public class AddCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String fileName = args[1];

        add(repository, fileName);

        return null;
    }

    static void add(Repository repository, String fileName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        File file = new File(fileName);

        if (!file.exists())
            throw new GitLikeException("File does not exist.");

        StageManager stageManager = repository.getStageManager();

        stageManager.removeRemovalFile(fileName);

        String content = Utils.readContentsAsString(file);
        Blob blob = new Blob(fileName, content);

        Commit currentCommit = repository.getCurrentCommit();

        if (currentCommit.containsBlob(blob.getId())) {
            stageManager.removeAdditionFile(fileName);
        }
        else {
            stageManager.addAdditionFile(fileName, content);
        }
    }
}
