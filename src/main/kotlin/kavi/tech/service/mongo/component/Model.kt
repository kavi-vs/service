package kavi.tech.service.mongo.component

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import rx.Single
import java.util.*

/**
 * 基础包含dao方法接口
 * @author sili | 2017/10/23
 */

interface Model<T> {

    /**
     * 分页查询
     * */
    fun listPage(query: JsonObject = JsonObject(),
                 page: Int = PageItemModel.PAGE,
                 size: Int = PageItemModel.SIZE,
                 sortKey: String,
                 sortOrder: SortOrder
    ): Single<PageItemModel<JsonObject>>

    /**
     * 根据ID查询
     * */
    fun byId(id: String): Single<Optional<T>>

    /**
     * 根据键查询
     * */
    fun byKey(key: String, value: String): Single<Optional<T>>

    /**
     * 添加数据
     * */
    fun add(objects: T): Single<T>

    /**
     * 更新数据
     * */
    fun update(objects: T): Single<Optional<Pair<JsonObject,T>>>

    /**
     * 根据ID删除数据
     * */
    fun remove(id: String): Single<Optional<Pair<JsonObject, String>>>

    /**
     * 统计数量
     * */
    fun count(query: JsonObject): Single<Long>

    /**
     * 列表查询
     * */
    fun list(query: JsonObject, findOptions: FindOptions): Single<List<JsonObject>>

}