package com.eshare.shiro.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class CustomRealm extends JdbcRealm {

    private Map<String, String> credentials = new HashMap<>();
    private Map<String, Set> roles = new HashMap<>();
    private Map<String, Set> permissions = new HashMap<>();

    {
        credentials.put("Evan", "123");
        credentials.put("Yan", "123");

        roles.put("Evan", new HashSet<>(Arrays.asList("ADMIN")));
        roles.put("Yan", new HashSet<>(Arrays.asList("USER")));

        permissions.put("ADMIN", new HashSet<>(Arrays.asList("READ", "WRITE")));
        permissions.put("USER", new HashSet<>(Arrays.asList("READ")));
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        if (userToken.getUsername() == null || userToken.getUsername().isEmpty() ||
                !credentials.containsKey(userToken.getUsername())) {
            throw new UnknownAccountException("User doesn't exist");
        }
        return new SimpleAuthenticationInfo(userToken.getUsername(),
                credentials.get(userToken.getUsername()), getName());
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set roles = new HashSet<>();
        Set permissions = new HashSet<>();

        for (Object user : principals) {
            try {
                roles.addAll(getRoleNamesForUser(null, (String) user));
                permissions.addAll(getPermissions(null, null, roles));
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo(roles);
        authInfo.setStringPermissions(permissions);
        return authInfo;
    }

    @Override
    protected Set getRoleNamesForUser(Connection conn, String username)
            throws SQLException {
        if (!roles.containsKey(username)) {
            throw new SQLException("User doesn't exist");
        }
        return roles.get(username);
    }

    @Override
    protected Set getPermissions(Connection conn, String username, Collection roles)
            throws SQLException {
        Set userPermissions = new HashSet<>();
        for (Object role : roles) {
            if (!permissions.containsKey(role)) {
                throw new SQLException("Role doesn't exist");
            }
            userPermissions.addAll(permissions.get(role));
        }
        return userPermissions;
    }


}
