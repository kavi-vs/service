package kavi.tech.service.ms

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.rx.java.RxHelper
import kavi.tech.service.mysql.component.SQL
import kavi.tech.service.mysql.dao.UserDao
import kavi.tech.service.mysql.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 代码编写完毕，使用命令：gradle build 或 IDEA 的 Build -> build project
 * */
@Service
class UserServiceImpl @Autowired constructor(
    private val  userDao : UserDao
) : UserService {
    override fun list(document: JsonObject, handler: Handler<AsyncResult<JsonArray>>) {
        val sql = SQL.init {
            SELECT("*")
            FROM(User.tableName)
        }
        userDao.select(sql).map { JsonArray(it) }.subscribe(RxHelper.toSubscriber(handler))
    }
}