package es.albaibs.ibscomanda

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import es.albaibs.ibscomanda.Varios.ponerCeros
import net.sourceforge.jtds.jdbc.Driver
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class DBConnection {

    companion object {

        var connectionGES: Connection? = null
        var connectionINF: Connection? = null
        var connectionSYS: Connection? = null

        fun conectar(context: Context, bdInf: Boolean): Connection {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val prefURL = "jdbc:jtds:sqlserver:"
            val USER = "sa"
            val PASS = "albamaster"
            lateinit var connSys: Connection
            lateinit var conn: Connection
            try {
                // En primer lugar nos tendremos que conectar a la base de datos del sistema ALBASYSxx para, a través
                // de la tabla Aplicaciones, averiguar el nombre de la base de datos a la que tenemos que conectarnos.
                // Si lo que queremos es conectarnos a la base de datos de informes (ALBAINF) no tendremos que hacer nada de esto.
                val quePrefijo = prefs.getString("prefijo", "") ?: ""
                var BD: String
                var queSistema = prefs.getString("sistema", "00") ?: "00"
                queSistema = ponerCeros(queSistema, 2)
                //var queTerminal = ponerCeros(prefs.getString("terminal", ""), 3)

                if (bdInf) {
                    if (quePrefijo != "") BD = quePrefijo + "_ALBAINF" + queSistema
                    else BD = "ALBAINF$queSistema"

                    val URL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + BD + ";"

                    Driver().javaClass
                    conn = DriverManager.getConnection(URL, USER, PASS)
                    connectionINF = conn

                } else {
                    if (quePrefijo != "") BD = quePrefijo + "_ALBASYS" + queSistema
                    else BD = "ALBASYS" + queSistema

                    var URL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + BD + ";"

                    // Nos creamos una conexión (de nombre connSys) que permanecerá abierta hasta que cerremos el programa.
                    // Esto es así porque la gestión comprueba las conexiones a la base de datos AlbaSys.
                    Driver().javaClass
                    connSys = DriverManager.getConnection(URL, USER, PASS)

                    var queBDGestion = ""

                    val commSys = connSys.createStatement()
                    val rs = commSys.executeQuery("SELECT TOP 1 fichero FROM Aplicaciones")
                    while (rs.next()) {
                        queBDGestion = rs.getString("fichero")
                    }

                    // Vemos si el puesto ya está conectado para, si es así, devolver la conexión cerrada y obtener un error en Main
                    //val comm = conn.createStatement()
                    //rs = commSys.executeQuery("sp_who isPuesto" + queTerminal)
                    //if (!rs.isBeforeFirst) {
                    URL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + queBDGestion + ";"
                    //USER = "isPuesto" + queTerminal
                    //PASS = HashUtils.sha1("otseuPsi" + queTerminal)

                    conn = DriverManager.getConnection(URL, USER, PASS)
                    connectionGES = conn

                    // Tenemos que cerrar connSys y volverla a abrir con el puesto configurado
                    connSys.close()
                    if (quePrefijo != "") BD = quePrefijo + "_ALBASYS" + queSistema
                    else BD = "ALBASYS$queSistema"
                    URL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + BD + ";"
                    connSys = DriverManager.getConnection(URL, USER, PASS)
                    connectionSYS = connSys
                    //}
                    //else conn.close()
                }

            } catch (ex: SQLException) {
                conn.close()
                ex.printStackTrace()
            } catch (ex: Exception) {
                conn.close()
                ex.printStackTrace()
            }

            return conn
        }
    }



}