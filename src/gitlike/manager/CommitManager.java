package gitlike.manager;

import gitlike.Initializable;
import gitlike.Utils;
import gitlike.object.Commit;

import java.io.File;
import java.util.List;

/**
 * Class that provides access to commits and manages commits at the file level.
 */
public class CommitManager implements Initializable {
    private static final String COMMIT_FOLDER_NAME = "commits";

    private final ObjectManager<Commit> objectManager;

    /**
     * Create commit manager
     * @param repositoryFolder repository folder
     */
    public CommitManager(File repositoryFolder) {
        objectManager = new ObjectManager<>(new File(repositoryFolder, COMMIT_FOLDER_NAME));
    }

    /**
     * Save commit
     */
    public void saveCommit(Commit commit) {
        objectManager.save(commit.getId(), commit);
    }

    /**
     * Get commit by id, return null if no commit exist
     * Supports short id
     * <p>
     * Time complexity
     * O(1) - for full id
     * O(number of commits) - for short id
     */
    public Commit getCommit(String id) {
        if (id.length() == Utils.UID_LENGTH) {
            return objectManager.get(id, Commit.class);
        }

        return getCommitByShortId(id);
    }

    /**
     * Get a list of all existing commits
     */
    public List<Commit> getAllCommits() {
        return objectManager.getAll(Commit.class);
    }

    /**
     * Check if the repository contains a commit
     * Supports short id
     * <p>
     * Time complexity:
     * O(1) - for full id
     * O(number of commits) - for short id
     */
    public boolean containsCommit(String id) {
        if (id.length() == Utils.UID_LENGTH) {
            return objectManager.contains(id);
        }

        return getCommitByShortId(id) != null;
    }

    public boolean isInitialized() {
        return objectManager.isInitialized();
    }

    public void initialize() {
        objectManager.initialize();
    }

    /**
     * Get commit by short id.
     * This is an expensive operation!
     * Time complexity: O(number of commits)
     */
    private Commit getCommitByShortId(String id) {
        for (Commit commit: getAllCommits()) {
            String shortId = commit.getId().substring(0, id.length());
            if (shortId.equals(id))
                return commit;
        }
        return null;
    }
}
