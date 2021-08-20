import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThreeDSecureViewModelTest {
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val threeDSecureViewModelTest: AndroidViewModel by lazy {
        AndroidViewModel(context)
    }

    @Test
    fun isNotNullTest() {
        assertNotNull(threeDSecureViewModelTest)
    }
}