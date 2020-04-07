package kavi.tech.service.mysql.component

import java.io.Serializable
import java.util.*
import java.util.regex.Pattern

open class SQL : AbstractSQL<SQL>() {

    override val self: SQL
        get() = this
    /**
     * 插入内容转换及过滤格式化
     * @author sili | 2017/11/3
     */
    fun SET(pair: Pair<String, Any?>): SQL {
        super.SET(PairFormat(pair))
        return this
    }

    /**
     * 值过滤及格式化
     * @author sili | 2017/11/3
     */
    fun VALUES(columns : String, values : Any?) : SQL {
        super.VALUES(columns, ValueFormat(values))
        return this
    }

    /**
     * 条件内容转换及过滤格式化
     * @author sili | 2017/11/3
     */
    fun WHERE(where : Pair<String, Any?>) : SQL {
        this.WHERE(PairFormat(where))
        return this
    }

    /**
     * 元组条件【字段 ， 判断方式， 值】
     * */
    fun WHERE(where : Triple<String, String, Any?>) : SQL {
        super.WHERE(FormatWhere(where))
        return this
    }

    /**
     * 多态序列号查询
     * */
    fun WHERES(wheres: List<Serializable>) : SQL {
        wheres.forEach {
            when(it) {
                is Pair<*, *> -> WHERE(it as Pair<String, Any?>)
                is Triple<*, *, *> -> WHERE(it as Triple<String, String, Any?>)
            }
        }
        return this
    }


    /**
     * 复数内容转SQL字符串
     * @author sili | 2017/11/3
     */
    fun PairFormat(pair: Pair<String, Any?>) : String {
        return FormatWhere(when(pair.second) {
            null -> Triple(pair.first, "IS", pair.second)
            else -> Triple(pair.first, "=", pair.second)
        })
    }


    /**
     * 格式化判断条件
     * */
    fun FormatWhere(where : Triple<String, String, Any?>) : String{
        val method = formatMethod(where.second)
        if (method == "FIND_IN_SET") {
            return "FIND_IN_SET(${where.first}, ${formatValue(where.third)})"
        }
        val value = when(method) {
            "IN" -> "(${toArray(where.third)})"
            "NOT IN" -> "(${toArray(where.third)})"
            "EXISTS" -> "(${toArray(where.third)})"
            "NOT EXISTS" -> "(${toArray(where.third)})"
            "BETWEEN" -> toBetween(where.third)
            "NOT BETWEEN" -> toBetween(where.third)
            "IS" -> "NULL"
            "IS NOT" -> "NULL"
            else -> formatValue(where.third)
        }
        return "${formatField(where.first)} $method $value"
    }

    /**
     * OR多条件
     * @author sili | 2017/11/10
     */
    fun ORWHERE(vararg conditions: Pair<String, String?>): SQL {
        val orWhere = Arrays.asList(*conditions).map{PairFormat(it)}.joinToString(" OR ")
        super.WHERE("(${orWhere})")
        return this
    }

    /**
     * OR多条件 数组形态
     * @author sili | 2017/11/10
     */
    fun ORWHERE(listConditions: ArrayList<Pair<String, String?>>): SQL {
        val orWhere = listConditions.map{PairFormat(it)}.joinToString(" OR ")
        super.WHERE("(${orWhere})")
        return this
    }

    fun ORWHERE(listConditions: List<String>): SQL {
        val orWhere = listConditions.joinToString(" OR ")
        super.WHERE("(${orWhere})")
        return this
    }



    /**
     * 字符转义
     * todo 修复json转换导致编码错误
     * */
    private fun xssEncode(value: String): String {
        val str = stripXSSAndSql(value)
        val sb = StringBuilder(str.length + 16)
        for (i in 0 until str.length) {
            val c = str[i]
            when (c) {
                '>' -> sb.append("＞") // 转义大于号
                '<' -> sb.append("＜") // 转义小于号
                '\'' -> sb.append("＇")// 转义单引号
                '\"' -> sb.append("＂")// 转义双引号
                '&' -> sb.append("＆") // 转义&
                '#' -> sb.append("＃") // 转义#
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * sql语句过滤
     * */
    private fun stripXSSAndSql(str: String): String {
        var value = str
        var scriptPattern = Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE)
        value = scriptPattern.matcher(value).replaceAll("")
        // Avoid anything in a src="http://..." type of e-xpression
        scriptPattern = Pattern.compile("src[\r\n| | ]*=[\r\n| | ]*[\\\"|\\\'](.*?)[\\\"|\\\']", Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
        value = scriptPattern.matcher(value).replaceAll("")
        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE)
        value = scriptPattern.matcher(value).replaceAll("")
        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<[\r\n| | ]*script(.*?)>", Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
        value = scriptPattern.matcher(value).replaceAll("")
        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
        value = scriptPattern.matcher(value).replaceAll("")
        // Avoid e-xpression(...) expressions
        scriptPattern = Pattern.compile("e-xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
        value = scriptPattern.matcher(value).replaceAll("")
        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE)
        value = scriptPattern.matcher(value).replaceAll("")
        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE)
        value = scriptPattern.matcher(value).replaceAll("")
        // Avoid onload= expressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)
        value = scriptPattern.matcher(value).replaceAll("")
        return value
    }


    companion object {
        /**
         * 静态构造
         * */
        fun init(body: SQL.()->Unit) = object : SQL() { init{ body(this) } }.toString()

        /**
         * 格式化字段
         * */
        fun formatField(field: String): String{
            return "`$field`"
        }

        /**
         * 格式化判断方式
         * */
        fun formatMethod(method: String): String{
            return method
        }

        /**
         * 格式化值
         * */
        fun formatValue(value: Any?): String{
            return ValueFormat(value)
        }

        /**
         * 值转换至分割字符串
         */
        fun toArray(value: Any?):String{
            return when(value) {
                is Array<*> -> {
                    value.mapNotNull { item ->
                        item?.let {
                            isNumber(it)
                                ?: formatValue(it)
                        }
                    }.joinToString(",")
                }
                else -> value.toString()
            }
        }

        /**
         * 转换至BETWEEN格式
         * */
        fun toBetween(value: Any?):String{
            return when(value) {
                is Pair<Any?, Any?> -> {
                    " ${isNumber(value.first)
                        ?: formatValue(value.first)} AND ${isNumber(
                        value.second
                    ) ?: formatValue(value.second)} "
                }
                else -> value.toString()
            }
        }

        /**
         * 判断返回数字类型
         * */
        fun isNumber(value: Any?): Number? {
            return when(value) {
                is Number -> value
                else -> null
            }
        }

        /**
         * 格式化参数
         * @author sili | 2017/11/3
         */
        fun <T> ValueFormat(value : T) : String {
            return when(value){
                is String -> "'${ValueFilter(value.toString())}'"
                is Enum<*> -> "'${ value as Enum<*>}'"
                is Array<*> -> {
                    val arrValue = value as Array<*>
                    return "'${arrValue.map{ ValueFilter(it.toString().trim()) }.joinToString(",")}'"
                }
                else -> ValueFilter(value.toString())
            }
        }

        /**
         * 内容过滤 todo SQL注入过滤，特殊符号转义
         * @author sili | 2017/11/3
         */
        fun ValueFilter(value : String) : String {
            return value
        }

        /**
         * 正则匹配第一个内容
         * @author sili | 2017/11/9
         */
        fun MatchRgex(soap: String, rgex: String): String {
            val pattern = Pattern.compile(rgex)
            val m = pattern.matcher(soap)
            while (m.find()) {
                return m.group(1)
            }
            return ""
        }
    }
}