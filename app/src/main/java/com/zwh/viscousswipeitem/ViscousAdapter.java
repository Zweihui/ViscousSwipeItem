package com.zwh.viscousswipeitem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.zwh.viscous.SwipeLayout;
import com.zwh.viscous.ViscousView;
import java.util.ArrayList;

/**
 * core class to combine view and data
 * Created by RuiDu on 2015/12/22.
 */
class ViscousAdapter extends RecyclerView.Adapter<ViscousAdapter.MHolder>{


    private ArrayList<String> mLists;

    /**
     * to show if the item can be deleted,it basically depends on
     * whether nowOpen is null.BUT the change of the value may be delayed
     * to avoid user slide a item to right and delete it without ACTION_UP.
     */
    private boolean canDelete = true;

    /**
     * in this situation,we assume that there's only one item
     * can be at the state OPEN at one time. so when a holder is
     * opened, we get its instance. and before you do other operations,
     * the opened item should be closed.
     */
    private MHolder nowOpen = null;

    public ViscousAdapter(ArrayList<String> mLists){
        this.mLists = mLists;
    }




    @Override
    public MHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        MHolder holder = new MHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final MHolder holder, final int position) {

        /*
        to manage get more buttons issue and delete issue,we suppose there's only
        one item open at time.
         */
        holder.swipeLayout.setOnOnSwipeListener(new SwipeLayout.OnSwipeListener() {


            @Override
            public void onStartOpen() {
                if(nowOpen!=null&&(nowOpen!=holder)){
                    nowOpen.swipeLayout.close();
                    nowOpen = null;
                }
            }

            @Override
            public void onOpen() {
                nowOpen = holder;
                canDelete = false;
            }

            @Override public void onOpening(int dx) {

            }

            @Override
            public void onStartClose() {

            }


            @Override
            public void onClose() {
                if(nowOpen == holder){
                    nowOpen=null;
                }
                if (holder.swipeLayout.mState == SwipeLayout.State.Close){
                    holder.viscousView.setmFraction(0.9f*holder.upperChildView.getLeft()/holder.upperChildView.getChildAt(0).getWidth());
                }

            }
        });


        /*
        we use this listener to close any open item before the next action
        you might notice that the field canDelete IS NOT changed once close the
        item,it's changed only after the user make an ACTION_DOWN event again,which
        assure we won't delete the item carelessly
         */
        holder.upperChildView.setOnTouchListener(new View.OnTouchListener(){


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(nowOpen!=null) {
                    nowOpen.swipeLayout.close();
                    return true;
                }else{
                    if(event.getAction() ==  MotionEvent.ACTION_DOWN){
                        canDelete = true;
                    }

                }
                return false;
            }
        });


    }


    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MHolder extends RecyclerView.ViewHolder{

        SwipeLayout swipeLayout;
        LinearLayout upperChildView;
        ViscousView viscousView;

        public MHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            upperChildView = (LinearLayout) itemView.findViewById(R.id.ll);
            viscousView = (ViscousView) itemView.findViewById(R.id.visview);

        }
    }

    /**
     * Since the view is cached when is not seen,we should restore the
     * state of the view once in cach,that is,we should reset the childlayout
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(MHolder holder) {

        //if it was once deleted,we should reset the position of its child layout
        holder.swipeLayout.initialState();
        super.onViewDetachedFromWindow(holder);
    }


    /**
     * to tell the ItemTouchHelper if the item can be deleted
     * @return
     */
    public boolean canDelete(){
        return canDelete;
    }

}