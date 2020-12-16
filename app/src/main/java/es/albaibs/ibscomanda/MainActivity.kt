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
import es.albaibs.ibscomanda.varios.Mensaje
import es.albaibs.ibscomanda.varios.Preferencias
import es.albaibs.ibscomanda.varios.ponerCeros
import es.albaibs.ibscomanda.ventas.ComandaActivity
import es.albaibs.ibscomanda.databinding.MainActivityBinding
import es.albaibs.ibscomanda.varios.ListaSalas
import es.albaibs.ibscomanda.ventas.SalasRvAdapter
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.doAsync
import java.sql.Connection


class MainActivity : AppCompatActivity() {
    private var conn: Connection? = null
    private var connInf: Connection? = null
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdptSalas: SalasRvAdapter

    //private var fCuentas: MutableList<Cuentas> = arrayListOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: MainActivityBinding
    private var fPrefijo: String = ""
    private var fSistema: String = ""


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

        //prefs.edit().putString("ultima_sala", edtUltimaSala.text.toString()).apply()
        //prefs.edit().putString("ultima_mesa", edtUltimaMesa.text.toString()).apply()

        super.onDestroy()
    }


    private fun inicializarControles() {
        //edtUltimaSala.setText(prefs.getString("ultima_sala", "") ?: "")
        //edtUltimaMesa.setText(prefs.getString("ultima_mesa", "") ?: "")

        fRecycler = binding.rvMain
    }


    private fun prepararSalas() {
        fAdptSalas = SalasRvAdapter(getSalas(), this, object: SalasRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaSalas) {
            }
        })

        fRecycler.layoutManager = LinearLayoutManager(this)
        fRecycler.adapter = fAdptSalas
        fAdptSalas.notifyDataSetChanged()
    }

    private fun getSalas(): MutableList<ListaSalas> {
        return SalasDao.getAllSalas(connInf!!)
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
                    prepararSalas()
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