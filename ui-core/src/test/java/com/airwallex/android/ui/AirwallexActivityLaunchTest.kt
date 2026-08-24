package com.airwallex.android.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.parcelize.Parcelize
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexActivityLaunchTest {

    @Test
    fun `launchForResult stores locale tag before launch`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val resultLauncher = mockk<ActivityResultLauncher<Intent>>()
        every { resultLauncher.launch(any()) } just runs
        resultLauncherMap()[activity] = resultLauncher

        try {
            TestActivityLaunch(activity).launchForResult(TestArgs("fr-CA")) { _, _ -> }

            assertEquals(
                "fr-CA",
                AirwallexLocalePrefs.getLocale(activity.applicationContext)?.toLanguageTag()
            )
        } finally {
            AirwallexLocalePrefs.setLocaleTag(activity.applicationContext, null)
            resultLauncherMap().remove(activity)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun resultLauncherMap(): MutableMap<Activity, ActivityResultLauncher<Intent>> {
        val field = AirwallexActivityLaunch::class.java.getDeclaredField("resultLauncherMap")
        field.isAccessible = true
        return field.get(null) as MutableMap<Activity, ActivityResultLauncher<Intent>>
    }

    private class TestActivityLaunch(activity: Activity) :
        AirwallexActivityLaunch<TargetActivity, TestArgs>(
            activity,
            TargetActivity::class.java,
            1
        )

    private class TargetActivity : Activity()

    @Parcelize
    private data class TestArgs(
        override val localeTag: String?
    ) : AirwallexActivityLaunch.Args
}
