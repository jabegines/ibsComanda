package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaLineasCuenta
import kotlinx.android.synthetic.main.item_gruposvta_list.view.*
import kotlinx.android.synthetic.main.item_gruposvta_list.view.tvDescrGrupoVta
import kotlinx.android.synthetic.main.item_lineacuenta_list.view.*

class VerCuentaRvAdapter(var lineas: MutableList<ListaLineasCuenta>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<VerCuentaRvAdapter.ViewHolder>() {

    var selectedPos: Int = RecyclerView.NO_POSITION


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lineas[position]

        if (selectedPos == position) {
            holder.itemView.tvCantidad.setTextColor(Color.RED)
            holder.itemView.tvDescripcion.setTextColor(Color.RED)
            holder.itemView.tvImporte.setTextColor(Color.RED)
        } else {
            holder.itemView.tvCantidad.setTextColor(Color.BLACK)
            holder.itemView.tvDescripcion.setTextColor(Color.BLACK)
            holder.itemView.tvImporte.setTextColor(Color.BLACK)
        }

        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
            listener.onClick(it, lineas[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_lineacuenta_list, parent, false))
    }

    override fun getItemCount(): Int {
        return lineas.size
    }

    private fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaLineasCuenta)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val cantidad = itemView.findViewById(R.id.tvCantidad) as TextView
        private val descripcion = itemView.findViewById(R.id.tvDescripcion) as TextView
        private val importe = itemView.findViewById(R.id.tvImporte) as TextView

        fun bind(linea: ListaLineasCuenta, context: Context) {
            val dCantidad = linea.cantidad.toDouble()
            val parteDecimal = dCantidad % 1
            if (parteDecimal > 0.0) cantidad.text = String.format("%.3f", dCantidad)
            else cantidad.text = String.format("%.0f", dCantidad)

            descripcion.text = linea.descripcion

            val dImporte = linea.importe.toDouble()
            importe.text = String.format("%.2f", dImporte)
        }
    }


}