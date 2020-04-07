package kavi.tech.service.common.extension

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import kotlin.reflect.KClass

/**
 * 日志接口方法
 */
fun logger(clz: KClass<*>): Logger {
    return LoggerFactory.getLogger(clz.qualifiedName)
}
