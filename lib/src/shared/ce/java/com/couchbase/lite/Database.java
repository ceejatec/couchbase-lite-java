//
// Database.java
//
// Copyright (c) 2018 Couchbase, Inc.  All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.lite;

import android.support.annotation.NonNull;

import java.io.File;

import com.couchbase.lite.internal.core.C4Constants;
import com.couchbase.lite.internal.utils.Preconditions;


/**
 * A Couchbase Lite database.
 */
public final class Database extends AbstractDatabase {
    /**
     * Make a copy of a database in a new location.
     *
     * @param path   path to the existing db file
     * @param name   the name of the new DB
     * @param config a config with the new location
     * @throws CouchbaseLiteException on copy failure
     */
    public static void copy(
        @NonNull File path,
        @NonNull String name,
        @NonNull DatabaseConfiguration config)
        throws CouchbaseLiteException {
        Preconditions.checkArgNotNull(path, "path");
        Preconditions.checkArgNotNull(name, "name");
        Preconditions.checkArgNotNull(config, "config");

        AbstractDatabase.copy(path, name, config, C4Constants.EncryptionAlgorithm.NONE, null);
    }

    //---------------------------------------------
    // Constructors
    //---------------------------------------------

    /**
     * Construct a Database with a given name and the default config.
     * If the database does not yet exist it will be created.
     *
     * @param name   The name of the database: May NOT contain capital letters!
     * @throws CouchbaseLiteException if any error occurs during the open operation.
     */
    public Database(@NonNull String name) throws CouchbaseLiteException {super(name, new DatabaseConfiguration()); }

    /**
     * Construct a  AbstractDatabase with a given name and database config.
     * If the database does not yet exist, it will be created, unless the `readOnly` option is used.
     *
     * @param name   The name of the database: May NOT contain capital letters!
     * @param config The database config.
     * @throws CouchbaseLiteException Throws an exception if any error occurs during the open operation.
     */
    public Database(@NonNull String name, @NonNull DatabaseConfiguration config) throws CouchbaseLiteException {
        super(name, config);
    }

    //---------------------------------------------
    // Package visible: Implementing abstract methods for Encryption
    //---------------------------------------------

    @Override
    int getEncryptionAlgorithm() { return C4Constants.EncryptionAlgorithm.NONE; }

    @Override
    byte[] getEncryptionKey() { return null; }
}
