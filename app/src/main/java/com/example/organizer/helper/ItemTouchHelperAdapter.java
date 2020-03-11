package com.example.organizer.helper;

public interface ItemTouchHelperAdapter {
    void onItemDismiss(int position);

    boolean onItemMove(int fromPosition, int toPosition);
}
