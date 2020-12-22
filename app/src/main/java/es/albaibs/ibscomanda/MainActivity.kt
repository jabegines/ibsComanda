package es.albaibs.ibscomanda

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.albaibs.ibscomanda.dao.SalasDao
import es.albaibs.ibscomanda.ventas.ComandaActivity
import es.albaibs.ibscomanda.databinding.MainActivityBinding
import es.albaibs.ibscomanda.varios.*
import es.albaibs.ibscomanda.ventas.MesasRvAdapter
import es.albaibs.ibscomanda.ventas.SalasRvAdapter
import kotlinx.android.synthetic.main.main_activity.*
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Esta configuración sirve para poder usar imágenes vectoriales con versiones antiguas de android (4.4, etc.)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

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

        super.onDestroy()
    }


    private fun inicializarControles() {
        val queSala = prefs.getString("ultima_sala", "0") ?: "0"
        fUltimaSala = queSala.toShort()

        fRecycler = binding.rvMain
    }


    private fun prepararSalas() {
        fAdptSalas = SalasRvAdapter(getSalas(), this, object: SalasRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaSalas) {
                prepararMesasSala(data)
            }
        })

        fRecycler.layoutManager = LinearLayoutManager(this)
        fRecycler.adapter = fAdptSalas
        fAdptSalas.notifyDataSetChanged()
    }

    private fun getSalas(): MutableList<ListaSalas> {
        return SalasDao.getAllSalas(connInf!!)
    }


    private fun prepararMesasSala(data: ListaSalas) {
        fAdptMesas = MesasRvAdapter(getMesas(data), this, object: MesasRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaMesas) {
            }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 4)
        fRecycler.adapter = fAdptMesas
        fAdptMesas.notifyDataSetChanged()
    }


    private fun getMesas(data: ListaSalas): MutableList<ListaMesas> {
        return SalasDao.getMesasSala(connInf!!, data.salaId)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_configuracion -> {
                val i = Intent(this, Preferencias::class.java)
                startActivity(i)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    fun lanzarComanda(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador
        var continuar = true
        /*
        if (edtUltimaSala.text.toString() == "") {
            Mensaje(this, getString(R.string.sin_sala))
            continuar = false
        }

        if (edtUltimaMesa.text.toString() == "") {
            Mensaje(this, getString(R.string.sin_mesa))
            continuar = false
        }

        if (continuar) {
            doAsync {
                if (SalasDao.existeSala(connInf!!, edtUltimaSala.text.toString().toInt())) {

                    if (SalasDao.existeMesa(connInf!!, edtUltimaSala.text.toString().toInt(), edtUltimaMesa.text.toString().toInt())) {

                        val i = Intent(this@MainActivity, ComandaActivity::class.java)
                        i.putExtra("sala", edtUltimaSala.text.toString())
                        i.putExtra("mesa", edtUltimaMesa.text.toString())
                        startActivity(i)

                    } else {
                        uiThread {
                            Mensaje(this@MainActivity, getString(R.string.mesa_no_existe))
                        }
                    }

                } else {
                    uiThread {
                        Mensaje(this@MainActivity, getString(R.string.sala_no_existe))
                    }
                }

            }
        }
        */
    }



    private fun conectarABD() {

        doAsync {
            try {
                if (conn == null) conn = DBConnection.conectar(this@MainActivity, false)
                if (connInf == null) connInf = DBConnection.conectar(this@MainActivity, true)

                if (connInf != null) {
                    uiThread {
                        prepararSalas()
                    }

                    //if (!conn!!.isClosed) {

                        //fCuentas = CuentasDao.getAllCuentas(conn!!, fPrefijo, fSistema)
                        //fModificadores = CuentasDao.getModificadores(conn!!, fCuentas)
                    //}

                } else {
                    Mensaje(this@MainActivity, "Error al conectar, revise las conexiones")
                }

            } catch (e: Exception) {
                Mensaje(this@MainActivity, e.message.toString())
            }
        }
    }


}