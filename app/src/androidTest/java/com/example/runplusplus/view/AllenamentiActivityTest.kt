package com.example.runplusplus.view

import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.model.Allenamento
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before
import org.junit.After
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class AllenamentiActivityTest {

    @Before
    fun setUp() {
        Intents.init()  // inizializza Intents per intercettare gli intent
    }

    @After
    fun tearDown() {
        Intents.release()  // rilascia Intents dopo il test
    }

    @Test
    fun onCreate_shouldSetupToolbarAndRecyclerView() {
        ActivityScenario.launch(AllenamentiActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val actionBar = activity.supportActionBar
                assertEquals("Run++", actionBar?.title)
                assertEquals("Allenamenti", actionBar?.subtitle)

                val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerViewAllenamenti)
                assertNotNull("RecyclerView mancante", recyclerView)

                val btnTipo = activity.findViewById<Button>(R.id.btnOrdinaTipo)
                assertNotNull("Bottone 'Ordina per Tipo' mancante", btnTipo)
            }
        }
    }

    @Test
    fun clickFab_shouldStartNuovoAllenamentoActivity() {
        ActivityScenario.launch(AllenamentiActivity::class.java).use { scenario ->

            // Usa Espresso per cliccare il FAB
            onView(withId(R.id.fabAggiungiAllenamento)).perform(click())

            // Verifica che sia partito l'intent verso NuovoAllenamentoActivity
            intended(hasComponent(NuovoAllenamentoActivity::class.java.name))
        }
    }
}
