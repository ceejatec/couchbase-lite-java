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
import com.couchbase.lite.AbstractReplicator
import com.couchbase.lite.BaseReplicatorTest
import com.couchbase.lite.DatabaseEndpoint
import com.couchbase.lite.ListenerToken
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Replicator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


const val TEST_KEY = "test-key"

class ReplicatorPendingDocIdTest : BaseReplicatorTest() {

    @Test
    fun testPendingDocIDs() {
        val n = 5

        var value = "create"
        createDocs(n, value)
        validatePendingDocumentIds(value)

        value = "update"
        updateDocs(n, value)
        validatePendingDocumentIds(value)

        deleteDocs(n)
        validatePendingDocumentIds(null)
    }

    @Test
    fun testPendingDocIDsPullOnlyException() {
        var replicator: Replicator? = null
        var token: ListenerToken? = null
        var err: Exception? = null;
        try {
            run(pullConfig(), 0, null, false, false) { r: Replicator ->
                replicator = r

                token = r.addChangeListener { change ->
                    if (change.status.activityLevel == AbstractReplicator.ActivityLevel.BUSY) {
                        try {
                            change.replicator.pendingDocumentIds
                        } catch (e: Exception) {
                            err = e
                        }
                    }
                }
            }
        } finally {
            token?.let { replicator?.removeChangeListener(it) }
        }

        assertNotNull(err)
    }

    private fun createDocs(n: Int, value: String): Unit {
        for (i in 0 until n) {
            val doc = MutableDocument("doc-${i}")
            doc.setString(TEST_KEY, value)
            db.save(doc)
        }
    }

    private fun updateDocs(n: Int, value: String) {
        for (i in 0 until n) {
            db.getDocument("doc-${i}")?.let { db.save(it.toMutable().setString(TEST_KEY, value)) }
        }
    }

    private fun deleteDocs(n: Int) {
        for (i in 0 until n) {
            db.getDocument("doc-${i}")?.let { db.delete(it) }
        }
    }

    private fun validatePendingDocumentIds(value: String?) {
        val config = makeConfig(true, false, false, DatabaseEndpoint(otherDB))

        var replicator: Replicator? = null
        var token1: ListenerToken? = null
        var token2: ListenerToken? = null
        var replicated = false
        var docIds: Set<String>? = null
        val sizes = listOf(0, 0, 0).toMutableList()
        try {
            run(config, 0, null, false, false) { r: Replicator ->
                replicator = r

                token1 = r.addDocumentReplicationListener { replicated = true }

                token2 = r.addChangeListener { change ->
                    val ids = change.replicator.pendingDocumentIds
                    docIds = ids

                    when (change.status.activityLevel) {
                        AbstractReplicator.ActivityLevel.CONNECTING -> sizes[0] = ids.size
                        AbstractReplicator.ActivityLevel.BUSY -> if (!replicated) {
                            sizes[1] = ids.size
                        }
                        AbstractReplicator.ActivityLevel.STOPPED -> sizes[2] = ids.size
                        else -> Unit
                    }
                }
            }
        } finally {
            token1?.let { replicator?.removeChangeListener(it) }
            token2?.let { replicator?.removeChangeListener(it) }
        }

        assertEquals(5, sizes[0])
        assertNotEquals(0, sizes[1])
        assertEquals(0, sizes[2])

        docIds?.let {
            for (id in it) {
                val doc = db.getDocument(id);
                doc.let { assertEquals(value, doc.getString(TEST_KEY)) }
            }
        }
    }
}