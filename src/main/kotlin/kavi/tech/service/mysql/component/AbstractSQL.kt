package kavi.tech.service.mysql.component

import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

abstract class AbstractSQL<T> {

    private val sql = SQLStatement()

    abstract val self: T

    fun UPDATE(table: String): T {
        sql().statementType = SQLStatement.StatementType.UPDATE
        sql().tables.add(table)
        return self
    }

    fun SET(sets: String): T {
        sql().sets.add(sets)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun SET(vararg sets: String): T {
        sql().sets.addAll(Arrays.asList(*sets))
        return self
    }

    fun INSERT_INTO(tableName: String): T {
        sql().statementType = SQLStatement.StatementType.INSERT
        sql().tables.add(tableName)
        return self
    }

    fun VALUES(columns: String, values: String): T {
        sql().columns.add(columns)
        sql().values.add(values)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun INTO_COLUMNS(vararg columns: String): T {
        sql().columns.addAll(Arrays.asList(*columns))
        return self
    }

    /**
     * @since 3.4.2
     */
    fun INTO_VALUES(vararg values: String): T {
        sql().values.addAll(Arrays.asList(*values))
        return self
    }

    fun BATCH_INSERT_INTO(tableName: String): T {
        sql().statementType = SQLStatement.StatementType.BATCH_INSERT
        sql().tables.add(tableName)
        return self
    }

    fun BATCH_INTO_VALUES(vararg values: Any?): T {
        sql().batch_values.add(Arrays.asList(*values))
        return self
    }

    fun SELECT(columns: String): T {
        sql().statementType = SQLStatement.StatementType.SELECT
        sql().select.add(columns)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun SELECT(vararg columns: String): T {
        sql().statementType = SQLStatement.StatementType.SELECT
        sql().select.addAll(Arrays.asList(*columns))
        return self
    }

    fun SELECT_DISTINCT(columns: String): T {
        sql().distinct = true
        SELECT(columns)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun SELECT_DISTINCT(vararg columns: String): T {
        sql().distinct = true
        SELECT(*columns)
        return self
    }

    fun DELETE_FROM(table: String): T {
        sql().statementType = SQLStatement.StatementType.DELETE
        sql().tables.add(table)
        return self
    }

    fun FROM(table: String): T {
        sql().tables.add(table)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun FROM(vararg tables: String): T {
        sql().tables.addAll(Arrays.asList(*tables))
        return self
    }

    fun JOIN(join: String): T {
        sql().join.add(join)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun JOIN(vararg joins: String): T {
        sql().join.addAll(Arrays.asList(*joins))
        return self
    }

    fun INNER_JOIN(join: String): T {
        sql().innerJoin.add(join)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun INNER_JOIN(vararg joins: String): T {
        sql().innerJoin.addAll(Arrays.asList(*joins))
        return self
    }

    fun LEFT_OUTER_JOIN(join: String): T {
        sql().leftOuterJoin.add(join)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun LEFT_OUTER_JOIN(vararg joins: String): T {
        sql().leftOuterJoin.addAll(Arrays.asList(*joins))
        return self
    }

    fun RIGHT_OUTER_JOIN(join: String): T {
        sql().rightOuterJoin.add(join)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun RIGHT_OUTER_JOIN(vararg joins: String): T {
        sql().rightOuterJoin.addAll(Arrays.asList(*joins))
        return self
    }

    fun OUTER_JOIN(join: String): T {
        sql().outerJoin.add(join)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun OUTER_JOIN(vararg joins: String): T {
        sql().outerJoin.addAll(Arrays.asList(*joins))
        return self
    }

    fun WHERE(conditions: String): T {
        sql().where.add(conditions)
        sql().lastList = sql().where
        return self
    }

    /**
     * @since 3.4.2
     */
    fun WHERE(vararg conditions: String): T {
        sql().where.addAll(Arrays.asList(*conditions))
        sql().lastList = sql().where
        return self
    }

    fun OR(): T {
        sql().lastList.add(OR)
        return self
    }

    fun AND(): T {
        sql().lastList.add(AND)
        return self
    }

    fun GROUP_BY(columns: String): T {
        sql().groupBy.add(columns)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun GROUP_BY(vararg columns: String): T {
        sql().groupBy.addAll(Arrays.asList(*columns))
        return self
    }

    fun HAVING(conditions: String): T {
        sql().having.add(conditions)
        sql().lastList = sql().having
        return self
    }

    /**
     * @since 3.4.2
     */
    fun HAVING(vararg conditions: String): T {
        sql().having.addAll(Arrays.asList(*conditions))
        sql().lastList = sql().having
        return self
    }

    fun ORDER_BY(columns: String): T {
        sql().orderBy.add(columns)
        return self
    }

    /**
     * @since 3.4.2
     */
    fun ORDER_BY(vararg columns: String): T {
        sql().orderBy.addAll(Arrays.asList(*columns))
        return self
    }

    private fun sql(): SQLStatement {
        return sql
    }

    fun <A : Appendable> usingAppender(a: A): A {
        sql().sql(a)
        return a
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sql().sql(sb)
        return sb.toString()
    }

    private class SafeAppendable(private val a: Appendable) {
        var isEmpty = true
            private set

        fun append(s: CharSequence): SafeAppendable {
            try {
                if (isEmpty && s.length > 0) {
                    isEmpty = false
                }
                a.append(s)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            return this
        }

    }

    private class SQLStatement {

        enum class StatementType {
            DELETE, INSERT, SELECT, UPDATE, BATCH_INSERT
        }

        internal var statementType: StatementType? = null
        internal var sets: MutableList<String> = ArrayList()
        internal var select: MutableList<String> = ArrayList()
        internal var tables: MutableList<String> = ArrayList()
        internal var join: MutableList<String> = ArrayList()
        internal var innerJoin: MutableList<String> = ArrayList()
        internal var outerJoin: MutableList<String> = ArrayList()
        internal var leftOuterJoin: MutableList<String> = ArrayList()
        internal var rightOuterJoin: MutableList<String> = ArrayList()
        internal var where: MutableList<String> = ArrayList()
        internal var having: MutableList<String> = ArrayList()
        internal var groupBy: MutableList<String> = ArrayList()
        internal var orderBy: MutableList<String> = ArrayList()
        internal var lastList: MutableList<String> = ArrayList()
        internal var columns: MutableList<String> = ArrayList()
        internal var values: MutableList<String> = ArrayList()
        internal var batch_values: MutableList<List<Any?>> = ArrayList()
        internal var distinct: Boolean = false

        private fun sqlClause(builder: SafeAppendable, keyword: String, parts: List<String>, open: String, close: String,
                              conjunction: String) {
            if (!parts.isEmpty()) {
                if (!builder.isEmpty) {
                    builder.append(" ")
                }
                builder.append(keyword)
                builder.append(" ")
                builder.append(open)
                var last = "________"
                var i = 0
                val n = parts.size
                while (i < n) {
                    val part = parts[i]
                    if (i > 0 && part != AND && part != OR && last != AND && last != OR) {
                        builder.append(conjunction)
                    }
                    builder.append(part)
                    last = part
                    i++
                }
                builder.append(close)
            }
        }

        private fun selectSQL(builder: SafeAppendable): String {
            if (distinct) {
                sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ")
            } else {
                sqlClause(builder, "SELECT", select, "", "", ", ")
            }

            sqlClause(builder, "FROM", tables, "", "", ", ")
            joins(builder)
            sqlClause(builder, "WHERE", where, "(", ")", " AND ")
            sqlClause(builder, "GROUP BY", groupBy, "", "", ", ")
            sqlClause(builder, "HAVING", having, "(", ")", " AND ")
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ")
            return builder.toString()
        }

        private fun joins(builder: SafeAppendable) {
            sqlClause(builder, "JOIN", join, "", "", " JOIN ")
            sqlClause(builder, "INNER JOIN", innerJoin, "", "", " INNER JOIN ")
            sqlClause(builder, "OUTER JOIN", outerJoin, "", "", " OUTER JOIN ")
            sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", " LEFT OUTER JOIN ")
            sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", " RIGHT OUTER JOIN ")
        }

        private fun insertSQL(builder: SafeAppendable): String {
            sqlClause(builder, "INSERT INTO", tables, "", "", "")
            sqlClause(builder, "", columns, "(", ")", ", ")
            sqlClause(builder, "VALUES", values, "(", ")", ", ")
            return builder.toString()
        }

        private fun deleteSQL(builder: SafeAppendable): String {
            sqlClause(builder, "DELETE FROM", tables, "", "", "")
            sqlClause(builder, "WHERE", where, "(", ")", " AND ")
            return builder.toString()
        }

        private fun updateSQL(builder: SafeAppendable): String {
            sqlClause(builder, "UPDATE", tables, "", "", "")
            joins(builder)
            sqlClause(builder, "SET", sets, "", "", ", ")
            sqlClause(builder, "WHERE", where, "(", ")", " AND ")
            return builder.toString()
        }

        private fun batchInsertSQL(builder: SafeAppendable): String {
            sqlClause(builder, "INSERT INTO", tables, "", "", "")
            sqlClause(builder, "", columns, "(", ")", ", ")
            sqlClause(builder, "VALUES",
                    batch_values.map {"(${it.let {
                        if(it.size < columns.size) it.plus(arrayOfNulls<Any>(columns.size - it.size))
                        else it.subList(0, columns.size)
                    }.joinToString(",")})"},
                    "", "", ", ")
            return builder.toString()
        }

        fun sql(a: Appendable): String? {
            val builder = SafeAppendable(a)
            if (statementType == null) {
                return null
            }

            val answer: String?

            when (statementType) {
                StatementType.DELETE -> answer = deleteSQL(builder)

                StatementType.INSERT -> answer = insertSQL(builder)

                StatementType.SELECT -> answer = selectSQL(builder)

                StatementType.UPDATE -> answer = updateSQL(builder)

                StatementType.BATCH_INSERT -> answer = batchInsertSQL(builder)

                else -> answer = null
            }

            return answer
        }
    }// Prevent Synthetic Access

    companion object {

        private val AND = ") AND ("
        private val OR = ") OR ("
    }
}