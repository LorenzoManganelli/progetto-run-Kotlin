package com.example.runplusplus

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.adapter.RTTAdapter
import com.example.runplusplus.model.RTTProgramma
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RTTAdapterTest {

    private lateinit var adapter: RTTAdapter
    private lateinit var programs: List<RTTProgramma>

    @Mock
    private lateinit var mockOnClickListener: (RTTProgramma) -> Unit

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        //dati di esempio per l'adapter
        programs = listOf(
            RTTProgramma(id = 1, nome = "Programma Yoga", tipologia = "Flessibilità", difficolta = "Principiante", durataGiorni = 20),
            RTTProgramma(id = 2, nome = "Programma HIIT", tipologia = "Cardio", difficolta = "Esperto", durataGiorni = 45)
        )
        adapter = RTTAdapter(programs, mockOnClickListener)
    }

    @Test
    fun getItemCount_returnsCorrectSize() {
        assert(adapter.itemCount == programs.size)
    }

    @Test
    fun onBindViewHolder_bindsDataCorrectly() {
        //simula la creazione della view del singolo elemento
        val parent = RecyclerView(RuntimeEnvironment.getApplication())
        // Imposta un LayoutManager per il parent RecyclerView
        parent.layoutManager = LinearLayoutManager(RuntimeEnvironment.getApplication())
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rtt, parent, false)
        val viewHolder = RTTAdapter.ViewHolder(itemView)

        //associa il primo elemento ai campi della view
        adapter.onBindViewHolder(viewHolder, 0)

        //verifica che i TextView mostrino i dati corretti
        val nomeTextView = itemView.findViewById<TextView>(R.id.textNomeRTT)
        val infoTextView = itemView.findViewById<TextView>(R.id.textInfoRTT)

        assert(nomeTextView.text == "Programma Yoga")
        assert(infoTextView.text == "Flessibilità • Principiante • 20 giorni")
    }

    @Test
    fun onBindViewHolder_setsOnClickListener() {
        //simula la creazione della view del singolo elemento
        val parent = RecyclerView(RuntimeEnvironment.getApplication())
        //imposta un LayoutManager per il parent RecyclerView
        parent.layoutManager = LinearLayoutManager(RuntimeEnvironment.getApplication())
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_rtt, parent, false)
        val viewHolder = RTTAdapter.ViewHolder(itemView)

        //associa il primo elemento
        adapter.onBindViewHolder(viewHolder, 0)

        //simula un click sull'elemento
        itemView.performClick()

        //verifica che il listener sia stato chiamato con il programma corretto
        verify(mockOnClickListener).invoke(programs[0])
    }
}