package kavi.tech.service.mongo.component

import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.ext.mongo.FindOptions
import io.vertx.rxjava.ext.mongo.MongoClient
import rx.Single
import java.util.*
import java.util.stream.Collectors


abstract class AbstractModel<T : AbstractSchema>(
    private val client: MongoClient,
    private val collectionName: String,
    private val clazz: Class<T>
) : Model<T> {

    abstract val log: Logger

    /**
     * 数据库执行日志
     * */
    open fun logger(condition: String, startTime: Long) = log.info("MONGO[${System.currentTimeMillis() - startTime}ms]: $condition")
    fun logger(condition: JsonObject, startTime: Long) = this.logger(condition.toString(), startTime)
    fun logger(condition: JsonArray, startTime: Long) = this.logger(condition.toString(), startTime)

    /**
     * 翻页查询
     * @param query 查询条件
     * @param page
     * @param size 返回数量
     * @param sortKey 排序键
     * @param sortOrder 排序方式
     */
    override fun listPage(
        query: JsonObject,
        page: Int,
        size: Int,
        sortKey: String,
        sortOrder: SortOrder
    ): Single<PageItemModel<JsonObject>> {
        val options = FindOptions()
                .setLimit(size)
                .setSkip((page - 1) * size)
                .setSort(JsonObject().put(sortKey, sortOrder.value))

        val countObservable = client.rxCount(collectionName, query)
        val documentsObservable = client.rxFindWithOptions(collectionName, query, options)
        val startTime = System.currentTimeMillis()
        return Single.zip(countObservable, documentsObservable) {
                count, documents ->
            val items = documents.stream().collect(Collectors.toList())
            PageItemModel(count, size, items)
        }.doAfterTerminate { logger(query, startTime) }
    }

    override fun byId(id: String): Single<Optional<T>> = one(JsonObject().put("_id", id))

    override fun byKey(key: String, value: String): Single<Optional<T>>  = one(JsonObject().put(key, value))

    override fun add(objects : T): Single<T> {
        // 插入前执行预插入行为
        objects.preInsert()
        val document = JsonObject(Json.encode(objects))
            document.remove("_id")
        val startTime = System.currentTimeMillis()
        return client.rxInsert(collectionName, document)
                     .doAfterTerminate { logger(document, startTime) }
                     .map { objects.apply { this._id = it } }
    }

    override fun update(objects: T): Single<Optional<Pair<JsonObject, T>>> {
        val query = JsonObject().put("_id", objects._id)
        val document = JsonObject(Json.encodePrettily(objects))
            document.remove("_id")
        val update = JsonObject().put("\$set", document)
        val startTime = System.currentTimeMillis()
        return client.rxUpdateCollection(collectionName, query, update)
            .doAfterTerminate { logger(document, startTime) }
            .map { Optional.of(Pair(it.toJson(), objects)) }
    }

    override fun remove(id: String): Single<Optional<Pair<JsonObject, String>>> {
        val startTime = System.currentTimeMillis()
        val query = JsonObject().put("_id", id)
        return client.rxRemoveDocument(collectionName, query)
            .doAfterTerminate { logger(query, startTime) }
            .map { Optional.of(Pair(it.toJson(), id)) }
    }

    fun one(query: JsonObject): Single<Optional<T>> {
        val startTime = System.currentTimeMillis()
        return client.rxFindOne(collectionName, query, JsonObject())
            .doAfterTerminate { logger(query, startTime) }
            .map {
            when (it) {
                null -> Optional.empty()
                else -> Optional.of(Json.mapper.convertValue(it, clazz))
            }
        }
    }

    override fun count(query: JsonObject): Single<Long> {
        val startTime = System.currentTimeMillis()
        return client.rxCount(collectionName, query).doAfterTerminate { logger(query, startTime) }
    }

    override fun list(query: JsonObject, findOptions: FindOptions): Single<List<JsonObject>>{
        val startTime = System.currentTimeMillis()
        return this.client.rxFindWithOptions(collectionName, query, findOptions)
                   .doAfterTerminate { logger(query, startTime) }
    }
}
