package gitlike.command;

import gitlike.*;
import gitlike.manager.*;
import gitlike.object.Blob;
import gitlike.object.Branch;
import gitlike.GitLikeException;
import gitlike.object.Commit;

import java.io.File;
import java.util.Set;

/**
 * Command that can do a few different things:
 *  1. Restore file version from current commit
 *  2. Restore file version from given commit
 *  3. Change the current active branch
 * </p>
 * args:
 *  1. checkout -- [file name]
 *  2. checkout [commit id] -- [file name]
 *  3. checkout [branch name]
 */
public class CheckoutCommand implements Command {

    @Override
    public String execute(String... args) {
        Repository repository = new Repository();

        switch(args.length) {
            case 2:
                checkoutBranch(repository, args[1]);
                break;
            case 3:
                if (!args[1].equals("--"))
                    throw new GitLikeException("Incorrect operands.");
                checkoutFile(repository, args[2]);
                break;
            case 4:
                if (!args[2].equals("--"))
                    throw new GitLikeException("Incorrect operands.");
                checkoutCommitFile(repository, args[1], args[3]);
                break;
            default:
                throw new GitLikeException("Incorrect operands.");
        }

        return null;
    }

    static void checkoutFile(Repository repository, String fileName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        Commit currentCommit = repository.getCurrentCommit();
        checkoutCommitFile(repository, currentCommit.getId(), fileName);
    }

    static void checkoutCommitFile(Repository repository, String commitId, String fileName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        if (!repository.getCommitManager().containsCommit(commitId))
            throw new GitLikeException("No commit with that id exists.");

        Commit commit = repository.getCommitManager().getCommit(commitId);

        if (!commit.containsFile(fileName))
            throw new GitLikeException("File does not exist in that commit");

        for (String blobId: commit.getBlobIds()) {
            Blob blob = repository.getBlobManager().getBlob(blobId);
            if (blob.getFileName().equals(fileName)) {
                File file = new File(fileName);
                Utils.writeContents(file, blob.getContent());
                return;
            }
        }
    }

    static void checkoutBranch(Repository repository, String branchName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        BranchManager branchManager = repository.getBranchManager();
        if (!branchManager.containsBranch(branchName))
            throw new GitLikeException("No such branch exists.");

        String headBranchName = repository.getCurrentBranch().getName();
        if (headBranchName.equals(branchName))
            throw new GitLikeException("No need to checkout the current branch.");

        Branch checkedBranch = branchManager.getBranch(branchName);

        checkoutCommit(repository, checkedBranch.getCommitId());

        repository.setCurrentBranch(branchName);
    }

    static void checkoutCommit(Repository repository, String commitId) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLike directory.");

        CommitManager commitManager = repository.getCommitManager();

        if (!commitManager.containsCommit(commitId))
            throw new GitLikeException("No commit with that id exists.");

        BlobManager blobManager = repository.getBlobManager();
        StageManager stageManager = repository.getStageManager();

        Commit currentCommit = repository.getCurrentCommit();
        Commit checkedCommit = commitManager.getCommit(commitId);

        Set<Blob> checkedBlobs = blobManager.getCommitBlobs(checkedCommit);

        for (Blob blob: checkedBlobs) {
            String fileName = blob.getFileName();
            File workingDirFile = new File(fileName);
            if (workingDirFile.exists()
                    && !currentCommit.containsFile(fileName)
                    && !stageManager.containAdditionFile(fileName))
                throw new GitLikeException("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        for (Blob blob: checkedBlobs) {
            String fileName = blob.getFileName();
            File workingDirFile = new File(fileName);
            Utils.writeContents(workingDirFile, blob.getContent());
        }

        Set<Blob> currentBlobs = blobManager.getCommitBlobs(currentCommit);
        for (Blob blob: currentBlobs) {
            String fileName = blob.getFileName();
            if (!checkedCommit.containsFile(fileName)) {
                File workingDirFile = new File(blob.getFileName());
                workingDirFile.delete();
            }
        }

        stageManager.clear();
    }
}
