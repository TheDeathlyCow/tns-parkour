package com.github.thedeathlycow.tnsparkour.database;

import com.github.thedeathlycow.tnsparkour.TnsParkour;
import com.github.thedeathlycow.tnsparkour.events.OneParamEventDelegate;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public abstract class Database {

    public final OneParamEventDelegate<Connection> onDatabaseInitializeDelegate = new OneParamEventDelegate<>();

    public Database() {
        TnsParkour.getInstance().onEnableDelegate.register(this::load);
    }

    public abstract Connection getSQLConnection() throws SQLException;

    public abstract void load();

    public void initialize() {
        try (Connection connection = getSQLConnection()) {
            onDatabaseInitializeDelegate.execute(connection);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to connect to database");
        }
    }
}
