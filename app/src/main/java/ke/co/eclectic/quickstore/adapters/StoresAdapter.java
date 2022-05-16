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

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.Store;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> {

    List<Store> storeList;
    Context context;
    StoreComm storeComm;

    /**
     * Instantiates a new Stores adapter.
     *
     * @param context   the context
     * @param storeList the store list
     */
    public StoresAdapter(Context context, List<Store> storeList) {
        this.storeList = storeList;
        this.context = context;
        this.storeComm= (StoreComm) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stores, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresAdapter.ViewHolder holder, int position) {
        holder.txtStoreName.setText(storeList.get(holder.getAdapterPosition()).getStorename());
        holder.txtStoreLocation.setText(storeList.get(holder.getAdapterPosition()).getLocation());
        holder.contentLay.setOnClickListener(view -> storeComm.storeMessage(position,storeList.get(holder.getAdapterPosition()),"view"));
    }

    /**
     * Refresh store lst.
     *
     * @param cList the store  list
     */
    public void refresh(List<Store> cList) {
        storeList = cList;
        notifyDataSetChanged();
    }

    public interface StoreComm{
        /**
         * sends item data to implementors
         * @param position position of item
         * @param store single  item
         * @param action action to be performed
         */
        void storeMessage(int position, Store store, String action);
    }


    @Override
    public int getItemCount() {
        return storeList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStoreName;
        TextView txtStoreLocation;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtStoreName = itemView.findViewById(R.id.txtStoreName);
            txtStoreLocation = itemView.findViewById(R.id.txtStoreLocation);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }
    }
}
