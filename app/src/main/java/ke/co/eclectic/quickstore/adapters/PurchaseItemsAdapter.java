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
import ke.co.eclectic.quickstore.models.others.PurchaseItems;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class PurchaseItemsAdapter extends RecyclerView.Adapter<PurchaseItemsAdapter.ViewHolder> {

    List<PurchaseItems> purchaseItemsList;
    Context context;

    /**
     * Instantiates a new Purchase items adapter.
     *
     * @param context           the context
     * @param purchaseItemsList the purchase items list
     */
    public PurchaseItemsAdapter(Context context, List<PurchaseItems> purchaseItemsList) {
        this.purchaseItemsList = purchaseItemsList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseItemsAdapter.ViewHolder holder, int position) {
        PurchaseItems purchaseItems = purchaseItemsList.get(holder.getAdapterPosition());
        holder.txtProductName.setText(purchaseItems.getProductname());
        holder.txtQty.setText(purchaseItems.getQuantityStr());
        holder.txtPriceUnit.setText(purchaseItems.getItemcostStr());
    }

    /**
     * Refresh purchase item list.
     *
     * @param cList the purchase item list
     */
    public void refresh(List<PurchaseItems> cList) {
        purchaseItemsList = cList;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return purchaseItemsList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
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
