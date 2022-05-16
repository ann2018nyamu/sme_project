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

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class SingleViewAdapter extends RecyclerView.Adapter<SingleViewAdapter.ViewHolder> {

    List<String> singleList;
    Context context;
    StringComm stringComm;

    /**
     * Instantiates a new Single view adapter.
     *
     * @param context    the context
     * @param singleList the string  list
     */
    public SingleViewAdapter(Context context, List<String> singleList) {
        this.singleList = singleList;
        this.context = context;
        this.stringComm= (StringComm) context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_single, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewAdapter.ViewHolder holder, int position) {
        holder.txtName.setText(singleList.get(position));
        holder.txtName.setTypeface(GlobalVariable.getMontserratMedium(context));
        holder.contentLay.setOnClickListener(view -> stringComm.strMessage(position,singleList.get(position),""));
    }

    /**
     * The interface String comm.
     */
    public interface StringComm{
        /**
         * sends data to implementors
         *
         * @param position the position
         * @param val      the val
         * @param action   the action
         */
        void strMessage(int position,String val, String action );
    }

    @Override
    public int getItemCount() {
        return singleList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

