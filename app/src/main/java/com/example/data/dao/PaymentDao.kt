package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_records ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE invoiceId = :invoiceId ORDER BY timestamp ASC")
    fun getPaymentsForInvoice(invoiceId: Long): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE customerPhone = :phone ORDER BY timestamp DESC")
    fun getPaymentsForCustomer(phone: String): Flow<List<PaymentRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecordEntity): Long
}
