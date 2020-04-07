package kavi.tech.service.mysql.dao

import io.vertx.core.logging.Logger
import io.vertx.rxjava.ext.asyncsql.AsyncSQLClient
import io.vertx.rxjava.ext.sql.SQLConnection
import kavi.tech.service.common.extension.logger
import kavi.tech.service.common.strategry.HashStrategy
import kavi.tech.service.mysql.component.AbstractDao
import kavi.tech.service.mysql.entity.User
import kavi.tech.service.mysql.component.SQL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.io.Serializable
import rx.Single

@Repository
class UserDao  @Autowired constructor(
    private val client: AsyncSQLClient,
    private val strategy: HashStrategy
) : AbstractDao<User>(client) {
    override val log: Logger = logger(this::class)

    /**
     * 用户登入搜索
     * */
    fun login(where: List<Serializable>): Single<User> {
        return this.one(SQL.init { SELECT("*"); FROM(User.tableName); WHERES(where) })
    }

    /**
     * 创建用户
     * */
    fun save(user: User): Single<User> {
        val username = user.username
        val password = user.password
        if (user.id == null && password == null) {
            return Single.error(NullPointerException("Password is not null"))
        }
        if (username.isNullOrEmpty()) {
            return Single.error(NullPointerException("Username is not null"))
        }
        /*防止外部更新的数据（设为null不处理的数据）*/
        user.password = null
        user.passwordSalt = null
        /*设置新密码*/
        password?.apply {
            val version = -1 // 默认版本
            val passwordSalt = strategy.generateSalt()
            user.password = strategy.computeHash(this, passwordSalt, version)
            user.passwordSalt = passwordSalt
        }
        /* 验证用户名唯一存在 */
        val sql = SQL.init {
            SELECT("id")
            FROM(User.tableName)
            WHERE(Pair("username", username))
        }
        return this.one(sql){ conn, result ->
            when(result != null && result.rows.isNotEmpty()) {
                true -> Single.error(IllegalAccessError("Username are already exist"))
                else -> insert(conn, user)
            }
        }
    }

    /**
     * 新增记录
     * */
    private fun insert(conn: SQLConnection, user : User): Single<User> {
        val sql = SQL.init {
            INSERT_INTO(user.tableName())
            user.preInsert().forEach { t, u -> VALUES(t, u) }
        }
        return this.update(conn, sql).map {
            user.apply { this.id = it.keys.getLong(0) }
        }
    }
}