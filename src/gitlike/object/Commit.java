package gitlike.object;

import gitlike.Utils;

import java.io.Serializable;
import java.util.*;

public class Commit implements Serializable {

    /**
     * SHA-1 commit identifier
     */
    private final String id;

    /**
     * SHA-1 blob identifiers
     */
    private final Set<String> blobIds;

    /**
     * Commit also store blob names
     * Because it's sometimes useful to check if a file exists in a commit without loading its content
     */
    private final Set<String> blobFileNames;

    private final String logMessage;
    private final String parentId;
    private final String mergedParentId;
    private final long timestamp;

    /**
     * Create commit with current date
     */
    public Commit(String logMessage, String parentId, String mergedParentId, Set<Blob> blobs) {
        this(logMessage, parentId, mergedParentId, blobs, System.currentTimeMillis());
    }

    /**
     * Create commit with given date
     */
    public Commit(String logMessage, String parentId, String mergedParentId, Set<Blob> blobs, long timestamp) {
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.mergedParentId = mergedParentId;
        this.logMessage = logMessage;

        StringBuilder blobsSHABuilder = new StringBuilder();
        this.blobIds = new HashSet<>();
        this.blobFileNames = new HashSet<>();
        if (blobs != null) {
            for (Blob blob : blobs) {
                blobIds.add(blob.getId());
                blobFileNames.add(blob.getFileName());
                blobsSHABuilder.append(blob.getId());
            }
        }
        String blobsSHA = blobsSHABuilder.toString();

        String parentForSHA = parentId;
        if (parentForSHA == null)
            parentForSHA = "";

        String mergedParentForSHA = parentId;
        if (mergedParentForSHA == null)
            mergedParentForSHA = "";

        this.id = Utils.sha1(Long.toString(this.timestamp), parentForSHA, mergedParentForSHA, this.logMessage,
                blobsSHA);
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getMergedParentId() {
        return mergedParentId;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public Set<String> getBlobIds() {
        return blobIds;
    }

    public boolean containsBlob(String blobId) {
        return blobIds.contains(blobId);
    }

    public boolean containsFile(String fileName) {
        return blobFileNames.contains(fileName);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append(String.format("===%n"));
        result.append(String.format("commit %s%n", id));

        if (mergedParentId != null) {
            final int NUM_SYMBOLS = 7;
            String firstParentSHA = parentId.substring(0, NUM_SYMBOLS);
            String secondParentSHA = mergedParentId.substring(0, NUM_SYMBOLS);
            result.append(String.format("Merge: %s %s%n", firstParentSHA, secondParentSHA));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        result.append(String.format("Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz%n", calendar));

        result.append(String.format("%s%n", logMessage));

        return result.toString();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Commit another = (Commit) obj;
        return id.equals(another.id);
    }
}
