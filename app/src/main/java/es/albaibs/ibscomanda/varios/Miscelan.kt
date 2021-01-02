package es.albaibs.ibscomanda.varios

import android.content.Context
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.NumberFormat
import kotlin.math.pow


fun ponerCeros(cadena: String, longitud: Byte): String {
    var queCadena = cadena.trim { it <= ' ' }
    val fNumCeros = longitud - cadena.length

    if (queCadena != "") {
        if (fNumCeros > 0) {
            queCadena = String.format("%0" + fNumCeros + "d", 0) + queCadena
            return queCadena
        } else
            return queCadena
    } else
        return ""
}


fun Mensaje(fContexto: Context, queMensaje: String): Unit {
    fContexto.alert(queMensaje, "Atención") {
        okButton {}
    }.show()
}



object HashUtils {
    //fun sha512(input: String) = hashString("SHA-512", input)
    //fun sha256(input: String) = hashString("SHA-256", input)
    fun sha1(input: String) = hashString("SHA-1", input)

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}


fun redondear(dNumero: Double, iDecimales: Int): Double {
    val dDecimales = (iDecimales * 1.0) + 1

    val numberFormat = NumberFormat.getInstance()
    numberFormat.maximumFractionDigits = 0
    numberFormat.roundingMode = RoundingMode.DOWN

    val sPrecision = numberFormat.format(10.0.pow(dDecimales)).replace(".", "").replace(",", "")
    val iPrecision = sPrecision.toInt()
    val sRedondeo = numberFormat.format(dNumero * iPrecision).replace(".", "").replace(",", "")

    var iRedondeo = sRedondeo.toInt()
    val iAux = iRedondeo % 10

    iRedondeo =
        if (iAux >= 0) {
            if (iAux >= 5) iRedondeo + 10 - iAux
            else iRedondeo - iAux
        } else {
            if (iAux <= -5) iRedondeo -10 - iAux
            else iRedondeo - iAux
        }

    return iRedondeo.toDouble() / iPrecision
}
