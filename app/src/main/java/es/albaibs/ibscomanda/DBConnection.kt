package es.albaibs.ibscomanda

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import es.albaibs.ibscomanda.varios.ponerCeros
import net.sourceforge.jtds.jdbc.Driver
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class DBConnection {

    companion object {

        var connectionGES: Connection? = null
        var connectionINF: Connection? = null
        var connectionSYS: Connection? = null
        var connectionDB: Connection? = null

        fun conectar(context: Context, bdInf: Boolean): Connection {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val prefURL = "jdbc:jtds:sqlserver:"
            val usuario = "sa"
            val password = "albamaster"
            lateinit var connSys: Connection
            lateinit var connDB: Connection
            lateinit var conn: Connection
            try {
                // En primer lugar nos tendremos que conectar a la base de datos del sistema ALBASYSxx para, a través
                // de la tabla Aplicaciones, averiguar el nombre de la base de datos a la que tenemos que conectarnos.
                // Si lo que queremos es conectarnos a la base de datos de informes (ALBAINF) no tendremos que hacer nada de esto.
                val quePrefijo = prefs.getString("prefijo", "") ?: ""
                var queBaseDatos: String
                var queSistema = prefs.getString("sistema", "00") ?: "00"
                queSistema = ponerCeros(queSistema, 2)
                //var queTerminal = ponerCeros(prefs.getString("terminal", ""), 3)

                if (bdInf) {
                    queBaseDatos = if (quePrefijo != "") quePrefijo + "_ALBAINF" + queSistema
                    else "ALBAINF$queSistema"

                    val queURL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + queBaseDatos + ";"

                    Driver().javaClass
                    conn = DriverManager.getConnection(queURL, usuario, password)
                    connectionINF = conn

                } else {
                    queBaseDatos = if (quePrefijo != "") quePrefijo + "_ALBASYS" + queSistema
                    else "ALBASYS$queSistema"

                    var queURL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + queBaseDatos + ";"

                    // Nos creamos una conexión (de nombre connSys) que permanecerá abierta hasta que cerremos el programa.
                    // Esto es así porque la gestión comprueba las conexiones a la base de datos AlbaSys.
                    Driver().javaClass
                    connSys = DriverManager.getConnection(queURL, usuario, password)

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
                    queURL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + queBDGestion + ";"
                    conn = DriverManager.getConnection(queURL, usuario, password)
                    connectionGES = conn

                    queBaseDatos = if (quePrefijo!= "") quePrefijo + "_ALBADB$queSistema"
                    else "ALBADB$queSistema"
                    queURL = prefURL + "//" + prefs.getString("ip_servidor", "") +":" + prefs.getString("puerto_servidor", "") + "/" + queBaseDatos + ";"
                    connDB = DriverManager.getConnection(queURL, usuario, password)
                    connectionDB = connDB

                    // Tenemos que cerrar connSys y volverla a abrir con el puesto configurado
                    connSys.close()
                    queBaseDatos = if (quePrefijo != "") quePrefijo + "_ALBASYS" + queSistema
                    else "ALBASYS$queSistema"
                    queURL = prefURL + "//" + prefs.getString("ip_servidor", "") + ":" + prefs.getString("puerto_servidor", "") + "/" + queBaseDatos + ";"
                    connSys = DriverManager.getConnection(queURL, usuario, password)
                    connectionSYS = connSys
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