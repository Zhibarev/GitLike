package gitlike.object;

import gitlike.Utils;

import java.io.Serializable;

public class Blob implements Serializable {
    private final String id;

    private final String fileName;
    private final String content;

    public Blob(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.id = Utils.sha1(this.fileName, this.content);
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
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
        Blob another = (Blob) obj;
        return id.equals(another.id);
    }
}
