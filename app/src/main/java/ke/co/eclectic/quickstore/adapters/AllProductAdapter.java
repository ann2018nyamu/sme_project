package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.Product;

/**
 * Created by Manduku O. David on 20/12/2018.
 */
public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.ViewHolder> {

    private List<Product> productList;
    /**
     * The Context.
     */
    Context context;
    /**
     * The Product adapter interface.
     */
    ProdComm prodComm;

    /**
     * Instantiates a new Employee adapter.
     *
     * @param context   the activity context
     * @param productList the product list
     */
    public AllProductAdapter(Context context, List<Product> productList) {
        this.productList = productList;
        this.context = context;
        prodComm = (ProdComm) context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if(viewType == 0 ){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_header, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {

         if(productList.get(position).getCategoryname().contentEquals(productList.get(position).getProductname())){
             return 0;
         }else{
             return 1;
         }

    }

    @Override
    public void onBindViewHolder(@NonNull AllProductAdapter.ViewHolder holder, int position) {

        final Product productItem = productList.get(holder.getAdapterPosition());

            holder.txtProductName.setText(productItem.getProductname());
            holder.txtProductUnit.setText(productItem.getUnitname());

        holder.contentLay.setOnClickListener(view -> prodComm.prodMessage(position,productList.get(holder.getAdapterPosition()),"view"));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    /**
     * Refresh.
     *
     * @param sList the s list
     */
    public void refresh(List<Product> sList) {
        productList.clear();
        productList = sList;
        notifyDataSetChanged();
    }

    /**
     * The interface Product comm.
     */
    public interface ProdComm{
        /**
         * Str message.
         * @param position the position
         * @param product  the product
         * @param action   the action
         */
        void prodMessage(int position, Product product, String action);
    } 

    @Override
    public int getItemCount() {
        return productList.size();
    }


    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Txt username.
         */
        TextView txtProductUnit, txtProductName;

        /**
         * The Content lay.
         */
        LinearLayout contentLay;
        /**
         * The Img userimg.
         */
        CircleImageView imgUserimg;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtProductUnit = itemView.findViewById(R.id.txtProductUnit);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            imgUserimg =  itemView.findViewById(R.id.imgUserimg);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

