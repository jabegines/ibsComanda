package es.albaibs.ibscomanda.ventas

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.databinding.SeleccMenuActivityBinding
import es.albaibs.ibscomanda.varios.ListaGruposModif
import kotlinx.android.synthetic.main.selecc_modif_activity.*
import java.sql.Connection


class SeleccMenuActivity: AppCompatActivity() {
    private lateinit var binding: SeleccMenuActivityBinding
    private lateinit var fRecGrupos: RecyclerView
    private lateinit var fRecModif: RecyclerView
    private lateinit var fRecMdSelecc: RecyclerView
    private lateinit var fAdpGrupos: GruposModifRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection

    private var fArticulo = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SeleccMenuActivityBinding.inflate(layoutInflater)
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
        fRecMdSelecc = rvModSelecc

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


}