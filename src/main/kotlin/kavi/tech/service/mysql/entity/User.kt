package kavi.tech.service.mysql.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import kavi.tech.service.mysql.component.AbstractEntity

/**
 * 用户表
 *
 * 序列化时忽略密码和盐值
 * value = ["password", "password_salt"]
 * allowGetters = false (不可读)
 * allowSetters = true (可写入)
 * */
@JsonIgnoreProperties(ignoreUnknown = true, value = ["password", "password_salt"], allowGetters = false, allowSetters = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy::class)
data class User(
    var id: Long? = null,
    var username:String? = null,                // 用户名
    var realname:String? = null,                // 真实姓名
    var password:String? = null,                // 密码
    @field:JsonProperty("password_salt") // 数据库字段映射
    var passwordSalt:String? = null,            // 密码校验盐值
    var status: Int = StatusEnum.ALLOW.ordinal  // 状态
): AbstractEntity() {

    override fun tableName() = tableName

    companion object {
        /**
        * 表名
        * */
        const val tableName = "user"

        /**
        * 状态枚举
        * */
        enum class StatusEnum(value: Int) {
           ALLOW(0), // 可用
           DENY(1)   // 禁止
        }
    }
}