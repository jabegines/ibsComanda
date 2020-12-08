package es.albaibs.ibscomanda.Ventas

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.Dao.ArticulosDao
import es.albaibs.ibscomanda.Dao.GruposVtaDao
import es.albaibs.ibscomanda.Varios.ListaArticulosGrupo
import es.albaibs.ibscomanda.Varios.ListaGruposVta
import es.albaibs.ibscomanda.Varios.Mensaje
import es.albaibs.ibscomanda.databinding.ComandaActivityBinding
import kotlinx.android.synthetic.main.comanda_activity.*
import org.jetbrains.anko.doAsync
import java.sql.Connection


class ComandaActivity: AppCompatActivity() {
    private lateinit var binding: ComandaActivityBinding
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdptGrupos: GruposVtaRvAdapter
    private lateinit var fAdptArticulos: ArticulosGrupoRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComandaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarControles()
        prepararGruposVta()
    }


    private fun inicializarControles() {
        fRecycler = binding.rvComanda
        //fRecycler.layoutManager = GridLayoutManager(this, 2) //LinearLayoutManager(this)
    }


    private fun prepararGruposVta() {
        btnGruposVta.visibility = View.GONE
        setRVGrupos()
    }

    private fun setRVGrupos() {
        fAdptGrupos = GruposVtaRvAdapter(getGrupos(), this, object: GruposVtaRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaGruposVta) {
                prepararArticulosGrupo(data.grupoId)
            }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 4)
        fRecycler.adapter = fAdptGrupos
    }


    private fun getGrupos(): MutableList<ListaGruposVta> {
        return GruposVtaDao.getAllGruposVta(connInf)
    }


    private fun prepararArticulosGrupo(queGrupo: Int) {
        btnGruposVta.visibility = View.VISIBLE
        setRVArticulos(queGrupo)
    }


    private fun setRVArticulos(queGrupo: Int) {
        fAdptArticulos = ArticulosGrupoRvAdapter(getArticulos(queGrupo), this, object: ArticulosGrupoRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaArticulosGrupo) {
                vender(data)
            }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 3)
        fRecycler.adapter = fAdptArticulos
    }

    private fun getArticulos(queGrupo: Int): MutableList<ListaArticulosGrupo> {
        return ArticulosDao.getArticulosGrupo(connInf, queGrupo)
    }

    private fun vender(data: ListaArticulosGrupo) {
        data.cantidad++
        fRecycler.adapter?.notifyDataSetChanged()
    }

    fun volverAGrupos(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador
        prepararGruposVta()
    }

}