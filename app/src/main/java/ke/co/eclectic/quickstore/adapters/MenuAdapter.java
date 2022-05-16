package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.AppMenu;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    List<AppMenu> singleList;
    Context context;
    MenuComm menuComm;
    TypedArray imgs;


    /**
     * Instantiates a new Menu adapter.
     *
     * @param context    the context
     * @param singleList the single list
     */
    public MenuAdapter(Context context, List<AppMenu> singleList) {
        this.singleList = singleList;
        this.context = context;
        this.menuComm= (MenuComm) context;
        imgs = context.getResources().obtainTypedArray(R.array.menu_imgs);

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        final int pos = position;
        holder.txtName.setText(singleList.get(pos).getTitle());
        holder.txtName.setTextColor(Color.parseColor("#ffffff"));
        holder.txtName.setTypeface(GlobalVariable.getMontserratMedium(context));
       // holder.imgPrev.setImageResource(imgs.getResourceId(pos, -1));
        holder.imgPrev.setImageResource(singleList.get(pos).getImgResource());
        holder.contentLay.setOnClickListener(view -> menuComm.strMessage(holder.getAdapterPosition(),singleList.get(pos).getTitle(),""));
        
    }

    /**
     * The  Menu adapter interface.
     */
    public interface MenuComm{
        /**
         * Str message.
         *
         * @param position the position of clicked item
         * @param val      the value of clicked item
         * @param action   the action to be performed
         */
        void strMessage(int position, String val, String action);
    }

    @Override
    public int getItemCount() {
        return singleList.size();
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        LinearLayout contentLay;
        ImageView imgPrev;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgPrev = itemView.findViewById(R.id.imgPrev);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

