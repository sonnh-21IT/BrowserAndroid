package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.listeners.OnItemBookmarkClickListener;
import com.example.myapplication.model.Website;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private List<Website> websiteList;
    private OnItemBookmarkClickListener listener;

    public BookmarkAdapter(List<Website> websiteList, OnItemBookmarkClickListener listener) {
        this.websiteList = websiteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_website, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Website website = websiteList.get(position);
        if (website == null) {
            return;
        }
        holder.txtTitle.setText(website.getTitle());
        holder.txtUrl.setText(website.get_url());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, website.get_url(), website.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (websiteList != null) {
            return websiteList.size();
        }
        return 0;
    }

    private void showPopupMenu(View anchorView, String url, String title) {
        // Create a custom view for the popup menu
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.custom_popup_menu_website, null);

        // Initialize the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setEvent(popupView, popupWindow, url, title);
        // Show the popup window
        popupWindow.showAsDropDown(anchorView);
    }

    private void setEvent(View popupView, PopupWindow popupWindow, String url, String title) {
        popupView.findViewById(R.id.menu_website_book_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Added a page path to favorites", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.menu_website_reopen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOpen(url);
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.menu_website_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShare(url);
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.menu_website_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(url);
                popupWindow.dismiss();
            }
        });
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtUrl;
        private LinearLayout layout;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtUrl = itemView.findViewById(R.id.txt_url);
            layout = itemView.findViewById(R.id.item_website);
        }
    }
}