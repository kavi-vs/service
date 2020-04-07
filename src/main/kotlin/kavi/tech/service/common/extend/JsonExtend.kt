package kavi.tech.service.common.extend

import io.vertx.core.json.JsonObject

interface JsonExtend {
    /**
     * bean 转json格式
     * @author sili | 2017/11/18
     */
    open fun toJson(): JsonObject = JsonObject.mapFrom(this)
}