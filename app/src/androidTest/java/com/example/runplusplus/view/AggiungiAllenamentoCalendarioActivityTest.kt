package com.example.runplusplus.view

import android.content.Intent
import android.widget.EditText
import android.widget.LinearLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.runplusplus.R
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AggiungiAllenamentoCalendarioActivityTest {

    private fun launchActivity(): ActivityScenario<AggiungiAllenamentoCalendarioActivity> {
        val intent = Intent(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            AggiungiAllenamentoCalendarioActivity::class.java
        )
        intent.putExtra("data", LocalDate.now().toString()) // necessario
        return ActivityScenario.launch(intent)
    }

    @Test
    fun testActivityLaunchesCorrectly() {
        launchActivity()
        onView(withId(R.id.spinnerTipo)).check(matches(isDisplayed()))
        onView(withId(R.id.inputOra)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSalva)).check(matches(isDisplayed()))
    }

    @Test
    fun testTipoAllenamentoCambiaDettagli() {
        launchActivity()

        // Seleziona "Corsa"
        onView(withId(R.id.spinnerTipo)).perform(click())
        onView(withText("Corsa")).perform(click())

        // Controlla che i campi "Chilometri" e "Velocità media (km/h)" siano visibili
        onView(withHint("Chilometri")).check(matches(isDisplayed()))
        onView(withHint("Velocità media (km/h)")).check(matches(isDisplayed()))

        // Seleziona "Pesi"
        onView(withId(R.id.spinnerTipo)).perform(click())
        onView(withText("Pesi")).perform(click())

        // Controlla che ora ci siano altri campi
        onView(withHint("Tipo esercizio")).check(matches(isDisplayed()))
        onView(withHint("Ripetizioni e set")).check(matches(isDisplayed()))
    }

    @Test
    fun testSalvaAllenamento_mostraToast() {
        launchActivity()

        // Seleziona tipo allenamento
        onView(withId(R.id.spinnerTipo)).perform(click())
        onView(withText("Corsa")).perform(click())

        // Compila i dettagli richiesti
        onView(withHint("Chilometri")).perform(typeText("5"))
        onView(withHint("Velocità media (km/h)")).perform(typeText("10"))
        closeSoftKeyboard()

        // Inserisce l'ora
        onView(withId(R.id.inputOra)).perform(click())
        // Viene simulato un clic diretto su ora
        onView(withId(R.id.inputOra)).perform(typeText("08:00"), closeSoftKeyboard()) //simulato con un valore preciso invece di toccare sull'orologio
        closeSoftKeyboard()

        // Clic su "Salva"
        onView(withId(R.id.btnSalva)).perform(click())

        // Verifica che venga mostrato il Toast di successo
        onView(withText("Allenamento salvato!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSalvaAllenamento_mostraErrore() {
        launchActivity()

        // Seleziona tipo
        onView(withId(R.id.spinnerTipo)).perform(click())
        onView(withText("Corsa")).perform(click())

        // Compila i dettagli richiesti
        onView(withHint("Chilometri")).perform(typeText("5"))
        onView(withHint("Velocità media (km/h)")).perform(typeText("10"))
        closeSoftKeyboard()

        // NON Inserisce orario valido

        // Clic su Salva
        onView(withId(R.id.btnSalva)).perform(click())

        // Verifica che venga mostrato il messaggio di errore
        onView(withText("Compila tutti i dettagli!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
}
