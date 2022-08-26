package com.woowahan.data.dao

import com.woowahan.data.entity.table.BanchanItemTableEntity

class SpyBanchanDaoImpl(
    private val banchans: MutableList<BanchanItemTableEntity>,
    private val removeConstraintThrowableHash: String,
    private val removeConstraintThrowable: Throwable
): BanchanDao {

    var insertCalledCount: Int = 0
        private set

    override fun insertBanchanItems(banchan: BanchanItemTableEntity) {
        insertCalledCount++
    }

    override fun removeBanchanItems(vararg hash: String): Int {
        var count = 0
        hash.forEach { key ->
            if(key == removeConstraintThrowableHash)
                throw removeConstraintThrowable
            val res = banchans.removeIf { it.hash == key }
            if(res) count++
        }
        return count
    }
}