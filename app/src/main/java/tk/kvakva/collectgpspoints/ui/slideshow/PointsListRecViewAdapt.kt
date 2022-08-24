package tk.kvakva.collectgpspoints.ui.slideshow

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import tk.kvakva.collectgpspoints.GeoPoint
import tk.kvakva.collectgpspoints.R

private const val TAG = "PointsListRecViewAdapt"
class PointsListRecViewAdapt(var dataSet: Array<GeoPoint>) :
    RecyclerView.Adapter<PointsListRecViewAdapt.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout: ConstraintLayout

        init {
            // Define click listener for the ViewHolder's View.
            //textView = view.findViewById(R.id.textView)
            constraintLayout = itemView.findViewById(R.id.constraintLayoutGePo)
        }

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = dataSet.size

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.geo_point_layout, parent, false)

        return ViewHolder(view)


    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getBindingAdapterPosition] which
     * will have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.constraintLayout.findViewById<TextView>(R.id.idTv).text =
            holder.itemView.context.resources.getString(R.string.id0001Format, dataSet[position].id)
        //holder.constraintLayout.findViewById<TextView>(R.id.latitudeTv).text=dataSet[position].lat.toString()
        holder.constraintLayout.findViewById<TextView>(R.id.latitudeTv).text =
            holder.constraintLayout.context.resources.getString(
                R.string.l0001Format,
                dataSet[position].lat
            )
        //holder.constraintLayout.findViewById<TextView>(R.id.longitudeTv).text=dataSet[position].lon.toString()
        holder.constraintLayout.findViewById<TextView>(R.id.longitudeTv).text =
            holder.constraintLayout.context.resources.getString(
                R.string.l0001Format,
                dataSet[position].lon
            )
        holder.constraintLayout.findViewById<TextView>(R.id.dateTimeTv).text = "smrtPhn DateTime:\n" +
            dataSet[position].smartDateTime
        holder.constraintLayout.findViewById<CheckBox>(R.id.SavedToServercheckBox).isChecked =
            dataSet[position].uploaded
        holder.constraintLayout.findViewById<TextView>(R.id.gpsDateTimeTv).text = "gpsDateTime:\n" +
            dataSet[position].gpsDateTime
        holder.constraintLayout.findViewById<TextView>(R.id.accuracyTv).text = "accuracy:\n" +
            dataSet[position].accuracy.toString()

        holder.constraintLayout.findViewById<TextView>(R.id.speedTv).text = "speed:\n" +
            dataSet[position].speed.toString()
        holder.constraintLayout.findViewById<TextView>(R.id.speedAccuracyTv).text = "spd acu:\n" +
            dataSet[position].speedAccuracy.toString()
        holder.constraintLayout.findViewById<TextView>(R.id.providerTv).text =
            dataSet[position].provider.toString()
    }


}