package com.agenthun.readingroutine.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenthun.readingroutine.R;
import com.agenthun.readingroutine.UiUtils;
import com.agenthun.readingroutine.views.TagView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Henry on 2015/5/20.
 */
public class NotesAdapter extends BaseTAdapter {

    private static final String TAG = "NotesAdapter";
    private static final int MIN_ITEM_COUNT = 1;
    private static final int MAX_ITEM_ANIMATION_DELAY = 500;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private int cellHeight;
    private int cellWidth;

    private int lastAnimatedItem = 0;
    private List<String> mDataset;
    private Context mContext;

    public NotesAdapter(Context context, CharSequence title, Drawable icon, List<String> objects) {
        super(context, title, icon);
        this.mContext = context;
/*        this.cellHeight = (int) (UiUtils.getScreenWidthPixels(context) / 2.3f);
        this.cellWidth = UiUtils.getScreenWidthPixels(context);*/
        this.mDataset = objects;
    }

    @Override
    protected RecyclerView.ViewHolder createBodyViewHolder(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notes, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
/*        layoutParams.height = cellHeight;
        layoutParams.width = cellWidth;*/
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        RecyclerView.ViewHolder notesViewHolder = new NotesViewHolder(view);
        return notesViewHolder;
    }

    @Override
    protected void bindBodyViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        animateNotes((NotesViewHolder) viewHolder);
        String item = mDataset.get(position - 1);

        ((NotesViewHolder) viewHolder).swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        ((NotesViewHolder) viewHolder).swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.ic_trash));
            }
        });

        //click listener
        ((NotesViewHolder) viewHolder).deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(((NotesViewHolder) viewHolder).swipeLayout);
                mItemManger.closeAllItems();

                int pos = ((NotesViewHolder) viewHolder).getLayoutPosition();
                mOnItemClickListener.onItemDeleteClick(((NotesViewHolder) viewHolder).deleteButton, pos);
//                Log.d(TAG, "onItemDeleteClick() returned: " + pos);
            }
        });

        if (mOnItemClickListener != null) {
            ((NotesViewHolder) viewHolder).tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = ((NotesViewHolder) viewHolder).getLayoutPosition();
                    mOnItemClickListener.onItemClick(((NotesViewHolder) viewHolder).tag, pos);
//                    Log.d(TAG, "onClick() returned: " + pos);
                }
            });
        }

        // set tag title
        ((NotesViewHolder) viewHolder).noteTitle.setText(item);

        // set tag color
        if (position % 4 == 0)
            ((NotesViewHolder) viewHolder).tag.setTagMaskColor(Color.parseColor("#F0E093"));
        else if (position % 4 == 1)
            ((NotesViewHolder) viewHolder).tag.setTagMaskColor(Color.parseColor("#96BBB3"));
        else if (position % 4 == 2)
            ((NotesViewHolder) viewHolder).tag.setTagMaskColor(Color.parseColor("#DFD576"));
        else if (position % 4 == 3)
            ((NotesViewHolder) viewHolder).tag.setTagMaskColor(Color.parseColor("#A08880"));

/*        ((NotesViewHolder) viewHolder).bookView = new BookView(mContext);
        ((NotesViewHolder) viewHolder).bookView.setImageResource(R.drawable.style_book);
        ((NotesViewHolder) viewHolder).bookView.addShadow();
        ((NotesViewHolder) viewHolder).bookView.addBorder(10, Color.WHITE);
        ((NotesViewHolder) viewHolder).bookView.setCornerRadius(10);*/
/*        ((NotesViewHolder) viewHolder).bookView.setVertices(6);
        ((NotesViewHolder) viewHolder).bookView.setBaseShape(new StarShape(0.8f, false));*/

        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animateNotes(NotesViewHolder holder) {
        if (!isLockedAnimations()) {
            if (lastAnimatedItem == holder.getPosition()) {
                setLockedAnimations(true);
            }
        }

        long animationDelay = getHeaderAnimationStartTime() + MAX_ITEM_ANIMATION_DELAY - System.currentTimeMillis();

        if (getHeaderAnimationStartTime() == 0) {
            animationDelay = holder.getPosition() * 30 + MAX_ITEM_ANIMATION_DELAY;
        } else if (animationDelay < 0) {
            animationDelay = holder.getPosition() * 30;
        } else {
            animationDelay += holder.getPosition() * 30;
        }

        holder.swipeLayout.setScaleY(0);
        holder.swipeLayout.setScaleX(0);
        holder.swipeLayout.animate().scaleY(1).scaleX(1).setDuration(200).setInterpolator(INTERPOLATOR).setStartDelay(animationDelay).start();
    }

    @Override
    public int getItemCount() {
        return MIN_ITEM_COUNT + mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.swipe)
        SwipeLayout swipeLayout;
        @InjectView(R.id.ic_trash)
        ImageView deleteButton;
        @InjectView(R.id.tag)
        TagView tag;
        @InjectView(R.id.note_title)
        TextView noteTitle;
        @InjectView(R.id.note_time)
        TextView noteTime;
        @InjectView(R.id.note_content)
        TextView noteContent;

        public NotesViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    //itemClick interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemDeleteClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
