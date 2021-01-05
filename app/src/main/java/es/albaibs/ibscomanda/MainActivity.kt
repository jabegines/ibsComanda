package es.albaibs.ibscomanda

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.dao.SalasDao
import es.albaibs.ibscomanda.ventas.ComandaActivity
import es.albaibs.ibscomanda.databinding.MainActivityBinding
import es.albaibs.ibscomanda.varios.*
import es.albaibs.ibscomanda.ventas.MesasRvAdapter
import es.albaibs.ibscomanda.ventas.SalasRvAdapter
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_activity.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.sql.Connection


class MainActivity : AppCompatActivity() {
    private var conn: Connection? = null
    private var connInf: Connection? = null
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdptSalas: SalasRvAdapter
    private lateinit var fAdptMesas: MesasRvAdapter

    //private var fCuentas: MutableList<Cuentas> = arrayListOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: MainActivityBinding
    private var fPrefijo: String = ""
    private var fSistema: String = ""

    private var fUltimaSala: Short = 0
    private var fNombreUltSala: String = ""


    private val fRequestVender = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Esta configuración sirve para poder usar imágenes vectoriales con versiones antiguas de android (4.4, etc.)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        fPrefijo = prefs.getString("prefijo", "") ?: ""
        if (fPrefijo != "") fPrefijo += "_"
        fSistema = prefs.getString("sistema", "00") ?: "00"
        fSistema = ponerCeros(fSistema, 2)

        inicializarControles()

        conectarABD()
    }


    override fun onDestroy() {
        // Cerramos las conexiones a las bases de datos
        try {
            if (connInf != null) connInf!!.close()
            if (conn != null) conn!!.close()
            val connSys: Connection = DBConnection.connectionSYS as Connection
            connSys.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        prefs.edit().putString("ultima_sala", fUltimaSala.toString()).apply()
        prefs.edit().putString("nombre_ult_sala", fNombreUltSala).apply()

        super.onDestroy()
    }


    private fun inicializarControles() {
        val queSala = prefs.getString("ultima_sala", "0") ?: "0"
        val queNombre = prefs.getString("nombre_ult_sala", "") ?: ""
        fUltimaSala = queSala.toShort()
        fNombreUltSala = queNombre

        fRecycler = binding.rvMain
    }


    private fun prepararSalas() {
        binding.btnConfiguracion.visibility = View.VISIBLE
        binding.btnSalas.visibility = View.GONE

        fAdptSalas = SalasRvAdapter(getSalas(), this, object: SalasRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaSalas) {
                fUltimaSala = data.salaId
                fNombreUltSala= data.nombre
                prepararMesasSala(data.salaId, data.nombre)
            }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 2)
        fRecycler.adapter = fAdptSalas
        fAdptSalas.notifyDataSetChanged()

        binding.tvTitulos.text = getString(R.string.salas)
    }

    private fun getSalas(): MutableList<ListaSalas> {
        return SalasDao.getAllSalas(connInf!!)
    }


    private fun prepararMesasSala(salaId: Short, nombreSala: String) {
        binding.btnConfiguracion.visibility = View.GONE
        binding.btnSalas.visibility = View.VISIBLE

        fAdptMesas = MesasRvAdapter(getMesas(salaId), this, object: MesasRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaMesas) {

                val i = Intent(this@MainActivity, ComandaActivity::class.java)
                i.putExtra("sala", salaId.toString())
                i.putExtra("mesa", data.mesaId.toString())
                startActivityForResult(i, fRequestVender)
            }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 8)
        fRecycler.adapter = fAdptMesas
        fAdptMesas.notifyDataSetChanged()

        val queSala = getString(R.string.sala) + " " + nombreSala
        binding.tvTitulos.text = queSala
    }


    private fun getMesas(salaId: Short): MutableList<ListaMesas> {
        return SalasDao.getMesasSala(connInf!!, salaId)
    }



    fun configuracion(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        val i = Intent(this, Preferencias::class.java)
        startActivity(i)
    }

    fun verSalas(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        prepararSalas()
    }

    private fun conectarABD() {

        doAsync {
            try {
                if (conn == null) conn = DBConnection.conectar(this@MainActivity, false)
                if (connInf == null) connInf = DBConnection.conectar(this@MainActivity, true)

                if (connInf != null) {
                    uiThread {
                        if (fUltimaSala > 0) prepararMesasSala(fUltimaSala, fNombreUltSala)
                        else prepararSalas()
                    }

                } else {
                    Mensaje(this@MainActivity, "Error al conectar, revise las conexiones")
                }

            } catch (e: Exception) {
                Mensaje(this@MainActivity, e.message.toString())
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Buscar ruta.
        if (requestCode == fRequestVender) {
            if (resultCode == Activity.RESULT_OK) {
                //fRecycler.adapter?.notifyDataSetChanged()

            }
        }
    }



}