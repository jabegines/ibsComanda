package es.albaibs.ibscomanda.ventas

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.dao.*
import es.albaibs.ibscomanda.databinding.ComandaActivityBinding
import es.albaibs.ibscomanda.varios.*
import es.albaibs.ibscomanda.ventas.Impresion.Companion.imprimir
import kotlinx.android.synthetic.main.comanda_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.sql.Connection


class ComandaActivity: AppCompatActivity() {
    private lateinit var binding: ComandaActivityBinding
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdptGrupos: GruposVtaRvAdapter
    private lateinit var fAdptArticulos: ArticulosGrupoRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection
    private val connGes: Connection = DBConnection.connectionGES as Connection
    private lateinit var prefs: SharedPreferences

    private var fVistaActual: Int  = 1
    private var fVistaAnterior: Int = 1
    private var fGrupoActual: Int = 0
    private var fPuesto: Short = 0
    private var fSala: Short = 0
    private var fMesa: Short = 0
    private var fLinea: Int = 0
    private var fTarifa: Int = 1
    private var fUsuario: Short = 0

    private var fPosicionActual = 0
    private var fDataActual = ListaArticulosGrupo()
    private var fFormatoId: Short = 0
    private lateinit var lModificadores: MutableList<ListaModificadores>

    private val fRequestSelecFormato = 1
    private val fRequestSelecModif = 2
    private val fRequestSelecMenu = 3



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComandaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        fPuesto = prefs.getString("terminal", "0")?.toShort() ?: 0
        fUsuario = prefs.getString("usuario", "0")?.toShort() ?: 0
        fTarifa = ConfiguracionesDao.getEntero(connInf, "HOSTELERIA", "TARIFAPRECIOS")
        val i = intent
        fSala = i.getStringExtra("sala").toShort()
        fMesa = i.getStringExtra("mesa").toShort()
        lModificadores = emptyList<ListaModificadores>().toMutableList()

