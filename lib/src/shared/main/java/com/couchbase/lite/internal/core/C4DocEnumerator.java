//
// C4DocEnumerator.java
//
// Copyright (c) 2017 Couchbase, Inc All rights reserved.
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
package com.couchbase.lite.internal.core;

import com.couchbase.lite.LiteCoreException;


public class C4DocEnumerator {
    static native void close(long e);

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    static native void free(long e);

    static native long enumerateChanges(long db, long since, int flags)
        throws LiteCoreException;

    static native long enumerateAllDocs(long db, int flags) throws LiteCoreException;

    static native boolean next(long e) throws LiteCoreException;

    static native long getDocument(long e) throws LiteCoreException;

    static native void getDocumentInfo(long e, Object[] outIDs, long[] outNumbers);
    //-------------------------------------------------------------------------
    // Member Variables
    //-------------------------------------------------------------------------
    private long handle; // hold pointer to C4DocEnumerator

    //-------------------------------------------------------------------------
    // native methods
    //-------------------------------------------------------------------------

    C4DocEnumerator(long db, long since, int flags) throws LiteCoreException {
        handle = enumerateChanges(db, since, flags);
    }

    C4DocEnumerator(long db, int flags) throws LiteCoreException {
        handle = enumerateAllDocs(db, flags);
    }

    //-------------------------------------------------------------------------
    // public methods
    //-------------------------------------------------------------------------
    public void close() {
        close(handle);
    }

    public void free() {
        if (handle != 0L) {
            free(handle);
            handle = 0L;
        }
    }

    public boolean next() throws LiteCoreException {
        return next(handle);
    }

    public C4Document getDocument() throws LiteCoreException {
        final long doc = getDocument(handle);
        return doc != 0 ? new C4Document(doc) : null;
    }

    //-------------------------------------------------------------------------
    // protected methods
    //-------------------------------------------------------------------------
    @SuppressWarnings("NoFinalizer")
    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }
}
