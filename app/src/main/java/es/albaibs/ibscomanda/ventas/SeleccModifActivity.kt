package es.albaibs.ibscomanda.ventas

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.dao.GruposModifDao
import es.albaibs.ibscomanda.dao.ModificadoresDao
import es.albaibs.ibscomanda.databinding.SeleccModifActivityBinding
import es.albaibs.ibscomanda.varios.ListaGruposModif
import es.albaibs.ibscomanda.varios.ListaModificadores
import kotlinx.android.synthetic.main.selecc_modif_activity.*
import java.sql.Connection


class SeleccModifActivity: AppCompatActivity() {
    private lateinit var binding: SeleccModifActivityBinding
    private lateinit var fRecGrupos: RecyclerView
    private lateinit var fRecModif: RecyclerView
    private lateinit var fAdpGrupos: GruposModifRvAdapter
    private lateinit var fAdpModif: ModificadoresRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection

    private var fArticulo = 0
    private var fGrupo: Short = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SeleccModifActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        fArticulo = i.getIntExtra("articuloId", 0)

        inicializarControles()
        prepararRecGrupos()
        prepararRecModif()
    }


    private fun inicializarControles() {
        fRecGrupos = rvGruposModif
        fRecModif = rvModif
    }


    private fun prepararRecGrupos() {
        fAdpGrupos = GruposModifRvAdapter(getGrupos(), this, object: GruposModifRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaGruposModif) {
                fGrupo = data.grupoId
                prepararRecModif()
            }
        })

        fRecGrupos.layoutManager = LinearLayoutManager(this)
        fRecGrupos.adapter= fAdpGrupos
    }


    private fun getGrupos(): List<ListaGruposModif> {
        return GruposModifDao.getGruposModif(connInf, fArticulo)
    }

    private fun prepararRecModif() {
        fAdpModif = ModificadoresRvAdapter(getModificadores(), this, object: ModificadoresRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaModificadores) {
            }
        })

        fRecModif.layoutManager = GridLayoutManager(this, 3)
        fRecModif.adapter = fAdpModif
    }

    private fun getModificadores(): List<ListaModificadores> {
        val queLista = ModificadoresDao.getModificadores(connInf, fGrupo)
        return queLista
    }

}