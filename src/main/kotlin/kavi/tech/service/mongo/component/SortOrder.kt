package kavi.tech.service.mongo.component

/**
 * 排序基类枚举
 * @author sili | 2017/10/23
 */
enum class SortOrder constructor(internal val value: Int) {
    ASC(1), DESC(-1)
}
