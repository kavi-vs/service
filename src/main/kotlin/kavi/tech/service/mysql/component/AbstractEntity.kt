package kavi.tech.service.mysql.component

import com.fasterxml.jackson.annotation.JsonProperty
import kavi.tech.service.common.extend.JsonExtend

abstract class AbstractEntity : Entity, JsonExtend{
    @field:JsonProperty("created_at")
    override var createdAt: Long? = null
    @field:JsonProperty("updated_at")
    override var updatedAt: Long? = null
    @field:JsonProperty("deleted_at")
    override var deletedAt: Long? = null

    /**
     * 预处理插入数据
     * */
    open fun preInsert(): Map<String, Any?> {
        this.updatedAt = null
        this.createdAt = System.currentTimeMillis()
        this.deletedAt = 0
        return this.destruct()
    }

    /**
     * 预处理更新数据
     * */
    open fun preUpdate(): Map<String, Any?> {
        this.createdAt = null
        this.updatedAt = System.currentTimeMillis()
        return this.destruct()
    }

    /**
     * 数据解构
     * */
    fun destruct(isValueNull: Boolean = false): Map<String, Any?> {
        return this.toJson()
            .associate { Pair(it.key.toString(), it.value) }
            .filterValues { value ->
                when(value) {
                    null -> isValueNull
                    else -> true
                }
        }
    }

}