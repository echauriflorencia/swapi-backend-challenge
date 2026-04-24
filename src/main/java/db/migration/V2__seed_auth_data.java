package db.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class V2__seed_auth_data extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        long adminRoleId = upsertRole(context, "ADMIN", "System administrator");
        long userRoleId = upsertRole(context, "USER", "Default authenticated user");
        long adminUserId = upsertUser(context, "swapi", new BCryptPasswordEncoder().encode("swapi123"), true);
        assignRole(context, adminUserId, adminRoleId);
        assignRole(context, adminUserId, userRoleId);
    }

    private long upsertRole(Context context, String name, String description) throws Exception {
        try (PreparedStatement select = context.getConnection().prepareStatement("select id from app_roles where upper(name) = upper(?)")) {
            select.setString(1, name);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        try (PreparedStatement insert = context.getConnection().prepareStatement(
            "insert into app_roles (name, description, created_by, created_at, updated_by, updated_at) values (?, ?, ?, current_timestamp, ?, current_timestamp)",
            Statement.RETURN_GENERATED_KEYS
        )) {
            insert.setString(1, name);
            insert.setString(2, description);
            insert.setString(3, "system");
            insert.setString(4, "system");
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new IllegalStateException("Unable to seed role " + name);
    }

    private long upsertUser(Context context, String username, String passwordHash, boolean enabled) throws Exception {
        try (PreparedStatement select = context.getConnection().prepareStatement("select id from app_users where username = ?")) {
            select.setString(1, username);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        try (PreparedStatement insert = context.getConnection().prepareStatement(
            "insert into app_users (username, password_hash, enabled, created_by, created_at, updated_by, updated_at) values (?, ?, ?, ?, current_timestamp, ?, current_timestamp)",
            Statement.RETURN_GENERATED_KEYS
        )) {
            insert.setString(1, username);
            insert.setString(2, passwordHash);
            insert.setBoolean(3, enabled);
            insert.setString(4, "system");
            insert.setString(5, "system");
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new IllegalStateException("Unable to seed user " + username);
    }

    private void assignRole(Context context, long userId, long roleId) throws Exception {
        try (PreparedStatement select = context.getConnection().prepareStatement(
            "select 1 from app_user_roles where user_id = ? and role_id = ?"
        )) {
            select.setLong(1, userId);
            select.setLong(2, roleId);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        try (PreparedStatement insert = context.getConnection().prepareStatement(
            "insert into app_user_roles (user_id, role_id) values (?, ?)"
        )) {
            insert.setLong(1, userId);
            insert.setLong(2, roleId);
            insert.executeUpdate();
        }
    }
}