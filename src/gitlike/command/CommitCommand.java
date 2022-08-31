package gitlike.command;

import gitlike.GitLikeException;
import gitlike.manager.BlobManager;
import gitlike.Repository;
import gitlike.manager.StageManager;
import gitlike.object.Blob;
import gitlike.object.Branch;
import gitlike.object.Commit;

import java.util.HashSet;
import java.util.Set;

/**
 * Command to commit changes
 * usage: commit [message]
 */
public class CommitCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        String logMessage = args[1];

        Repository repository = new Repository();

        commit(repository, logMessage);

        return null;
    }

    static void commit(Repository repository, String logMessage, String mergedParentId) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        if (logMessage.isBlank())
            throw new GitLikeException("Please enter a commit message.");

        StageManager stageManager = repository.getStageManager();
        Set<Blob> additionBlobs = stageManager.getAllAdditionBlobs();
        Set<String> removalFileNames = new HashSet<>(stageManager.getAllRemovalFiles());

        if (additionBlobs.isEmpty() && removalFileNames.isEmpty())
            throw new GitLikeException("No changes added to the commit.");

        BlobManager blobManager = repository.getBlobManager();
        Commit oldCommit = repository.getCurrentCommit();
        Set<Blob> oldBlobs = blobManager.getCommitBlobs(oldCommit);

        Set<Blob> newBlobs = new HashSet<>();

        for (Blob blob: additionBlobs) {
            blobManager.saveBlob(blob);
            newBlobs.add(blob);
        }

        Set<String> additionBlobNames = new HashSet<>();
        for (Blob blob: additionBlobs) {
            additionBlobNames.add(blob.getFileName());
        }

        for (Blob blob: oldBlobs) {
            if (!additionBlobNames.contains(blob.getFileName()) && !removalFileNames.contains(blob.getFileName()))
                newBlobs.add(blob);
        }

        Commit newCommit = new Commit(logMessage, oldCommit.getId(), mergedParentId, newBlobs);
        repository.getCommitManager().saveCommit(newCommit);

        Branch headBranch = new Branch(repository.getCurrentBranch().getName(), newCommit.getId());
        repository.getBranchManager().saveBranch(headBranch);

        stageManager.clear();
    }

    static void commit(Repository repository, String logMessage) {
        commit(repository, logMessage, null);
    }

}
