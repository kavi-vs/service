package kavi.tech.service.common.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.shareddata.impl.ClusterSerializable
import io.vertx.ext.auth.User

abstract class AbstractAuthUser : User, ClusterSerializable {

    override fun isAuthorized(authority: String, resultHandler: Handler<AsyncResult<Boolean>>): User {
        return this
    }

    override fun clearCache(): User {
        return this
    }

    override fun writeToBuffer(buff: Buffer) {
    }

    override fun readFromBuffer(pos: Int, buffer: Buffer): Int {
        return pos
    }

    protected abstract fun doIsPermitted(permission: String, resultHandler: Handler<AsyncResult<Boolean>>)

}
