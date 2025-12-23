package com.xeismonium.washcleaner.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xeismonium.washcleaner.data.local.database.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.database.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionServiceDao
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionServiceEntity

@Database(
    entities = [
        CustomerEntity::class,
        ServiceEntity::class,
        LaundryTransactionEntity::class,
        TransactionServiceEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class WashCleanerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionServiceDao(): TransactionServiceDao
}

/**
 * Migration from version 2 to 3
 * Replaces isPaid Boolean with paidAmount Double for partial payment tracking
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new transactions table with paidAmount field
        database.execSQL("""
            CREATE TABLE transactions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                customerId INTEGER,
                customerName TEXT,
                totalPrice REAL NOT NULL,
                dateIn INTEGER NOT NULL,
                dateOut INTEGER,
                estimatedDate INTEGER,
                status TEXT NOT NULL,
                paidAmount REAL NOT NULL DEFAULT 0.0,
                FOREIGN KEY(customerId) REFERENCES customers(id) ON DELETE SET NULL
            )
        """)

        // Migrate existing data: isPaid=1 -> paidAmount=totalPrice, isPaid=0 -> paidAmount=0
        database.execSQL("""
            INSERT INTO transactions_new (id, customerId, customerName, totalPrice, dateIn, dateOut, estimatedDate, status, paidAmount)
            SELECT id, customerId, customerName, totalPrice, dateIn, dateOut, estimatedDate, status,
                CASE WHEN isPaid = 1 THEN totalPrice ELSE 0.0 END AS paidAmount
            FROM transactions
        """)

        // Drop old table
        database.execSQL("DROP TABLE transactions")

        // Rename new table
        database.execSQL("ALTER TABLE transactions_new RENAME TO transactions")

        // Recreate indices
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_customerId ON transactions(customerId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_status ON transactions(status)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_dateIn ON transactions(dateIn)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_customerName ON transactions(customerName)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_paidAmount ON transactions(paidAmount)")
    }
}
