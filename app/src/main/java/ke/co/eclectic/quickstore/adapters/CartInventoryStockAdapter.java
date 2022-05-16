package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.pos.CartEditActivity;
import ke.co.eclectic.quickstore.activities.pos.fragment.CartFragment;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.Product;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class CartInventoryStockAdapter extends RecyclerView.Adapter<CartInventoryStockAdapter.ViewHolder> {

    private List<InventoryStock> inventoryStockList;
    Context context;
    CartComm cartComm;

    public CartInventoryStockAdapter() {
    }

    /** Instantiates a new CartInventoryStock adapter.
     * @param context context
     * @param inventoryStockList inventory stock list
     * @param cartFragment cart fragment instance
     */
    public CartInventoryStockAdapter(Context context, List<InventoryStock> inventoryStockList, CartFragment cartFragment) {
        this.inventoryStockList = inventoryStockList;
        this.context = context;
        cartComm = (CartComm) cartFragment;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cartinventory_stock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartInventoryStockAdapter.ViewHolder holder, int position) {

            final InventoryStock inventoryStockItem = inventoryStockList.get(position);
            Timber.v(inventoryStockItem.getJson());
            holder.txtInventoryStockName.setText(inventoryStockItem.getProductname());
            holder.txtInventoryStockQty.setText("X".concat(inventoryStockItem.getChoosenquantity().toString()));
            holder.txtInventoryStockPrice.setText(inventoryStockItem.getChoosenPriceStr());
            holder.contentLay.setOnClickListener(view -> { cartComm.cartMessage(position,inventoryStockList.get(position),"edit");});
    }
    /**
     * The interface Product comm.
     */
    public interface CartComm{
        /**
         * Str message.
         * @param position the position
         * @param inventoryStock  the inventory stock
         * @param action   the action
         */
        void cartMessage(int position, InventoryStock inventoryStock, String action);
    }

    /**
     * Refresh inventory stock list
     *
     * @param sList inventory stock list
     */
    public void refresh(List<InventoryStock> sList) {
        inventoryStockList = sList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return inventoryStockList.size();
    }


    /**
     * creating  View holder class.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtInventoryStockPrice,
        txtInventoryStockName,
        txtInventoryStockQty;
        LinearLayout contentLay;
        CircleImageView imgUserimg;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtInventoryStockPrice = itemView.findViewById(R.id.txtCartStockPrice);
            txtInventoryStockName = itemView.findViewById(R.id.txtCartStockName);
            txtInventoryStockQty = itemView.findViewById(R.id.txtCartStockQty);
            imgUserimg =  itemView.findViewById(R.id.imgUserimg);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

