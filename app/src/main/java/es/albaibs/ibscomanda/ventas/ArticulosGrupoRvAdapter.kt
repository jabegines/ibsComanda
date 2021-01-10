package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaArticulosGrupo
import kotlinx.android.synthetic.main.item_articulos_list.view.*


class ArticulosGrupoRvAdapter(var articulos: MutableList<ListaArticulosGrupo>, var queCantidad: Double, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<ArticulosGrupoRvAdapter.ViewHolder>() {

    var selectedPos: Int = RecyclerView.NO_POSITION


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos[position]

        if (selectedPos == position) {
            holder.itemView.tvCantVend.visibility = View.VISIBLE
            holder.itemView.tvCantVend.text = String.format("%.0f", queCantidad)

        } else
            holder.itemView.tvCantVend.visibility = View.INVISIBLE

        holder.bind(item, context)


        holder.itemView.setOnClickListener {
            selectedPos = position
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

    private fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaArticulosGrupo)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val descripcion = itemView.findViewById(R.id.tvDescrArt) as TextView

        fun bind(articulo: ListaArticulosGrupo, context: Context) {
            descripcion.text = articulo.descripcion
        }
    }

}