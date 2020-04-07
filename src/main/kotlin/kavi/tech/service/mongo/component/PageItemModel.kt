package kavi.tech.service.mongo.component

import kavi.tech.service.common.extend.JsonExtend

/**
 * 项目包含基类
 * @author sili | 2017/10/23
 */
class PageItemModel<T>(val count: Long?, val pagesize: Int?, val items: List<T>) : JsonExtend {
    companion object {
        const val PAGE = 1  // 默认当前页
        const val SIZE = 20 // 默认页数
    }
}