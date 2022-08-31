package gitlike.object;

import java.io.Serializable;

public class Branch implements Serializable {

    private final String name;
    private final String commitId;

    public Branch(String name, String commitId) {
        this.name = name;
        this.commitId = commitId;
    }

    public String getName() {
        return name;
    }

    public String getCommitId() {
        return commitId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Branch another = (Branch) obj;
        return name.equals(another.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
