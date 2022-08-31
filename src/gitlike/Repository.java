package gitlike;

import gitlike.manager.BlobManager;
import gitlike.manager.BranchManager;
import gitlike.manager.CommitManager;
import gitlike.manager.StageManager;
import gitlike.object.Branch;
import gitlike.object.Commit;

import java.io.File;

/**
 * Class that provides access to control all elements of the repository
 */
public class Repository implements Initializable {
    private static final String REPOSITORY_FOLDER_NAME = ".gitlike";
    private static final String CURRENT_BRANCH_FILE_NAME = "currentBranch";

    private final File currentBranchFile;
    private final File repositoryFolder;

    private final CommitManager commitManager;
    private final BlobManager blobManager;
    private final BranchManager branchManager;
    private final StageManager stageManager;

    /**
     * Create a repository in the starting folder
     */
    public Repository() {
        repositoryFolder = new File(REPOSITORY_FOLDER_NAME);
        currentBranchFile = new File(repositoryFolder, CURRENT_BRANCH_FILE_NAME);

        commitManager = new CommitManager(repositoryFolder);
        blobManager = new BlobManager(repositoryFolder);
        branchManager = new BranchManager(repositoryFolder);
        stageManager = new StageManager(repositoryFolder);
    }

    /**
     * Get the commit manager
     */
    public CommitManager getCommitManager() {
        return commitManager;
    }

    /**
     * Get the branch manager
     */
    public BranchManager getBranchManager() {
        return branchManager;
    }

    /**
     * Get the stage manager
     */
    public StageManager getStageManager() {
        return stageManager;
    }

    /**
     * Get the blob manager
     */
    public BlobManager getBlobManager() {
        return blobManager;
    }

    /**
     * Get the current commit
     */
    public Commit getCurrentCommit() {
        String commitId = getCurrentBranch().getCommitId();
        return commitManager.getCommit(commitId);
    }

    /**
     * Get head branch
     */
    public Branch getCurrentBranch() {
        String branchName = Utils.readContentsAsString(currentBranchFile);
        return branchManager.getBranch(branchName);
    }

    /**
     * Set the head branch
     */
    public void setCurrentBranch(String branchName) {
        Utils.writeContents(currentBranchFile, branchName);
    }

    public void initialize() {
        if (!repositoryFolder.mkdir())
            throw new GitLikeException("Unable to create repository folder");
        commitManager.initialize();
        blobManager.initialize();
        branchManager.initialize();
        stageManager.initialize();
    }

    public boolean isInitialized() {
        return commitManager.isInitialized()
                && blobManager.isInitialized()
                && branchManager.isInitialized()
                && stageManager.isInitialized();
    }
}
