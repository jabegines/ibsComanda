package es.albaibs.ibscomanda.Ventas

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.Dao.ArticulosDao
import es.albaibs.ibscomanda.Dao.CuentasDao
import es.albaibs.ibscomanda.Dao.GruposVtaDao
import es.albaibs.ibscomanda.Dao.LineasDao
import es.albaibs.ibscomanda.Varios.DatosCabecera
import es.albaibs.ibscomanda.Varios.DatosLinea
import es.albaibs.ibscomanda.Varios.ListaArticulosGrupo
import es.albaibs.ibscomanda.Varios.ListaGruposVta
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
    private val connGes: Connection = DBConnection.connectionGES as Connection

    private var fCuentaAbierta: Boolean = false
    private var fSala: Short = 0
    private var fMesa: Short = 0
    private var fLinea: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComandaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        fSala = i.getStringExtra("sala").toShort()
        fMesa = i.getStringExtra("mesa").toShort()

        inicializarControles()
        prepararGruposVta()
    }


    private fun inicializarControles() {
        // Comprobamos si hay alguna linea en la cuenta, en cuyo caso actualizamos fLinea. Si no, añadimos a la cabecera.
        doAsync {
            fLinea = LineasDao.getUltimaLinea(connGes, fSala, fMesa)

            if (fLinea == 0) {
                val registro = DatosCabecera()
                registro.sala = fSala
                registro.mesa = fMesa
                registro.fraccion = 0

                CuentasDao.nuevaCabecera(connGes, registro)
                fLinea = 1
            }
        }
        fRecycler = binding.rvComanda
    }


    private fun prepararGruposVta() {
        btnGruposVta.visibility = View.GONE
        setRVGrupos()
    }

    private fun setRVGrupos() {
        fAdptGrupos = GruposVtaRvAdapter(getGrupos(), this, object : GruposVtaRvAdapter.OnItemClickListener {
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
        fAdptArticulos = ArticulosGrupoRvAdapter(getArticulos(queGrupo), this, object : ArticulosGrupoRvAdapter.OnItemClickListener {
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
        var resultado = true

        doAsync {
            val registro = DatosLinea()
            registro.sala = fSala
            registro.mesa = fMesa
            registro.fraccion = 0
            registro.linea = fLinea
            registro.orden = fLinea
            registro.articuloId = data.articuloId


        }

        //fRecycler.adapter?.notifyDataSetChanged()
    }

    fun volverAGrupos(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador
        prepararGruposVta()
    }

}