package kavi.tech.service.mysql.component

import kavi.tech.service.common.extend.JsonExtend


/**
 * 翻页数据类
 * @author sili | 2017/11/10
 */

data class PageItemDao<T>(
        var items: List<T> = emptyList(),     //对象列表
        var count: Int = 0                    //当前页数量
) : JsonExtend {

    //游標開始位置
    var startRow : Int = 0

    //最多显示页数
    var maxShowPage : Int = 5

    private var pages: MutableList<Int> = ArrayList()

    //每页数量
    var pageSize = SIZE
        set(value) {
            var pageSize = value
            if (pageSize <= 1) {
                pageSize = 10
            }
            startRow = (currentPage - 1) * pageSize
            field = pageSize
        }

    //当前页面
    var currentPage = 1
        set(value) {
            var currentPage = value
            if (currentPage <= 1) {
                currentPage = 1
            }
            startRow = (currentPage - 1) * pageSize
            field = currentPage
        }

    //总数量
    var totalCount: Int = 0
        set(value) {
            val totalCount = value
            val _page = (totalCount + pageSize - 1) / pageSize + 1
            if (currentPage > _page) {
                currentPage = _page
            }
            field = totalCount
            countPages()
        }

    //最大页数
    val maxPage: Int
        get() {
            if (totalCount <= 1) {
                return 1
            }
            return if (pageSize <= 1) {
                totalCount
            } else (totalCount + pageSize - 1) / pageSize
        }

    //计算页面数量
    private fun countPages() {
        val _page = maxPage
        if (_page <= maxShowPage) {
            for (i in 1.._page) {
                pages.add(i)
            }
        } else {
            val prefix = ArrayList<Int>()
            val suffix = ArrayList<Int>()
            if (currentPage < 3) {
                for (i in 1..currentPage + 1 - 1) {
                    prefix.add(i)
                }
            } else {
                when{
                    _page - currentPage == 0 -> {
                        for (i in 4 downTo -1 + 1) {
                            if (currentPage - i > 0) {
                                prefix.add(currentPage - i)
                            }
                        }
                    }
                    _page - currentPage == 1 -> {
                        for (i in 3 downTo -1 + 1) {
                            if (currentPage - i > 0) {
                                prefix.add(currentPage - i)
                            }
                        }
                    }
                    else -> {
                        prefix.add(currentPage - 2)
                        prefix.add(currentPage - 1)
                        prefix.add(currentPage)
                    }
                }
            }
            val _size = maxShowPage - prefix.size
            var i = currentPage + 1
            while (i <= _page && i <= currentPage + _size) {
                suffix.add(i)
                ++i
            }
            pages.addAll(prefix)
            pages.addAll(suffix)
        }
    }

    // 常量
    companion object {
        const val PAGE = 1
        const val SIZE = 20
    }
}
