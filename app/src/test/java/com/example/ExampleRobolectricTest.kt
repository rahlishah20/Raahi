package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.DestinationRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("RAAHI", appName)
    }

    @Test
    fun `karachi destination search returns relevant results`() = runBlocking {
        val repository = DestinationRepositoryImpl()

        val results = repository.searchDestinations("Clifton").first()
        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.name.contains("Clifton", ignoreCase = true) })
    }

    @Test
    fun `quick shortcuts returns office, home, and cafe in Karachi`() = runBlocking {
        val repository = DestinationRepositoryImpl()

        val shortcuts = repository.getQuickShortcuts().first()
        assertEquals(3, shortcuts.size)
        assertNotNull(shortcuts.firstOrNull { it.id == "quick-office" })
    }
}
