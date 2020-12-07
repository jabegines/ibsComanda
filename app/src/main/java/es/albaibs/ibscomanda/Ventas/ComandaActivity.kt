package es.albaibs.ibscomanda.Ventas

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.Dao.GruposVtaDao
import es.albaibs.ibscomanda.Varios.ListaGruposVta
import es.albaibs.ibscomanda.Varios.Mensaje
import es.albaibs.ibscomanda.databinding.ComandaActivityBinding
import org.jetbrains.anko.doAsync
import java.sql.Connection


class ComandaActivity: AppCompatActivity() {
    private lateinit var binding: ComandaActivityBinding
    private lateinit var fRecyclerGrupos: RecyclerView
    private lateinit var fAdptGrupos: GruposVtaRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComandaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarControles()
        prepararRecyclerView()
    }


    private fun inicializarControles() {
        fRecyclerGrupos = binding.rvGruposVta
        fRecyclerGrupos.layoutManager = LinearLayoutManager(this)
    }

    private fun prepararRecyclerView() {

        fAdptGrupos = GruposVtaRvAdapter(getGrupos(), this, object: GruposVtaRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaGruposVta) {
            }
        })

        fRecyclerGrupos.adapter = fAdptGrupos
    }


    private fun getGrupos(): MutableList<ListaGruposVta> {
        return GruposVtaDao.getAllGruposVta(connInf)
    }





}