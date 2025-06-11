package com.example.runplusplus.view

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.runplusplus.R
import com.example.runplusplus.adapter.AllenamentiPrecaricatiAdapter
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllenamentiPrecaricatiActivityTest {

    private lateinit var scenario: ActivityScenario<AllenamentiPrecaricatiActivity>
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        // Pulisce le SharedPreferences per ripristinare lo stato "nessun preferito"
        val prefs = context.getSharedPreferences("preferiti_precaricati", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        // Imposta la schermata iniziale (evitiamo toast che possono bloccare il test)
        val appPrefs = context.getSharedPreferences("impostazioni_app", Context.MODE_PRIVATE)
        appPrefs.edit().putString("schermata_iniziale", "").commit()

        // Avvia l'Activity
        scenario = ActivityScenario.launch(Intent(context, AllenamentiPrecaricatiActivity::class.java))
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testViewsAreDisplayed() {
        // Toolbar
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
        // Spinner categoria
        onView(withId(R.id.spinnerCategoria))
            .check(matches(isDisplayed()))
        // EditText filtro nome
        onView(withId(R.id.inputNomeFiltro))
            .check(matches(isDisplayed()))
        // Spinner difficoltà filtro
        onView(withId(R.id.spinnerDifficoltaFiltro))
            .check(matches(isDisplayed()))
        // Button Cerca
        onView(withId(R.id.btnCerca))
            .check(matches(isDisplayed()))
        // RecyclerView
        onView(withId(R.id.recyclerAllenamenti))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDefaultCategoryIsCorsa_andRecyclerPopulated() {
        // Controlla che la categoria di default sia "Corsa"
        onView(withId(R.id.spinnerCategoria))
            .check(matches(withSpinnerText(containsString("Corsa"))))

        // Verifica che almeno il primo elemento della RecyclerView contenga "Corsa base"
        onView(withId(R.id.recyclerAllenamenti))
            .perform(RecyclerViewActions.scrollTo<AllenamentiPrecaricatiAdapter.ViewHolder>(
                hasDescendant(withText("Corsa base"))
            ))
            .check(matches(hasDescendant(withText("Corsa base"))))
    }

    @Test
    fun testFilterByName_andDifficulty() {
        // Cambia categoria in "Ginnastica"
        onView(withId(R.id.spinnerCategoria)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Ginnastica"))).perform(click())

        // Inserisci "Yoga" nel filtro nome
        onView(withId(R.id.inputNomeFiltro)).perform(typeText("Yoga"), closeSoftKeyboard())

        // Scegli difficoltà "Facile"
        onView(withId(R.id.spinnerDifficoltaFiltro)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Facile"))).perform(click())

        // Premi Cerca
        onView(withId(R.id.btnCerca)).perform(click())

        // Verifica che nella lista compaia "Yoga Relax"
        onView(withId(R.id.recyclerAllenamenti))
            .check(matches(hasDescendant(withText("Yoga Relax"))))

        // Verifica che non compaia un allenamento con difficoltà diversa
        onView(withId(R.id.recyclerAllenamenti))
            .check(matches(not(hasDescendant(withText("Core Killer")))))
    }
}