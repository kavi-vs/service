package kavi.tech.service.common.strategry

import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray

interface HashStrategy {

    /**
     * 生成随机盐值
     */
    fun generateSalt(): String

    /**
     * 根据未加密的密码和salt计算哈希密码
     */
    fun computeHash(password: String, salt: String, version: Int): String

    /**
     * 从身份验证查询的结果中检索salt
     */
    fun salt(row: JsonArray): String

    /**
     * 设置每个位置对应于版本的nonce的有序列表。
     * nonce不应该存储在数据库存储中，而是作为应用程序配置提供。这个想法是在hash函数中添加一个额外的变量，
     * 以便使用rainbow表或预计算的hash更难破解密码。只让攻击者使用暴力手段。nonce依赖于实现。
     * E、 g.：对于SHA512，它们是散列过程中使用的额外盐，对于PBKDF2，它们映射算法应该进行的迭代次数
     *
     */

    var nonces: JsonArray?

    /**
     * nonces版本校验
     * */
    fun version(password: String, version: Int = -1): Int

    /**
     * 时间常数字符串比较以避免timming攻击。
     */
    fun isEqual(hasha: String, hashb: String): Boolean {
        var diff = hasha.length xor hashb.length
        var i = 0
        while (i < hasha.length && i < hashb.length) {
            diff = diff or (hasha[i].toInt() xor hashb[i].toInt())
            i++
        }
        return diff == 0
    }

    companion object {

        /**
         * 这是当前向后兼容的哈希实现，新应用程序应该以PBKDF2实现，除非安全性和CPU使用率之间的权衡是一个选项。
         */
        fun createSHA512(vertx: Vertx): HashStrategy {
            return SHA512Strategy(vertx)
        }

        /**
         * PBKDF2 策略
         */
        fun createPBKDF2(vertx: Vertx): HashStrategy {
            return PBKDF2Strategy(vertx)
        }

    }
}
