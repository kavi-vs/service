package kavi.tech.service.common.extension

import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject

/**
 * JSON数据格式转换扩展
 * */
inline fun <reified T> JsonObject?.value(key: String): T? {
    if (this == null) return null
    if (!this.containsKey(key)) return null
    val value = this.getValue(key)
    return when(value){
        is T -> value
        else -> null
    }
}

inline fun <reified T> JsonObject?.value(key: String, default: T): T {
    return this.value<T>(key) ?: default
}

/**
 * 实例化JSON格式
 * todo 优化
 * */
@Throws(DecodeException::class)
inline fun <reified T> JsonObject.toEntity(): T {
    return Json.prettyMapper.convertValue(this.keyToLowerCase(), T::class.java)
}

/**
 * 实例化JSON，异常不抛出错误
 * */
inline fun <reified T> JsonObject.tryToEntity(): T? {
    return try {
        this.toEntity<T>()
    } catch (e: Exception) {
        null
    }
}


/**
 * 将键值转换成小写
 * */
fun JsonObject.keyToLowerCase() : JsonObject{
    val json = JsonObject()
    this.forEach { json.put(it.key.toLowerCase(), it.value) }
    return json
}
