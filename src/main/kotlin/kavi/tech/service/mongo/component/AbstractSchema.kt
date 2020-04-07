package kavi.tech.service.mongo.component

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import kavi.tech.service.common.extend.JsonExtend

/**
 * 数据基础结构
 * @author sili | 2017/10/26
 */
abstract class AbstractSchema : JsonExtend {

    @JsonSerialize(using = ToStringSerializer::class)
    var _id: String? = null
    @field:JsonProperty("created_at")
    var createdAt : Long? = null
    @field:JsonProperty("updated_at")
    var updatedAt : Long? = null
    @field:JsonProperty("deleted_at")
    var deletedAt: Long? = null

    /**
     * 表明
     * */
    abstract fun tableName() : String

    open fun preInsert(){
        this.createdAt = System.currentTimeMillis()
    }

    open fun preUpdate(){
        this.updatedAt = System.currentTimeMillis()
    }

}