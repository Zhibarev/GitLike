package gitlike;

/** General exception indicating a GitLike error.  For fatal errors, the
 *  result of .getMessage() is the error message to be printed.
 */
public class GitLikeException extends RuntimeException {


    /** A GitLikeException with no message. */
    public GitLikeException() {
        super();
    }

    /** A GitLikeException MSG as its message. */
    public GitLikeException(String message) {
        super(message);
    }

}
