package kavi.tech.service.common.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider

class AuthUser(authProvider: MybatisAuthProvider) : AbstractAuthUser() {


    private var principal: JsonObject? = null

    private var rolePrefix: String? = null

    override fun doIsPermitted(permissionOrRole: String, resultHandler: Handler<AsyncResult<Boolean>>) {
    }

    override fun principal(): JsonObject? {
        return principal
    }

    override fun setAuthProvider(authProvider: AuthProvider) {
    }

    override fun writeToBuffer(buff: Buffer) {
        super.writeToBuffer(buff)
    }

    override fun readFromBuffer(pos: Int, buffer: Buffer): Int {
        return pos
    }

    private fun hasRoleOrPermission(
        roleOrPermission: String?,
        query: String,
        resultHandler: Handler<AsyncResult<Boolean>>
    ) {
    }
}
