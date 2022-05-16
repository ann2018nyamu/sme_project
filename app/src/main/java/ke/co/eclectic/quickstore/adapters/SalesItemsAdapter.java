package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.others.SalesItems;

/**
 *  Created by Manduku O. David on 21/12/2018.
 */
public class SalesItemsAdapter extends RecyclerView.Adapter<SalesItemsAdapter.ViewHolder> {

    List<SalesItems> salesItemsList;
    Context context;

    /**
     * Instantiates a new Sales items adapter.
     *
     * @param context        the context
     * @param salesItemsList the sales items list
     */
    public SalesItemsAdapter(Context context, List<SalesItems> salesItemsList) {
        this.salesItemsList = salesItemsList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesItemsAdapter.ViewHolder holder, int position) {
        SalesItems salesItems = salesItemsList.get(holder.getAdapterPosition());
        holder.txtProductName.setText(salesItems.getProductname());
        holder.txtQty.setText(salesItems.getQuantityStr());
        holder.txtPriceUnit.setText(salesItems.getInventorycost().toString());
    }

    /**
     * Refresh sales item list.
     *
     * @param cList the sales item  list
     */
    public void refresh(List<SalesItems> cList) {
        salesItemsList = cList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return salesItemsList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName;
        TextView txtQty;
        TextView txtPriceUnit;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtPriceUnit =  itemView.findViewById(R.id.txtPriceUnit);
        }
    }
}
