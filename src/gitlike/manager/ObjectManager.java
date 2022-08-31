package gitlike.manager;

import gitlike.GitLikeException;
import gitlike.Initializable;
import gitlike.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that encapsulate working with objects at the file level.
 * All other managers are a wrapper over this class with a different interface and partial functionality.
 */
public class ObjectManager<T extends Serializable> implements Initializable {
    protected File objectFolder;

    /**
     * Create object manager
     * @param objectFolder the folder that contains the objects for which the class is responsible.
     */
    public ObjectManager(File objectFolder) {
        this.objectFolder = objectFolder;
    }

    /**
     * Serialize an object and save to objectFolder
     */
    public void save(String fileName, T object) {
        File file = new File(objectFolder, fileName);
        Utils.writeObject(file, object);
    }

    /**
     * Get object from objectFolder
     */
    public T get(String fileName,  Class<T> expectedClass) {
        File file = new File(objectFolder, fileName);
        if (!file.exists())
            return null;
        return Utils.readObject(file, expectedClass);
    }

    /**
     * Delete file from objectFolder
     * returns whether the deletion was successful
     */
    public boolean delete(String fileName) {
        File file = new File(objectFolder, fileName);
        return file.delete();
    }

    /**
     * Check if the objectFolder contains a file
     */
    public boolean contains(String fileName) {
        File file = new File(objectFolder, fileName);
        return file.exists();
    }

    /**
     * Get all objects stored in the objectFolder
     */
    public List<T> getAll(Class<T> expectedClass) {
        List<T> objectList = new ArrayList<>();

        List<String> files = Utils.plainFilenamesIn(objectFolder);

        if (files == null)
            return objectList;

        for (String fileName: files) {
            File file = new File(objectFolder, fileName);
            T object = Utils.readObject(file, expectedClass);
            objectList.add(object);
        }

        return objectList;
    }

    /**
     * Get the names of all files stored in the objectFolder
     */
    public List<String> getAllFileNames() {
        return Utils.plainFilenamesIn(objectFolder);
    }

    /**
     * Clear the object folder
     */
    public void clear() {
        List<String> files = Utils.plainFilenamesIn(objectFolder);

        if (files == null)
            return;

        for (String fileName: files) {
            File file = new File(objectFolder, fileName);
            if (!file.delete())
                throw new GitLikeException("Unable to delete file " + file.getAbsolutePath());
        }
    }

    /**
     * Check if manager is initialized
     */
    public boolean isInitialized() {
        return objectFolder.exists();
    }


    /**
     * Initialize manager
     */
    public void initialize() {
        if (!objectFolder.mkdirs())
            throw new GitLikeException("Unable to create folder " + objectFolder.getAbsolutePath());
    }
}
