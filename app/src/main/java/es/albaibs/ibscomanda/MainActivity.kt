package es.albaibs.ibscomanda

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import es.albaibs.ibscomanda.Entity.Cuentas
import es.albaibs.ibscomanda.Varios.Mensaje
import es.albaibs.ibscomanda.Varios.Preferencias
import es.albaibs.ibscomanda.Varios.ponerCeros
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.sql.Connection

class MainActivity : AppCompatActivity() {
    private var conn: Connection? = null
    private var fCuentas: MutableList<Cuentas> = arrayListOf()
    private lateinit var prefs: SharedPreferences
    private var fPrefijo: String = ""
    private var fSistema: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        fPrefijo = prefs.getString("prefijo", "") ?: ""
        if (fPrefijo != "") fPrefijo += "_"
        fSistema = prefs.getString("sistema", "00") ?: "00"
        fSistema = ponerCeros(fSistema, 2)

        conectarABD()
    }


    override fun onDestroy() {
        // Cerramos las conexiones a las bases de datos
        try {
            //if (connInf != null) connInf.close()
            if (conn != null) conn!!.close()
            val connSys: Connection = DBConnection.connectionSYS as Connection
            connSys.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        super.onDestroy()
    }



    fun lanzarConfiguracion(view: View) {
        // LLamamos a invalidate para hacer algo con el view y que no dé warning el analizador
        view.invalidate()
        val i = Intent(this, Preferencias::class.java)
        startActivity(i)
    }


    private fun conectarABD() {

        doAsync {
            try {
                if (conn == null) conn = DBConnection.conectar(this@MainActivity, false)

                if (conn != null) {
                    if (!conn!!.isClosed) {

                        //fCuentas = CuentasDao.getAllCuentas(conn!!, fPrefijo, fSistema)
                        //fModificadores = CuentasDao.getModificadores(conn!!, fCuentas)

                        uiThread {
                            if (fCuentas.size > 0) {
                                //mostrarCuentas()
                            }
                            //else limpiarLayouts()
                        }
                    }

                } else {
                    Mensaje(this@MainActivity, "Error al conectar, revise las conexiones")
                }

            } catch (e: Exception) {
                Mensaje(this@MainActivity, e.message.toString())
            }
        }
    }


}