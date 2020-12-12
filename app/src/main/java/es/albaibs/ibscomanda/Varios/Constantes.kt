package es.albaibs.ibscomanda.Varios

import net.sourceforge.jtds.jdbc.DateTime
import java.time.LocalDateTime
import java.util.*


class ListaGruposVta {
    var grupoId: Int = 0
    var descripcion: String = ""
}


class ListaArticulosGrupo {
    var articuloId: Int = 0
    var descripcion: String = ""
}


class DatosCabecera {
    var sala: Short = 0
    var mesa: Short = 0
    var fraccion: Short = 0
    var cliente: Int = 0
    var tarifa: Short = 0
    var comensales: Short = 0
    var horaInicio: String = ""
    var usuario: Short = 0
    var flag: Short = 0
    var tipoIva: Short = 0
    var concepto: String = ""

}


class DatosLinea {
    var sala: Short = 0
    var mesa: Short = 0
    var fraccion: Short = 0
    var linea: Int = 0
    var orden: Int = 0
    var articuloId: Int = 0
    var codigoArt: String = ""
    var descripcion: String = ""
}