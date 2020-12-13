package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaLineasCuenta


class CuentasRvAdapter(var lineas: MutableList<ListaLineasCuenta>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<CuentasRvAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lineas[position]
        holder.bind(item, context)

        holder.itemView.setOnClickListener {
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

    fun setOnItemClickListener(listener: OnItemClickListener) {
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
            cantidad.text = linea.cantidad
            descripcion.text = linea.descripcion
            importe.text = linea.importe
        }
    }




}