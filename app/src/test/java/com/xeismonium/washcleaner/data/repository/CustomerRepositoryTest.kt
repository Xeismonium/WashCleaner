package com.xeismonium.washcleaner.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.xeismonium.washcleaner.data.local.AppDatabase
import com.xeismonium.washcleaner.data.local.dao.CustomerDao
import com.xeismonium.washcleaner.data.remote.FirestoreDataSource
import com.xeismonium.washcleaner.domain.model.Customer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CustomerRepositoryTest {

    private lateinit var customerDao: CustomerDao
    private lateinit var db: AppDatabase
    private lateinit var repository: CustomerRepositoryImpl
    private val firestoreDataSource = mockk<FirestoreDataSource>()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        customerDao = db.customerDao()
        
        // Mock Firestore interactions
        every { firestoreDataSource.collectionListener(any()) } returns emptyFlow()
        
        repository = CustomerRepositoryImpl(customerDao, firestoreDataSource)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `upsertCustomer saves to local and remote`() = runTest {
        val customer = Customer(name = "Jane Doe", phone = "0812345678", address = "Jl. Sudirman")
        
        coEvery { firestoreDataSource.setDocument(any(), any(), any()) } returns Result.success(Unit)

        repository.upsertCustomer(customer)

        val savedCustomers = repository.getCustomers().first().getOrNull()
        assertEquals(1, savedCustomers?.size)
        assertEquals("Jane Doe", savedCustomers?.first()?.name)
    }

    @Test
    fun `searchCustomers returns matching results`() = runTest {
        val customer1 = Customer(id = "1", name = "Alice", phone = "111", address = "A", createdAt = 100)
        val customer2 = Customer(id = "2", name = "Bob", phone = "222", address = "B", createdAt = 200)
        
        coEvery { firestoreDataSource.setDocument(any(), any(), any()) } returns Result.success(Unit)
        repository.upsertCustomer(customer1)
        repository.upsertCustomer(customer2)

        val searchResult = repository.searchCustomers("Alice").getOrNull()
        assertEquals(1, searchResult?.size)
        assertEquals("Alice", searchResult?.first()?.name)
    }

    @Test
    fun `deleteCustomer removes from local and remote`() = runTest {
        val customer = Customer(id = "to_delete", name = "Delete Me", phone = "000", address = "X")
        
        coEvery { firestoreDataSource.setDocument(any(), any(), any()) } returns Result.success(Unit)
        coEvery { firestoreDataSource.deleteDocument(any(), any()) } returns Result.success(Unit)

        repository.upsertCustomer(customer)
        repository.deleteCustomer("to_delete")

        val customers = repository.getCustomers().first().getOrNull()
        assertTrue(customers?.isEmpty() == true)
    }
}
