package es.albaibs.ibscomanda.Ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.Varios.ListaArticulosGrupo


class ArticulosGrupoRvAdapter(var articulos: MutableList<ListaArticulosGrupo>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<ArticulosGrupoRvAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos[position]
        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            notifyDataSetChanged()
            listener.onClick(it, articulos[position])
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_articulos_list, parent, false))
    }


    override fun getItemCount(): Int {
        return articulos.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaArticulosGrupo)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val descripcion = itemView.findViewById(R.id.tvDescrArt) as TextView
        private val cantidad = itemView.findViewById(R.id.tvCantidad) as TextView


        fun bind(articulo: ListaArticulosGrupo, context: Context) {
            descripcion.text = articulo.descripcion
            if (articulo.cantidad != 0) cantidad.text = articulo.cantidad.toString()
            else cantidad.visibility = View.INVISIBLE
        }
    }

}