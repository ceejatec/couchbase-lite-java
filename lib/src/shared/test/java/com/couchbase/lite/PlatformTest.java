//
// PlatformTest.java
//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.couchbase.lite;

import java.io.IOException;
import java.io.InputStream;


/**
 * Contains methods required for the tests to run on both Android and Java platforms.
 */
public interface PlatformTest {

    /* For calling CouchbaseLite.init() method. */
    void initCouchbaseLite();

    /* Set up any test specific logging */
    void setupFileLogging();

    /* Gets the directory for storing test databases. */
    String getDatabaseDirectory();

    /* Gets the platform specific temp directory. */
    String getTempDirectory(String name);

    /* Gets the assert as InputStream  by asset's name */
    InputStream getAsset(String asset) throws IOException;

    /* Scheduled to execute a task asynchronously. */
    void executeAsync(long delayMs, Runnable task);

    /* Reload the cross-platform error messages. */
    void reloadStandardErrorMessages();
}
