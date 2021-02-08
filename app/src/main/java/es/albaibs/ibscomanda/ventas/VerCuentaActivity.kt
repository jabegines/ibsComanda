package es.albaibs.ibscomanda.ventas

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.dao.CuentasDao
import es.albaibs.ibscomanda.dao.LineasDao
import es.albaibs.ibscomanda.databinding.VerCuentaActivityBinding
import es.albaibs.ibscomanda.varios.ListaLineasCuenta
import kotlinx.android.synthetic.main.comanda_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.sql.Connection


class VerCuentaActivity: AppCompatActivity() {
    private lateinit var binding: VerCuentaActivityBinding
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdapter: VerCuentaRvAdapter
    private val connGes: Connection = DBConnection.connectionGES as Connection

    private var fSala: Short = 0
    private var fMesa: Short = 0
    private lateinit var lineaActual: ListaLineasCuenta


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerCuentaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        fSala = i.getStringExtra("sala").toShort()
        fMesa = i.getStringExtra("mesa").toShort()

        inicializarControles()
        prepararRecycler()
    }


    private fun inicializarControles() {
        val numMesa = "Mesa\n$fMesa"
        btnMesa.text = numMesa
        fRecycler = binding.rvCuenta
    }


    private fun prepararRecycler() {
        fAdapter = VerCuentaRvAdapter(getLineasCuenta(), this, object : VerCuentaRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaLineasCuenta) {
                lineaActual = data
            }
        })

        fRecycler.layoutManager = LinearLayoutManager(this)
        fRecycler.adapter = fAdapter

    }


    private fun getLineasCuenta(): MutableList<ListaLineasCuenta> {
        return CuentasDao.getLineasCuenta(connGes, fSala, fMesa)
    }


    fun borrarLinea(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        if (fAdapter.selectedPos > -1) {
            doAsync {
                LineasDao.borrarLinea(connGes, fSala, fMesa, 0, lineaActual.linea)

                uiThread {
                    fAdapter.lineas = getLineasCuenta()
                    fAdapter.notifyDataSetChanged()
                    fAdapter.selectedPos = -1
                }
            }
        }
    }


    fun modifCantidad(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        if (fAdapter.selectedPos > -1) {

            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Introducir cantidad")
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_pedir_cant, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)
            editText.requestFocus()
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK") { _, _ ->
                doAsync {
                    LineasDao.modifCantidadLinea(connGes, fSala, fMesa, 0, lineaActual.linea, editText.text.toString())

                    uiThread {
                        fAdapter.lineas = getLineasCuenta()
                        fAdapter.notifyDataSetChanged()
                        fAdapter.selectedPos = -1
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { _, _ -> }
            builder.show()
        }
    }

    fun subirOrden(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        doAsync{
            val quePosicion = fAdapter.selectedPos
            LineasDao.subirOrden(connGes, fSala, fMesa, 0, lineaActual.linea)

            uiThread {
                fAdapter.lineas = getLineasCuenta()
                fAdapter.notifyDataSetChanged()
                fAdapter.selectedPos = quePosicion-1
            }
        }
    }

    fun bajarOrden(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        doAsync{
            val quePosicion = fAdapter.selectedPos
            LineasDao.bajarOrden(connGes, fSala, fMesa, 0, lineaActual.linea)

            uiThread {
                fAdapter.lineas = getLineasCuenta()
                fAdapter.notifyDataSetChanged()
                fAdapter.selectedPos = quePosicion+1
            }
        }
    }



}