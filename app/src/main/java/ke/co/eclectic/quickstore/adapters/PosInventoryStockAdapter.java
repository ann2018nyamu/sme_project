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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.pos.fragment.PosStocksFragment;
import ke.co.eclectic.quickstore.models.InventoryStock;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class PosInventoryStockAdapter extends RecyclerView.Adapter<PosInventoryStockAdapter.ViewHolder> {

    private  String action="";
    private List<InventoryStock> productList;
    Context context;
    private PosProdComm posProdComm;



    /**
     * Instantiates a new Pos inventory stock adapter.
     *
     * @param context          the context
     * @param productList      the inventory list
     * @param productsFragment the posstocks fragment instance
     */
    public PosInventoryStockAdapter(Context context, List<InventoryStock> productList, PosStocksFragment productsFragment) {
        this.productList = productList;
        this.context = context;
        this.action = action;
        this.posProdComm=  productsFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_posinventorystock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PosInventoryStockAdapter.ViewHolder holder, int position) {

        final InventoryStock stockItem = productList.get(position);
        holder.txtStockName.setText(stockItem.getProductname());

        if(stockItem.getProductname().toLowerCase().contentEquals("rewards")){
            holder.txtStockPrice.setText("");
            holder.txtTitleCode.setText("");
            holder.txtTitleCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.reward_black,0,0,0);
        }else {
            String[] names = stockItem.getProductname().replaceAll("\\s{2,}", " ").trim().split(" ");
            Timber.v(stockItem.getProductname().replaceAll("\\s{2,}", " ").trim());
            Character s1 = names[0].charAt(0);
            Character s2 ='Q';
            Timber.v(names.length+"  names");
            if(names.length > 1){
                s2 = names[1].charAt(0);
            }
            holder.txtTitleCode.setText(s1.toString().concat(s2.toString()).toUpperCase());
            holder.txtStockPrice.setText("Ksh".concat(" ").concat(stockItem.getSaleprice().toString()));
        }
        holder.contentLay.setOnClickListener(view -> posProdComm.posProdMessage(stockItem,"add_qty"));
    }

    /**
     * Refresh inventory stock list.
     *
     * @param sList the inventory list
     */
    public void refresh(List<InventoryStock> sList) {
        productList = sList;
        notifyDataSetChanged();
    }

    /**
     * The interface Employee comm.
     */
    public interface PosProdComm{
        /**
         * Str message.
         *
         * @param product      the product item
         * @param action   the action
         */
        void posProdMessage( InventoryStock product, String action);
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStockPrice,
        txtStockName,
        txtTitleCode;
        LinearLayout contentLay;
        CircleImageView imgUserimg;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtStockPrice = itemView.findViewById(R.id.txtStockPrice);
            txtStockName = itemView.findViewById(R.id.txtStockName);
            txtTitleCode = itemView.findViewById(R.id.txtTitleCode);
            imgUserimg =  itemView.findViewById(R.id.imgUserimg);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }

    }

}

