package es.albaibs.ibscomanda.ventas

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.dao.FormatosDao
import es.albaibs.ibscomanda.databinding.SeleccFormatoActivityBinding
import es.albaibs.ibscomanda.varios.ListaFormatos
import kotlinx.android.synthetic.main.selecc_formato_activity.*
import java.sql.Connection


class SeleccFormatoActivity: AppCompatActivity() {
    private lateinit var binding: SeleccFormatoActivityBinding
    private lateinit var fRecycler: RecyclerView
    private lateinit var fAdapter: FormatosRvAdapter
    private val connInf: Connection = DBConnection.connectionINF as Connection

    private var fArticulo = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SeleccFormatoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        fArticulo = i.getIntExtra("articuloId", 0)

        inicializarControles()
        prepararRecycler()
    }


    private fun inicializarControles() {

        fRecycler = rvFormatos
    }


    private fun prepararRecycler() {
        fAdapter = FormatosRvAdapter(getFormatos(), this, object: FormatosRvAdapter.OnItemClickListener {
            override fun onClick(view: View, data: ListaFormatos) {
                val returnIntent = Intent()
                returnIntent.putExtra("formatoId", data.formatoId)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        })

        fRecycler.layoutManager = GridLayoutManager(this, 4)
        fRecycler.adapter = fAdapter
    }

    private fun getFormatos(): List<ListaFormatos> {
        return FormatosDao.getFormatosArticulo(connInf, fArticulo)
    }


    fun cancelarFto(view: View) {
        view.getTag(0)          // Para que no dé warning el compilador

        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }


}