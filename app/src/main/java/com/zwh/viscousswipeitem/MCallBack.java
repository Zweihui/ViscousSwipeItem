package com.zwh.viscousswipeitem;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by RuiDu on 2015/12/18.
 */
public class MCallBack extends ItemTouchHelper.Callback {


    ViscousAdapter viscousAdapter;
    ArrayList<String> mLists;
    public MCallBack(ViscousAdapter viscousAdapter, ArrayList<String> mLists){
        this.mLists = mLists;
        this.viscousAdapter = viscousAdapter;
    }

    /**
     * when it comes a swiping-to-right action,we should see if it
     * can be deleted by querying adapter
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        Log.d("test", "isItemViewSwipeEnabled: "+ viscousAdapter.canDelete());
        return viscousAdapter.canDelete();
    }

    /**
     * you should be aware of the difference between layoutposition and adapterposition
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        viscousAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        mLists.remove(viewHolder.getLayoutPosition());

    }




    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.DOWN| ItemTouchHelper.UP, ItemTouchHelper.RIGHT);
    }

    /**
     * when the items are swaped,we should switch the item in data set meanwhile
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Collections.swap(mLists,viewHolder.getAdapterPosition(),target.getAdapterPosition());
        viscousAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }


}
