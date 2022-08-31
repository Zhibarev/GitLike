package gitlike.manager;

import gitlike.Initializable;
import gitlike.object.Blob;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that gives access to control the stage area.
 */
public class StageManager implements Initializable {
    private static final String STAGE_FOLDER_NAME = "staged";
    private static final String ADDED_FOLDER_NAME = "added";
    private static final String REMOVED_FOLDER_NAME = "removed";

    private final ObjectManager<Blob> addedObjectsManager;
    private final ObjectManager<String> removedObjectsManager;

    /**
     * Create a stage manager
     */
    public StageManager(File repositoryFolder) {
        File stageFolder = new File(repositoryFolder, STAGE_FOLDER_NAME);
        addedObjectsManager = new ObjectManager<>(new File(stageFolder, ADDED_FOLDER_NAME));
        removedObjectsManager = new ObjectManager<>(new File(stageFolder, REMOVED_FOLDER_NAME));
    }

    /**
     * Staging the file for addition
     */
    public void addAdditionFile(String fileName, String content) {
        addedObjectsManager.save(fileName, new Blob(fileName, content));
    }

    /**
     * Staging the file for removal
     */
    public void addRemovalFile(String fileName) {
        removedObjectsManager.save(fileName, "");
    }

    /**
     * Remove addition file from staging area
     */
    public void removeAdditionFile(String fileName) {
        addedObjectsManager.delete(fileName);
    }

    /**
     * Remove removal file from staging area
     */
    public void removeRemovalFile(String fileName) {
        removedObjectsManager.delete(fileName);
    }

    /**
     * Get the blob that match the addition file from staging area
     */
    public Blob getAdditionBlob(String fileName) {
        return addedObjectsManager.get(fileName, Blob.class);
    }

    /**
     * Get a set of blobs that match all additional files in the staging area
     */
    public Set<Blob> getAllAdditionBlobs() {
        return new HashSet<>(addedObjectsManager.getAll(Blob.class));
    }

    /**
     * Get a list of the names of all addition files in the staging area
     */
    public List<String> getAllAdditionFiles() {
        return addedObjectsManager.getAllFileNames();
    }

    /**
     * Get a list of the names of all removal files in the staging area
     */
    public List<String> getAllRemovalFiles() {
        return removedObjectsManager.getAllFileNames();
    }

    /**
     * Check that the staging area contains an addition file
     */
    public boolean containAdditionFile(String fileName) {
        return addedObjectsManager.contains(fileName);
    }

    /**
     * Check that the staging area contains a removal file
     */
    public boolean containRemovalFile(String fileName) {
        return removedObjectsManager.contains(fileName);
    }

    /**
     * Clear the staging area
     */
    public void clear() {
        addedObjectsManager.clear();
        removedObjectsManager.clear();
    }

    public void initialize() {
        addedObjectsManager.initialize();
        removedObjectsManager.initialize();
    }

    public boolean isInitialized() {
        return addedObjectsManager.isInitialized() && removedObjectsManager.isInitialized();
    }

}
