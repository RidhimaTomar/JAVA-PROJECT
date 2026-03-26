package usermanagement;
 
import model.User;
 
/** Returned by every user-management operation (register, suspend, etc). */
public class OperationResult {
 
    private final boolean success;
    private final String  message;
    private final User    user;
 
    private OperationResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user    = user;
    }
 
    public static OperationResult success(String message, User user) {
        return new OperationResult(true, message, user);
    }
 
    public static OperationResult failure(String message) {
        return new OperationResult(false, message, null);
    }
 
    public boolean isSuccess() { return success; }
    public String  getMessage(){ return message; }
    public User    getUser()   { return user; }
}