package gitlike.command;

import gitlike.Repository;
import gitlike.Utils;
import gitlike.manager.StageManager;
import gitlike.object.Blob;
import gitlike.object.Branch;
import gitlike.GitLikeException;
import gitlike.object.Commit;
import gitlike.manager.CommitManager;

import java.io.File;
import java.util.*;

/**
 * Merge files from the given branch into the current branch
 * usage: merge [branch name]
 */
public class MergeCommand implements Command {

    @Override
    public String execute(String... args) {
        final int NUM_ARGS = 2;
        if (args.length != NUM_ARGS)
            throw new GitLikeException("Incorrect operands.");

        Repository repository = new Repository();
        String branchName = args[1];

        return merge(repository, branchName);
    }

    static String merge(Repository repository, String branchName) {
        if (!repository.isInitialized())
            throw new GitLikeException("Not in an initialized GitLikedirectory.");

        if (!repository.getBranchManager().containsBranch(branchName))
            throw new GitLikeException("A branch with that name does not exist.");

        Branch branch = repository.getBranchManager().getBranch(branchName);
        Branch head = repository.getCurrentBranch();

        if (branch.equals(head))
            throw new GitLikeException("Cannot merge a branch with itself.");

        List<String> additionFiles = repository.getStageManager().getAllAdditionFiles();
        List<String> removalFiles = repository.getStageManager().getAllRemovalFiles();
        if (!additionFiles.isEmpty() || !removalFiles.isEmpty())
            throw new GitLikeException("You have uncommitted changes.");

        CommitManager commitManager = repository.getCommitManager();
        Commit headCommit = repository.getCurrentCommit();
        Commit branchCommit = commitManager.getCommit(branch.getCommitId());
        Commit splitCommit = getSplitPoint(repository, headCommit, branchCommit);

        if (splitCommit.equals(branchCommit))
            return "Given branch is an ancestor of the current branch.";

        if (splitCommit.equals(headCommit)) {
            CheckoutCommand.checkoutBranch(repository, branchName);
            repository.setCurrentBranch(head.getName());
            repository.getBranchManager().saveBranch(new Branch(head.getName(), branch.getCommitId()));
            return "Current branch fast-forwarded.";
        }

        Map<String, Blob> headBlobMap = getNameToBlobMap(repository, headCommit);
        Map<String, Blob> branchBlobMap = getNameToBlobMap(repository, branchCommit);
        Map<String, Blob> splitBlobMap = getNameToBlobMap(repository, splitCommit);

        StageManager stageManager = repository.getStageManager();
        for (String fileName: branchBlobMap.keySet()) {
            File workingDirFile = new File(fileName);
            if (workingDirFile.exists()
                    && !headCommit.containsFile(fileName)
                    && !stageManager.containAdditionFile(fileName)) {
                throw new GitLikeException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(headBlobMap.keySet());
        allFiles.addAll(branchBlobMap.keySet());
        allFiles.addAll(splitBlobMap.keySet());

        boolean isConflicted = false;
        for (String fileName: allFiles) {
            Blob headBlob = headBlobMap.get(fileName);
            Blob branchBlob = branchBlobMap.get(fileName);
            Blob splitBlob = splitBlobMap.get(fileName);

            boolean headModified = isModified(headBlob, splitBlob);
            boolean branchModified = isModified(branchBlob, splitBlob);
            boolean headBranchDiff = isModified(branchBlob, headBlob);

            if (branchBlob != null && branchModified && !headModified) {
                CheckoutCommand.checkoutCommitFile(repository, branchCommit.getId(), fileName);
                AddCommand.add(repository, fileName);
            }

            if (branchBlob == null && branchModified && !headModified) {
                RmCommand.rm(repository, fileName);
            }

            if (headModified && branchModified && headBranchDiff) {
                isConflicted = true;
                String headContent = "";
                if (headBlob != null)
                    headContent = headBlob.getContent();
                String branchContent = "";
                if (branchBlob != null)
                    branchContent = branchBlob.getContent();
                String mergedContent = String.format("<<<<<<< HEAD%n%s=======%n%s>>>>>>>%n", headContent, branchContent);
                File file = new File(fileName);
                Utils.writeContents(file, mergedContent);
                AddCommand.add(repository, fileName);
            }
        }

        String mergeMessage = String.format("Merged %s into %s.", branchName, head.getName());
        CommitCommand.commit(repository, mergeMessage, branch.getCommitId());

        if (isConflicted)
            return "Encountered a merge conflict.";
        return null;
    }

    static Map<String, Blob> getNameToBlobMap(Repository repository, Commit commit) {
        Map<String, Blob> result = new HashMap<>();
        Set<Blob> blobs = repository.getBlobManager().getCommitBlobs(commit);
        for (Blob blob: blobs) {
            result.put(blob.getFileName(), blob);
        }
        return result;
    }

    static Commit getSplitPoint(Repository repository, Commit headCommit, Commit branchCommit) {
        CommitManager commitManager = repository.getCommitManager();

        /* execute bfs to get all ancestors of the branch */
        Set<Commit> currentCommitHistory = new HashSet<>();
        Queue<Commit> toVisit = new ArrayDeque<>();
        toVisit.add(branchCommit);
        while (!toVisit.isEmpty()) {
            Commit commit = toVisit.poll();
            currentCommitHistory.add(commit);

            String parentId = commit.getParentId();
            if (parentId != null)
                toVisit.add(commitManager.getCommit(parentId));

            String mergedParentId = commit.getMergedParentId();
            if (mergedParentId != null)
                toVisit.add(commitManager.getCommit(mergedParentId));
        }

        /*
         * execute bfs to traverse the ancestors of the first commit and
         * in case of coincidence with the ancestors of the first commit, return the split point
         */
        toVisit.add(headCommit);
        while (!toVisit.isEmpty()) {
            Commit commit = toVisit.poll();
            if (currentCommitHistory.contains(commit))
                return commit;

            String parentId = commit.getParentId();
            if (parentId != null)
                toVisit.add(commitManager.getCommit(parentId));

            String mergedParentId = commit.getMergedParentId();
            if (mergedParentId != null)
                toVisit.add(commitManager.getCommit(mergedParentId));
        }

        return null;
    }

    static boolean isModified(Blob blob, Blob anotherBlob) {
        boolean blobExist = blob != null;
        boolean anotherBlobExist = anotherBlob != null;
        return blobExist && anotherBlobExist && !blob.equals(anotherBlob)
                || blobExist && !anotherBlobExist
                || !blobExist && anotherBlobExist;
    }

}
