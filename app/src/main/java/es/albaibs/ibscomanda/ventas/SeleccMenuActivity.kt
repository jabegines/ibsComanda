package es.albaibs.ibscomanda.ventas

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.dao.GruposModifDao
import es.albaibs.ibscomanda.dao.ModificadoresDao
import es.albaibs.ibscomanda.databinding.SeleccMenuActivityBinding
import es.albaibs.ibscomanda.varios.ListaGruposModif
import es.albaibs.ibscomanda.varios.ListaModificadores
import kotlinx.android.synthetic.main.selecc_modif_activity.*
import java.sql.Connection


class SeleccMenuActivity: AppCompatActivity() {
    private lateinit var binding: SeleccMenuActivityBinding
    private lateinit var fRecGrupos: RecyclerView
    private lateinit var fRecModif: RecyclerView
    private lateinit var fRecMdSelecc: RecyclerView
    private lateinit var fAdpGrupos: GruposModifRvAdapter
    private lateinit var fAdpModif: ModificadoresRvAdapter
    private lateinit var fAdpMdSelecc: ModifSeleccRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection

    private var fArticulo = 0
    private var fGrupo: Short = 0
    private lateinit var lArtMenuSelecc: MutableList<ListaModificadores>


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

        lArtMenuSelecc = emptyList<ListaModificadores>().toMutableList()
    }

    private fun prepararRecGrupos() {
        fAdpGrupos = GruposModifRvAdapter(getGrupos(), this, object: GruposModifRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaGruposModif) {
                fGrupo = data.grupoId
                prepararRecModif()
            }
        })
        fAdpGrupos.selectedPos = 0

        fRecGrupos.layoutManager = LinearLayoutManager(this)
        fRecGrupos.adapter= fAdpGrupos
    }


    private fun getGrupos(): List<ListaGruposModif> {
        val queLista = GruposModifDao.getGruposModif(connInf, fArticulo)
        fGrupo = queLista[0].grupoId
        return queLista
    }


    private fun prepararRecModif() {
        fAdpModif = ModificadoresRvAdapter(getModificadores(), this, object: ModificadoresRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaModificadores) {
                lArtMenuSelecc.add(data)
                prepararModSelecc()
            }
        })

        fRecModif.layoutManager = GridLayoutManager(this, 4)
        fRecModif.adapter = fAdpModif
    }

    private fun getModificadores(): List<ListaModificadores> {
        return ModificadoresDao.getModificadores(connInf, fGrupo)
    }


    private fun prepararModSelecc() {
        fAdpMdSelecc = ModifSeleccRvAdapter(lArtMenuSelecc, this, object: ModifSeleccRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaModificadores) {
            }
        })

        fRecMdSelecc.layoutManager = GridLayoutManager(this, 4)
        fRecMdSelecc.adapter = fAdpMdSelecc
    }


    fun borrarArtMenu(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        if (fAdpMdSelecc.selectedPos > -1) {
            lArtMenuSelecc.removeAt(fAdpMdSelecc.selectedPos)
            fAdpMdSelecc.notifyDataSetChanged()
            fAdpMdSelecc.selectedPos = -1
        }
    }


    fun cancelarMenu(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }


    fun aceptarArtMenu(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        try {
            var x = 1
            val returnIntent = Intent()
            returnIntent.putExtra("numArticulos", lArtMenuSelecc.size)
            for (modificador in lArtMenuSelecc) {
                val mArrayList: MutableList<String> = ArrayList()
                mArrayList.add(modificador.grupoModif.toString())
                mArrayList.add(modificador.modificador)
                mArrayList.add(modificador.esArticulo)
                mArrayList.add(modificador.dosis)
                mArrayList.add(modificador.incrPrecio)
                mArrayList.add(modificador.descripcion)
                val lArrayList = mArrayList as ArrayList<String>?
                returnIntent.putStringArrayListExtra("listaArtMenu$x", lArrayList)
                x++
            }

            setResult(RESULT_OK, returnIntent)
            finish()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}