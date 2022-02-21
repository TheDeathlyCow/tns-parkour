package com.github.thedeathlycow.tnsparkour.database.tables;

import com.github.thedeathlycow.tnsparkour.database.Database;

import java.sql.Connection;
import java.util.Collection;

public abstract class DatabaseTable<T> {

    private final Database database;

    public DatabaseTable(Database database) {
        this.database = database;
        this.database.onDatabaseInitializeDelegate.register(this::createTable);
    }

    public abstract void createTable(Connection connection);

    public abstract T getFirst(Object... whereConditions);

    public abstract Collection<T> getAll(Object... whereConditions);

}
