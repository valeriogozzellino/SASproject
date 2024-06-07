package catering.businesslogic.user;

import java.util.List;

public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private List<User> cooks;

    private UserManager() {
        cooks = User.loadCooks();
    }

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public void fakeLogin(String username) //TODO: bisogna implementare il login vero!
    {
        this.currentUser = User.loadUser(username);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public List<User> getCooks() {
        return cooks;
    }

    public User getCook(int id) {
        for(User u: cooks)
            if(u.getId() == id)
                return u;
        return null;
    }
}
