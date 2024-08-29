import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.paymentacceptance.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomerDialog<T>(
    context: Context,
    itemList: List<T>,
    private val binder: Binder<T>
) : BottomSheetDialog(context, com.airwallex.android.R.style.AirwallexBottomSheetDialog) {

    init {
        initView(itemList)
        initDialog()
    }

    private fun initView(itemList: List<T>) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_customer_list, null)
        setContentView(view)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = CustomerAdapter(itemList, binder)
        recyclerView.adapter = adapter
    }

    private fun initDialog() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    interface Binder<T> {
        fun bind(holder: CustomerAdapter.CustomerViewHolder, item: T)
    }

    class CustomerAdapter<T>(
        private val itemList: List<T>,
        private val binder: Binder<T>
    ) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_customer_list_item, parent, false)
            return CustomerViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
            val currentItem = itemList[position]
            binder.bind(holder, currentItem)
        }

        override fun getItemCount(): Int = itemList.size

        class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
            val itemText: TextView = itemView.findViewById(R.id.itemText)
            val btnPay: Button = itemView.findViewById(R.id.btnPay)
        }
    }
}