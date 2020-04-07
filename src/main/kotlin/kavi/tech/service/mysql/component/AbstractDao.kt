package kavi.tech.service.mysql.component

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.UpdateResult
import io.vertx.rxjava.ext.asyncsql.AsyncSQLClient
import io.vertx.rxjava.ext.sql.SQLConnection
import kavi.tech.service.common.extension.toEntity
import rx.Single
import kotlin.NoSuchElementException

abstract class AbstractDao<T: Entity> constructor(private val client : AsyncSQLClient) :
    Dao<T> {

    abstract val log: Logger

    /**
     * 数据库执行日志
     * */
    open fun logger(sql: String, startTime: Long) = log.info("SQL[${System.currentTimeMillis() - startTime}ms]: $sql")

    /**
     * 插入更新方法时间记录
     * */
    fun update(conn: SQLConnection, sql: String): Single<UpdateResult> {
        val startTime = System.currentTimeMillis()
        return conn.rxUpdate(sql).doAfterTerminate { logger(sql, startTime) }
    }

    /**
     * 无返回结果的执行查询
     * @param sql 执行语句
     * @param bodyAfter 执行后的处理方法函数
     * */
    fun execute(sql: String, bodyAfter: ((SQLConnection) -> Single<SQLConnection>)?): Single<Void> {
        return client.rxGetConnection().flatMap { conn ->
            val startTime = System.currentTimeMillis()
            conn.rxExecute(sql)
                .doAfterTerminate { logger(sql, startTime) }
                .flatMap { result ->
                    bodyAfter?.let { body -> body(conn).map { result } } ?: Single.just(result)
                }.doAfterTerminate(conn::close)
        }
    }

    /**
     * 查询方法时间记录
     * */
    fun query(conn: SQLConnection, sql: String): Single<ResultSet> {
        val startTime = System.currentTimeMillis()
        return conn.rxQuery(sql).doAfterTerminate { logger(sql, startTime) }
    }

    /**
     * 函数式数据处理方法
     * */
    fun <T> query(sql: String, body: ((SQLConnection, ResultSet?) -> Single<T>)): Single<T>
            = client.rxGetConnection().flatMap { conn ->
                query(conn, sql).flatMap { body(conn, it) }.doAfterTerminate(conn::close)
            }

    /**
     * 返回单个数据记录
     * @return 返回结果值 JsonArray
     * */
    fun querySingle(conn: SQLConnection, sql: String): Single<JsonArray> {
        val startTime = System.currentTimeMillis()
        return conn.rxQuerySingle(sql).doAfterTerminate { logger(sql, startTime) }
    }

    /**
     * 数据查找
     * */
    fun <T> select(sql: String, body: ((SQLConnection, ResultSet?) -> Single<T>)): Single<T> {
        return this.query(sql){ conn , result -> body(conn, result) }
    }

    /**
     * 数据查找默认处理
     * */
    fun select(sql: String): Single<List<JsonObject>> {
        return this.select(sql){ _, result -> Single.just(result?.rows ?: listOf()) }
    }

    /**
     * 返回单条记录并转换至指定类型
     * */
    inline fun <reified T> one(sql: String, noinline body: ((SQLConnection, ResultSet?) -> Single<T>)? = null): Single<T> {
        return when(body) {
            null -> this.select("$sql LIMIT 1").map {
                    result ->
                when(result.isEmpty()){
                    true -> throw NoSuchElementException()
                    else -> result.first().toEntity<T>()
                }
            }
            else -> this.select("$sql LIMIT 1", body)
        }
    }


    /**
     *
     * 翻页
     * @param sqlCount 查询总数量SQL语句
     * @param sqlList 查询列表SQL语句
     * @param currentPage 查询页面
     * @param pageSize 每页显示数量
     */
    fun listPage(sqlCount: SQL,
                 sqlList: SQL,
                 currentPage: Int = PageItemDao.PAGE,
                 pageSize: Int = PageItemDao.SIZE
    ): Single<PageItemDao<JsonObject>>{
        return client.rxGetConnection().flatMap { conn ->
            querySingle(conn, sqlCount.toString())
            .map { result ->
                PageItemDao<JsonObject>().apply {
                    this.totalCount = try { result.getInteger(0) } catch (e: Exception) { 0 }
                    this.pageSize = pageSize
                    this.currentPage = currentPage
                }
            }
            .flatMap { pageItem ->
                query(conn, sqlList.toString() + " LIMIT ${pageItem.startRow}, ${pageItem.pageSize}" )
                .map { pageItem.apply { this.items = it.rows } }
            }
            .doAfterTerminate(conn::close)
        }
    }

}
