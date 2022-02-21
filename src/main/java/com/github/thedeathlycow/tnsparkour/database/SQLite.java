package com.github.thedeathlycow.tnsparkour.database;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLite extends Database {

    public SQLite() {
        super();
    }

    @Override
    public Connection getSQLConnection() throws SQLException {
        return null;
    }

    @Override
    public void load() {

    }
}
