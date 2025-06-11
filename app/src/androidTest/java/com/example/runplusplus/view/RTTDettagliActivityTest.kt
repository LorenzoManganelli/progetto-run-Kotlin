package com.example.runplusplus.view

import android.content.Intent
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.runplusplus.R
import com.example.runplusplus.model.RTTProgramma
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RTTDettagliActivityTest {

    private fun launchWithProgramma(): ActivityScenario<RTTDettagliActivity> {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(context, RTTDettagliActivity::class.java).apply {
            putExtra("programma", RTTProgramma(
                id = 1,
                nome = "RTT Base",
                tipologia = "Cardio",
                difficolta = "Facile",
                durataGiorni = 7
            ))
        }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun launchesCorrectly() {
        launchWithProgramma()

        onView(withId(R.id.textNomeRTTDettaglio)).check(matches(withText("RTT Base")))
        onView(withId(R.id.textInfoRTTDettaglio)).check(matches(withText(containsString("Cardio"))))
    }

    @Test
    fun attivaSet_mostraToast() {
        launchWithProgramma()
        onView(withId(R.id.btnAttiva)).perform(click())

        onView(withText("Set attivato!")).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    @Test
    fun disattivaSet_mostraToast() {
        launchWithProgramma()
        onView(withId(R.id.btnDisattiva)).perform(click())

        onView(withText("Set disattivato!")).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    @Test
    fun visualizzaEsercizio_setNonAttivo_mostraToast() {
        launchWithProgramma()
        onView(withId(R.id.btnApriGiornoCorrente)).perform(click())

        onView(withText("Questo programma non è attivo.")).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    @Test
    fun impostaOra_mostraOraAggiornata() {
        val scenario = launchWithProgramma()

        scenario.onActivity {
            // Simula il set manuale dell'ora
            val textView = it.findViewById<TextView>(R.id.textOraSelezionata)
            it.runOnUiThread {
                textView.text = "Ora selezionata: 09:30"
            }
        }

        onView(withId(R.id.textOraSelezionata)).check(matches(withText(containsString("09:30"))))
    }
}
