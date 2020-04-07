package kavi.tech.service.mysql.component

interface Entity{

    /**
     * 更新时间
     * */
    var updatedAt: Long?

    /**
     * 创建时间
     * */
    var createdAt: Long?

    /**
     * 软删除时间，默认0，为可用，时间戳为删除日期
     * */
    var deletedAt: Long?

    /**
     * 表名
     * */
    fun tableName(): String

}