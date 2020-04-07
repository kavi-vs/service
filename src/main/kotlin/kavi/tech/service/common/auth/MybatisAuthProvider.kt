package kavi.tech.service.common.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.web.handler.impl.HttpStatusException
import kavi.tech.service.common.strategry.HashStrategy
import kavi.tech.service.mysql.dao.UserDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import tech.kavi.vs.core.VertxBeansBase.Companion.value
import javax.security.sasl.AuthenticationException

@Service
class MybatisAuthProvider @Autowired constructor(
    private val userDao: UserDao,
    private val strategy: HashStrategy
) : AuthProvider {

    /**
     * 登路密码验证器
     * */
    override fun authenticate(authInfo: JsonObject, handler: Handler<AsyncResult<User>>) {
        val username = authInfo.value<String>("username")
        if (username == null) {
            handler.handle(Future.failedFuture(HttpStatusException(400, NullPointerException("Username is null"))))
            return
        }
        val password = authInfo.value<String>("password")
        if (password == null) {
            handler.handle(Future.failedFuture(HttpStatusException(400, NullPointerException("Password is null"))))
            return
        }

        // 数据库密码验证
        userDao.login(listOf(Pair("username", username)))
        .map { user ->
            user ?: throw HttpStatusException(403, NullPointerException("User is null"))
            val userPassword = user.password
            val passwordSalt = user.passwordSalt
            when{
                userPassword == null -> throw HttpStatusException(403, NullPointerException("User password is null"))
                passwordSalt == null -> throw HttpStatusException(403, NullPointerException("User salt is null"))
                else -> {
                    // todo 增加权限级别
                    val version = strategy.version(userPassword)
                    val hashedPassword = strategy.computeHash(password, passwordSalt, version)
                    when(strategy.isEqual(userPassword, hashedPassword)){
                        true -> user
                        else -> throw HttpStatusException(403, AuthenticationException("User authentication error"))
                    }
                }
            }
        }
        .subscribe({ user ->
            handler.handle(Future.succeededFuture(AuthUser(this)))
        },{
            handler.handle(Future.failedFuture(it))
        })
    }
}