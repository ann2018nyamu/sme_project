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
import ke.co.eclectic.quickstore.models.Category;
import timber.log.Timber;

/**
 * The type Category adapter.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<Category> categoryList;
    Context context;
    CategoryComm categoryComm;

    /**
     * Instantiates a new Category adapter.
     *
     * @param context      the context
     * @param categoryList the category list
     */
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
        this.categoryComm= (CategoryComm) context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.txtCatName.setText(categoryList.get(holder.getAdapterPosition()).getCategoryname());
        holder.txtProductNo.setText(categoryList.get(holder.getAdapterPosition()).getProductcount().toString());
        holder.contentLay.setOnClickListener(view -> categoryComm.catMessage(position,categoryList.get(holder.getAdapterPosition()),""));
    }

    /**
     * Refresh category list
     *
     * @param cList the category list
     */
    public void refresh(List<Category> cList) {
        categoryList = cList;
        notifyDataSetChanged();
    }

    public interface CategoryComm{

        void catMessage(int position, Category category, String action);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    /**
     * creating  View holder  class.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCatName;
        TextView txtProductNo;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtCatName = itemView.findViewById(R.id.txtCatName);
            txtProductNo = itemView.findViewById(R.id.txtProductNo);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }
    }
}
