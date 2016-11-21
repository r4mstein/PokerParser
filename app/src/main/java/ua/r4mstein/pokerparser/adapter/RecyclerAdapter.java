package ua.r4mstein.pokerparser.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.r4mstein.pokerparser.MyModel;
import ua.r4mstein.pokerparser.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private List<MyModel> mItemList;
    private OnRecyclerViewClickListener mClickListener = null;

    public RecyclerAdapter(List<MyModel> itemList) {
        mItemList = itemList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.mTitleTextView.setText(mItemList.get(position).getLinkTitle());
        holder.mUserTextView.setText(mItemList.get(position).getUser());
        holder.setId(position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setOnClickListener(OnRecyclerViewClickListener clickListener) {
        mClickListener = clickListener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        private int mId = -1;

        private TextView mTitleTextView;
        private TextView mUserTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.item_title);
            mUserTextView = (TextView) itemView.findViewById(R.id.item_user);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClickRecyclerView(mId);
                    }
                }
            });
        }

        public void setId(int id) {
            mId = id;
        }
    }

    public interface OnRecyclerViewClickListener {
        void onClickRecyclerView(int id);
    }
}
