package com.example.dailystore.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dailystore.R
import com.example.dailystore.data.Address
import com.example.dailystore.databinding.AddressRvItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                btnAddressItem.text = address.addressTitle
                if (isSelected) {
                    btnAddressItem.background = ColorDrawable(
                        itemView.context.resources.getColor(
                            R.color.g_blue
                        )
                    )
                    btnAddressItem.setTextColor(itemView.resources.getColor(R.color.g_white))
                } else {
                    btnAddressItem.background = ColorDrawable(
                        itemView.context.resources.getColor(
                            R.color.g_white
                        )
                    )
                    btnAddressItem.setTextColor(itemView.resources.getColor(R.color.g_black))
                }
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(AddressRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.btnAddressItem.setOnClickListener {
            if (selectedAddress >= 0) {
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }


    /**
     * when more then two address selected is shown then use below code
     */
//    init {
//        differ.addListListener { _,_ ->
//            notifyItemChanged(selectedAddress)
//        }
//    }


    var onClick: ((Address) -> Unit)? = null
}

