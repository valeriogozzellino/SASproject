package catering.businesslogic.user;

import catering.businesslogic.shift.Shift;
import catering.businesslogic.shift.ShiftManager;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {

    private static Map<Integer, User> loadedUsers = new HashMap<Integer, User>();
    private List<Shift> shifts = new ArrayList<>();

    public static enum Role {
        SERVIZIO, CUOCO, CHEF, ORGANIZZATORE
    };

    private int id;
    private String username;
    private Set<Role> roles;

    public User() {
        id = 0;
        username = "";
        this.roles = new HashSet<>();
    }

    public boolean isChef() {
        return roles.contains(Role.CHEF);
    }

    public boolean isCook() {
        return roles.contains(Role.CUOCO);
    }

    public boolean isAvaible(Shift shift) {
        return shifts.contains(shift);
    }

    public boolean addShift(Shift shift) {
        return shifts.add(shift);
    }

    public String getUserName() {
        return username;
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        String result = username;
        if (roles.size() > 0) {
            result += ": ";

            for (User.Role r : roles) {
                result += r.toString() + " ";
            }
        }
        return result;
    }

    public static List<User> loadCooks() {
        String stm = "SELECT * FROM users JOIN userroles ON id = user_id WHERE role_id = 'c'";
        List<User> result = new ArrayList<>();
        PersistenceManager.executeQuery(stm, rs -> {
            User u = new User();
            u.username = rs.getString("username");
            u.id = rs.getInt("id");
            u.roles.add(Role.CUOCO);
            result.add(u);
        });

        List<Shift> shifts = new ArrayList<>();
        
        ShiftManager tm = ShiftManager.getInstance();
        for (User user: result) {
            stm = "SELECT turn_id FROM availableinturn WHERE user_id = " + user.id;
            PersistenceManager.executeQuery(stm, rs -> {
                user.shifts.add(tm.getShift(rs.getInt("turn_id")));
            });
        }

        return result;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static User loadUserById(int uid) {
        if (loadedUsers.containsKey(uid))
            return loadedUsers.get(uid);

        User load = new User();
        String userQuery = "SELECT * FROM users WHERE id='" + uid + "'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.username = rs.getString("username");
            }
        });
        if (load.id > 0) {
            loadedUsers.put(load.id, load);
            String roleQuery = "SELECT * FROM userroles WHERE user_id=" + load.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            load.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            load.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            load.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            load.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return load;
    }

    public static User loadUser(String username) {
        User u = new User();
        String userQuery = "SELECT * FROM users WHERE username='" + username + "'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
            }
        });
        if (u.id > 0) {
            loadedUsers.put(u.id, u);
            String roleQuery = "SELECT * FROM userroles WHERE user_id=" + u.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            u.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            u.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            u.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            u.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return u;
    }
}
