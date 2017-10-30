package com.vicpin.krealmextensions

import android.os.Handler
import android.os.Looper
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults


/**
 * Created by victor on 2/1/17.
 * Extensions for Realm. All methods here are asynchronous, and only notify changes once.
 */


/**
 * Returns first entity in database asynchronously.
 */
fun <T : RealmObject> T.queryFirstAsync(callback: (T?) -> Unit) {
    mainThread {

        val realm = getRealm()

        val result = RealmQuery.createQuery(realm, this.javaClass).findFirstAsync()
        result.addChangeListener<T> { it ->
            callback(if(it != null && it.isValid) realm.copyFromRealm(it) else null)
            result.removeChangeListeners()
            realm.close()
        }
    }

}

/**
 * Returns last entity in database asynchronously.
 */
fun <T : RealmObject> T.queryLastAsync(callback: (T?) -> Unit) {
    queryAllAsync { callback(if(it.isNotEmpty() && it.last().isValid) it.last() else null) }
}

/**
 * Returns all entities in database asynchronously.
 */
fun <T : RealmObject> T.queryAllAsync(callback: (List<T>) -> Unit) {
    mainThread {

        val realm = getRealm()

        val result: RealmResults<T> = RealmQuery.createQuery(realm, this.javaClass).findAllAsync()

        result.addChangeListener { it ->
            callback(realm.copyFromRealm(it))
            result.removeChangeListeners()
            realm.close()
        }
    }
}

/**
 * Queries for entities in database asynchronously.
 */
fun <T : RealmObject> T.queryAsync(query: Query<T>, callback: (List<T>) -> Unit) {
    mainThread {

        val realm = getRealm()
        val realmQuery: RealmQuery<T> = RealmQuery.createQuery(realm, this.javaClass)
        query(realmQuery)
        val result = realmQuery.findAllAsync()
        result.addChangeListener { it ->
            callback(realm.copyFromRealm(it))
            result.removeChangeListeners()
            realm.close()
        }
    }
}

private fun mainThread(block: () -> Unit) {
    Handler(Looper.getMainLooper()).post(block)
}




