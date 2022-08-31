package gitlike.manager;

import gitlike.GitLikeException;
import gitlike.Initializable;
import gitlike.object.Branch;

import java.io.File;
import java.util.List;

/**
 * Class that provides access to branches and manages branches at the file level.
 */
public class BranchManager implements Initializable {
    private static final String BRANCH_FOLDER_NAME = "branches";

    private final ObjectManager<Branch> objectManager;

    /**
     * Create branch manager
     * @param repositoryFolder repository folder
     */
    public BranchManager(File repositoryFolder) {
        objectManager = new ObjectManager<>(new File(repositoryFolder, BRANCH_FOLDER_NAME));
    }

    /**
     * Save branch to repository
     * This method is also used to replace the commit referenced by the branch
     */
    public void saveBranch(Branch branch) {
        objectManager.save(branch.getName(), branch);
    }

    /**
     * Remove branch from repository
     *
     * @throws GitLikeException if branch is not exist
     */
    public void deleteBranch(String branchName) {
        if (!objectManager.delete(branchName))
            throw new GitLikeException("A branch with that name does not exist.");
    }

    /**
     * Check if a branch with the given name exists
     */
    public boolean containsBranch(String branchName) {
        return objectManager.contains(branchName);
    }

    /**
     * Get a branch with the given name, return null is no branch exist
     */
    public Branch getBranch(String branchName) {
        return objectManager.get(branchName, Branch.class);
    }

    /**
     * Get list of all existing branches
     */
    public List<Branch> getAllBranches() {
        return objectManager.getAll(Branch.class);
    }

    public boolean isInitialized() {
        return objectManager.isInitialized();
    }

    public void initialize() {
        objectManager.initialize();
    }
}
