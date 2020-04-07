package kavi.tech.service.mongo.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kavi.tech.service.mongo.component.AbstractSchema

//忽略该目标对象不存在的属性
@JsonIgnoreProperties(ignoreUnknown = true)
data class Record constructor(
        var title : String? = null,          // 标题
        var content: String? = null,         // 内容
        var status : Int = 0                 // 状态

) : AbstractSchema(){

    override fun tableName() = tableName

    companion object {
        const val tableName = "record"
    }
}