package es.albaibs.ibscomanda.ventas

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.dao.*
import es.albaibs.ibscomanda.databinding.ComandaActivityBinding
import es.albaibs.ibscomanda.varios.*
import kotlinx.android.synthetic.main.comanda_activity.*
import org.jetbrains.anko.doAsync
import java.sql.Connection


class ComandaActivity: AppCompatActivity() {
    private lateinit var binding: ComandaActivityBinding
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdptGrupos: GruposVtaRvAdapter
    private lateinit var fAdptArticulos: ArticulosGrupoRvAdapter
    private lateinit var fAdptCuenta: CuentasRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection
    private val connGes: Connection = DBConnection.connectionGES as Connection

    private var fVistaActual: Int  = 1
    private var fVistaAnterior: Int = 1
    private var fGrupoActual: Int = 0
    private var fSala: Short = 0
    private var fMesa: Short = 0
    private var fLinea: Int = 0
    private var fTarifa: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComandaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fTarifa = ConfiguracionesDao.getEntero(connInf, "HOSTELERIA", "TARIFAPRECIOS")
        val i = intent
        fSala = i.getStringExtra("sala").toShort()
        fMesa = i.getStringExtra("mesa").toShort()

        inicializarControles()
        prepararGruposVta()
    }


    private fun inicializarControles() {
        binding.btnMesa.text = fMesa.toString()

        // Comprobamos si hay alguna linea en la cuenta, en cuyo caso actualizamos fLinea. Si no, añadimos a la cabecera.
        doAsync {
            fLinea = LineasDao.getUltimaLinea(connGes, fSala, fMesa)

            if (fLinea == 1) {
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
        fVistaAnterior = fVistaActual
        fVistaActual = VIENDO_GRUPOS
        binding.btnGrupos.visibility = View.GONE
        btnVerCuenta.setText(R.string.ver_cuenta)
        btnVerCuenta.setCompoundDrawablesWithIntrinsicBounds(null, ResourcesCompat.getDrawable(resources, R.drawable.cuenta, null), null, null)
        setRVGrupos()
    }

    private fun setRVGrupos() {
        fAdptGrupos = GruposVtaRvAdapter(getGrupos(), this, object : GruposVtaRvAdapter.OnItemClickListener {
                override fun onClick(view: View, data: ListaGruposVta) {
                    prepararArticulosGrupo(data.grupoId)
                }
            })

        fRecycler.layoutManager = GridLayoutManager(this, 3)
        fRecycler.adapter = fAdptGrupos
    }


    private fun getGrupos(): MutableList<ListaGruposVta> {
        return GruposVtaDao.getAllGruposVta(connInf)
    }


    private fun prepararArticulosGrupo(queGrupo: Int) {
        fVistaAnterior = fVistaActual
        fVistaActual = VIENDO_ARTICULOS
        fGrupoActual = queGrupo
        binding.btnGrupos.visibility = View.VISIBLE
        btnVerCuenta.setText(R.string.ver_cuenta)
        btnVerCuenta.setCompoundDrawablesWithIntrinsicBounds(null, ResourcesCompat.getDrawable(resources, R.drawable.cuenta, null), null, null)
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

    private fun prepararCuenta() {
        fVistaAnterior = fVistaActual
        fVistaActual = VIENDO_CUENTA
        btnVerCuenta.setText(R.string.vender)
        btnVerCuenta.setCompoundDrawablesWithIntrinsicBounds(null, ResourcesCompat.getDrawable(resources, R.drawable.vino, null), null, null)
        setRVCuenta()
    }

    private fun setRVCuenta() {
        fAdptCuenta = CuentasRvAdapter(getLineasCuenta(),this, object: CuentasRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaLineasCuenta) {
            }
        })

        fRecycler.layoutManager = LinearLayoutManager(this)
        fRecycler.adapter = fAdptCuenta
    }

    private fun getLineasCuenta(): MutableList<ListaLineasCuenta> {
        return CuentasDao.getLineasCuenta(connInf, fSala, fMesa)
    }

    private fun vender(data: ListaArticulosGrupo) {
        //var resultado = true

        doAsync {
            val registro = DatosLinea()
            registro.sala = fSala
            registro.mesa = fMesa
            registro.fraccion = 0
            registro.linea = fLinea
            registro.orden = fLinea
            registro.articuloId = data.articuloId
            registro.codigoArt = data.codigo
            registro.descripcion = data.descripcion
            registro.descrTicket = data.descrTicket
            registro.cantidad = "1"
            registro.piezas = "0"
            registro.precio = dimePrecioArt(data.articuloId)
            registro.importe = calculaImporte(registro)
            registro.usuario = 0

            LineasDao.anyadirLinea(connGes, registro)
            fLinea++
        }

        //fRecycler.adapter?.notifyDataSetChanged()
    }


    private fun calculaImporte(registro: DatosLinea): String {
        val dCantidad = registro.cantidad.toDouble()
        val dPrecio = registro.precio.replace(",", ".").toDouble()

        val dImporte = redondear((dCantidad * dPrecio), 2)

        return dImporte.toString()
    }

    private fun dimePrecioArt(articuloId: Int): String {
        var quePrecio = TarifasDao.getPrecio(connInf, fTarifa, articuloId)
        if (quePrecio == "") quePrecio = "0.0"

        return quePrecio
    }

    fun verGrupos(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador
        prepararGruposVta()
    }

    fun enEspera(view: View?) {
        view?.getTag(0)          // Para que no dé warning el compilador

        doAsync {
            if (LineasDao.sinLineas(connInf, fSala, fMesa))
                CuentasDao.borrarCuenta(connGes, fSala, fMesa)
        }
        finish()
    }

    fun verCuenta(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        if (fVistaActual == VIENDO_CUENTA)
            if (fVistaAnterior == VIENDO_GRUPOS)
                prepararGruposVta()
            else
                prepararArticulosGrupo(fGrupoActual)
        else
            prepararCuenta()
    }

    // Manejo los eventos del teclado en la actividad.
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            when (fVistaActual) {
                VIENDO_GRUPOS -> enEspera(null)
                VIENDO_CUENTA -> {
                    if (fVistaAnterior == VIENDO_GRUPOS)
                        prepararGruposVta()
                    else
                        prepararArticulosGrupo(fGrupoActual)
                }
                else -> prepararGruposVta()
            }

            return true
        }
        // Para las demás cosas, se reenvía el evento al listener habitual.
        return super.onKeyDown(keyCode, event)
    }


}