        inicializarControles()
        prepararGruposVta()
    }


    private fun inicializarControles() {
        val numMesa = "Mesa\n$fMesa"
        btnMesa.text = numMesa

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
        tvNombreGrupo.text = ""
        btnGrupos.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        btnVerCuenta.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ResourcesCompat.getDrawable(resources, R.drawable.cuenta, null))
        setRVGrupos()
    }

    private fun setRVGrupos() {
        fAdptGrupos = GruposVtaRvAdapter(getGrupos(), this, object : GruposVtaRvAdapter.OnItemClickListener {
                override fun onClick(view: View, data: ListaGruposVta) {
                    prepararArticulosGrupo(data.grupoId, data.descripcion)
                }
            })

        fRecycler.layoutManager = GridLayoutManager(this, 4)
        fRecycler.adapter = fAdptGrupos
    }


    private fun getGrupos(): MutableList<ListaGruposVta> {
        return GruposVtaDao.getAllGruposVta(connInf)
    }


    private fun prepararArticulosGrupo(queGrupo: Int, queDescr: String) {
        fVistaAnterior = fVistaActual
        fVistaActual = VIENDO_ARTICULOS
        fGrupoActual = queGrupo
        tvNombreGrupo.text = queDescr
        btnGrupos.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ResourcesCompat.getDrawable(resources, R.drawable.grupos, null))
        btnVerCuenta.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ResourcesCompat.getDrawable(resources, R.drawable.cuenta, null))
        setRVArticulos(queGrupo)
    }


    private fun setRVArticulos(queGrupo: Int) {
        fAdptArticulos = ArticulosGrupoRvAdapter(getArticulos(queGrupo), 0.0, this, object : ArticulosGrupoRvAdapter.OnItemClickListener {
                override fun onClick(view: View, data: ListaArticulosGrupo) {
                    fPosicionActual = fAdptArticulos.selectedPos
                    fFormatoId = 0
                    // Vemos si el artículo es de menú
                    if (data.flag2 and FLAGARTICULO_ES_MENU > 0) {
                        fAdptArticulos.queCantidad = 1.0
                        seleccionarMenu(data)

                    // Vemos si el artículo tiene modificadores
                    } else if (articuloTieneModif(data.articuloId)) {
                        fAdptArticulos.queCantidad = 1.0
                        seleccionarModif(data)

                    // Si el artículo tiene formatos los pediremos
                    } else {
                        if (data.flag1 and FLAGARTICULO_USARFORMATOS > 0) {
                            fAdptArticulos.queCantidad = 1.0
                            seleccionarFormato(data)
                        } else vender(data)
                    }
                }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 5)
        fRecycler.adapter = fAdptArticulos
    }


    private fun articuloTieneModif(queArticulo: Int): Boolean {
        val listaModif = ArticulosDao.tieneModificadores(connInf, queArticulo)
        return (listaModif.isNotEmpty())
    }

    private fun getArticulos(queGrupo: Int): MutableList<ListaArticulosGrupo> {
        return ArticulosDao.getArticulosGrupo(connInf, queGrupo, fSala, fMesa)
    }

    private fun seleccionarMenu(data: ListaArticulosGrupo) {
        fDataActual = data
        val i = Intent(this, SeleccMenuActivity::class.java)
        i.putExtra("articuloId", data.articuloId)
        startActivityForResult(i, fRequestSelecMenu)
    }

    private fun seleccionarModif(data: ListaArticulosGrupo) {
        fDataActual = data
        val i = Intent(this, SeleccModifActivity::class.java)
        i.putExtra("articuloId", data.articuloId)
        startActivityForResult(i, fRequestSelecModif)
    }


    private fun seleccionarFormato(data: ListaArticulosGrupo) {
        fDataActual = data
        val i = Intent(this, SeleccFormatoActivity::class.java)
        i.putExtra("articuloId", data.articuloId)
        startActivityForResult(i, fRequestSelecFormato)
    }


    fun verCuenta(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        val i = Intent(this, VerCuentaActivity::class.java)
        i.putExtra("sala", fSala.toString())
        i.putExtra("mesa", fMesa.toString())
        startActivity(i)
    }



    private fun vender(data: ListaArticulosGrupo) {
        doAsync {
            val registro = DatosLinea()
            registro.sala = fSala
            registro.mesa = fMesa
            registro.fraccion = 0
            registro.linea = fLinea
            registro.orden = LineasDao.dimeMaxOrden(connGes, fSala, fMesa)
            registro.articuloId = data.articuloId
            registro.codigoArt = data.codigo
            registro.descripcion = data.descripcion
            registro.descrTicket = data.descrTicket
            registro.cantidad = "1"
            registro.piezas = "0"
            registro.precio = dimePrecioArt(data.articuloId)
            registro.importe = calculaImporte(registro)
            registro.usuario = fUsuario
            registro.formatoId = fFormatoId

            LineasDao.anyadirLinea(connGes, registro)

            if (lModificadores.size > 0) {
                ModificadoresDao.anyadirModificadores(connGes, registro, lModificadores)
            }

            fLinea++
        }

        fAdptArticulos.queCantidad = 1.0
    }

    fun anyadirCant(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        doAsync {
            val queCantidad = LineasDao.dimeCantLinea(connGes, fSala, fMesa, 0, fLinea-1)
            LineasDao.actualizarCantidad(connGes, fSala, fMesa, 0, fLinea-1, 1)

            uiThread {
                fAdptArticulos.queCantidad = queCantidad + 1
                fAdptArticulos.selectedPos = fPosicionActual
                fAdptArticulos.notifyDataSetChanged()
            }
        }

    }

    fun restarCant(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        doAsync {
            val queCantidad = LineasDao.dimeCantLinea(connGes, fSala, fMesa, 0, fLinea-1)
            LineasDao.actualizarCantidad(connGes, fSala, fMesa, 0, fLinea-1, -1)

            uiThread {
                fAdptArticulos.queCantidad = queCantidad - 1
                fAdptArticulos.selectedPos = fPosicionActual
                fAdptArticulos.notifyDataSetChanged()
            }
        }
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
            if (LineasDao.sinLineas(connInf, fSala, fMesa)) {
                CuentasDao.borrarCuenta(connGes, fSala, fMesa)
            } else {
                //imprimirCocina()
            }
        }

        val returnIntent = Intent()
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private fun imprimirCocina() {
        val lDatosCocina = LineasDao.consultaCocina(connInf, fSala, fMesa, 0)

        var fSitActual: Short = -1
        for (datoCocina in lDatosCocina) {
            // Cada vez que cambiemos de situación buscaremos qué impresoras tienen dicha situación para imprirmir en ellas
            if (datoCocina.situacion != fSitActual) {
                fSitActual = datoCocina.situacion
                val lImpresoras = SituacionesPuestoDao.getImpresorasSituacion(connInf, fPuesto, fSitActual)
                for (nombreImpresora in lImpresoras) {
                    // Buscamos en ConfiguracionPuestos qué configuración tiene la impresa (IP y puerto)
                    val datosImpresora = SituacionesPuestoDao.getConfImpresora(connInf, fPuesto, nombreImpresora)
                    imprimir(lDatosCocina, datosImpresora, fSitActual, fSala, fMesa)
                }
            }
        }
    }



    // Manejo los eventos del teclado en la actividad.
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            when (fVistaActual) {
                VIENDO_GRUPOS -> enEspera(null)
                else -> prepararGruposVta()
            }

            return true
        }
        // Para las demás cosas, se reenvía el evento al listener habitual.
        return super.onKeyDown(keyCode, event)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == fRequestSelecFormato) {
            if (resultCode == Activity.RESULT_OK) {
                fFormatoId = data?.getShortExtra("formatoId", 0) ?: 0
                vender(fDataActual)
            }
        }
        else if (requestCode == fRequestSelecModif) {
            if (resultCode == Activity.RESULT_OK) {

                lModificadores = emptyList<ListaModificadores>().toMutableList()
                val numModificadores = data?.getIntExtra("numModificadores", 0) ?: 0
                var x = 1
                while (x <= numModificadores) {
                    val lModif = data?.getStringArrayListExtra("listaModif$x") ?: emptyList<String>()
                    val listaModif = ListaModificadores()
                    listaModif.numeroModif = x
                    listaModif.grupoModif = lModif[0].toShort()
                    listaModif.modificador = lModif[1]
                    listaModif.esArticulo = lModif[2]
                    listaModif.dosis = lModif[3]
                    listaModif.incrPrecio = lModif[4]
                    listaModif.descripcion = lModif[5]
                    lModificadores.add(listaModif)
                    x++
                }

                // Si el artículo tiene formatos los pediremos
                if (fDataActual.flag1 and FLAGARTICULO_USARFORMATOS > 0) {
                    fAdptArticulos.queCantidad = 1.0
                    seleccionarFormato(fDataActual)
                }
                else vender(fDataActual)
            }
        }
    }



}