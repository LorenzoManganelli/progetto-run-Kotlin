package com.example.runplusplus.view

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.runplusplus.R
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CalendarioActivityTest {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testCalendarioActivity_launchesCorrectly() {
        ActivityScenario.launch(CalendarioActivity::class.java)
        onView(withId(R.id.calendarRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.monthYearTV)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickOnPastDate_showsToast() {
        ActivityScenario.launch(CalendarioActivity::class.java)

        val pastDate = LocalDate.now().minusDays(1).dayOfMonth.toString()

        // Simula il click su un giorno passato
        onView(withId(R.id.calendarRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(pastDate)), click()
                )
            )

        // Verifica che venga mostrato un Toast (custom matcher, vedi sotto)
        onView(withText("Non puoi selezionare date passate"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testClickOnFutureDate_opensDettagliAllenamenti() {
        ActivityScenario.launch(CalendarioActivity::class.java)

        val futureDate = LocalDate.now().plusDays(1).dayOfMonth.toString()

        // Click su una data futura
        onView(withId(R.id.calendarRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(futureDate)), click()
                )
            )

        // Verifica che venga lanciata l'Activity corretta
        Intents.intended(hasComponent(DettagliAllenamentiCalendarioActivity::class.java.name))
    }

    // Funzione di supporto per recuperare il testo
    private fun getText(resId: Int): String {
        val scenario = ActivityScenario.launch(CalendarioActivity::class.java)
        var text = ""
        scenario.onActivity {
            text = it.findViewById<android.widget.TextView>(resId).text.toString()
        }
        return text
    }
}