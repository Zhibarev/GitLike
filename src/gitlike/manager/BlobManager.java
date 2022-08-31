package gitlike.manager;

import gitlike.Initializable;
import gitlike.object.Commit;
import gitlike.object.Blob;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that provides access to blobs and manages blobs at the file level.
 */
public class BlobManager implements Initializable {
    private static final String BLOB_FOLDER_NAME = "blobs";

    private final ObjectManager<Blob> objectManager;

    /**
     * Create blob manager
     * @param repositoryFolder repository folder
     */
    public BlobManager(File repositoryFolder) {
        objectManager = new ObjectManager<>(new File(repositoryFolder, BLOB_FOLDER_NAME));
    }

    /**
     * Save blob to repository
     */
    public void saveBlob(Blob blob) {
        objectManager.save(blob.getId(), blob);
    }

    /**
     * Get blob from repository.
     * Returns null if no blob was found.
     */
    public Blob getBlob(String id) {
        return objectManager.get(id, Blob.class);
    }

    /**
     * Get a set of all blobs for a given commit
     */
    public Set<Blob> getCommitBlobs(Commit commit) {
        Set<Blob> blobs = new HashSet<>();
        for (String blobId: commit.getBlobIds()) {
            blobs.add(getBlob(blobId));
        }
        return blobs;
    }

    public boolean isInitialized() {
        return objectManager.isInitialized();
    }

    public void initialize() {
        objectManager.initialize();
    }
}
