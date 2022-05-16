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
import java.util.Random;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.Supplier;
import timber.log.Timber;

/**
 *  Created by Manduku O. David on 21/12/2018.
 */
public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    List<Supplier> supplierList;
    Context context;
    SupplierComm supplierComm;

    /**
     * Instantiates a new Supplier adapter.
     *
     * @param context      the context
     * @param supplierList the supplier list
     */
    public SupplierAdapter(Context context, List<Supplier> supplierList) {
        this.supplierList = supplierList;
        this.context = context;
        this.supplierComm= (SupplierComm) context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_supplier, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierAdapter.ViewHolder holder, int position) {
        final Supplier supplier = supplierList.get(position);
        holder.txtCompanyName.setText(supplier.getCompanyname());
        holder.txtContactName.setText(supplier.getContactname());
        Random r = new Random();
        Integer distance = r.nextInt(900 - 10) + 10;
        holder.txtDistance.setText(distance.toString().concat("m away"));
        holder.contentLay.setOnClickListener(view -> {
            supplierComm.supplierMessage(position,supplierList.get(position),"view");
        });
    }

    /**
     * Refresh supplier list.
     *
     * @param sList the supplier list
     */
    public void refresh(List<Supplier> sList) {
        supplierList = sList;
        notifyDataSetChanged();
    }

    public interface SupplierComm{
        /**
         * send supplier data to interface implementors
         *
         * @param position position of the item in list
         * @param supplier single item
         * @param action action to be performed
         */
        void supplierMessage(int position, Supplier supplier, String action);
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCompanyName;
        TextView txtContactName;
        TextView txtDistance;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtCompanyName = itemView.findViewById(R.id.txtCompanyName);
            txtContactName = itemView.findViewById(R.id.txtContactName);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }
    }
}
