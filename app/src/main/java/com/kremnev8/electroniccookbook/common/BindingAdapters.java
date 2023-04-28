package com.kremnev8.electroniccookbook.common;

import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.BindableRecyclerViewAdapter;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;

import java.net.URLConnection;
import java.util.List;

public class BindingAdapters {

    private static final RequestOptions requestOptions;

    static {
        requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16));
    }

    @androidx.databinding.BindingAdapter("itemViewModels")
    public static <T, VT extends ItemViewModel> void bindItemViewModels(RecyclerView recyclerView, List<VT> itemViewModels) {
        BindableRecyclerViewAdapter<T, VT> adapter = getOrCreateAdapter(recyclerView);
        adapter.updateItems(itemViewModels);
    }

    @SuppressWarnings("unchecked")
    public static <T, VT extends ItemViewModel> BindableRecyclerViewAdapter<T, VT> getOrCreateAdapter(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof BindableRecyclerViewAdapter) {
            return (BindableRecyclerViewAdapter<T, VT>) recyclerView.getAdapter();
        } else {
            BindableRecyclerViewAdapter<T, VT> bindableRecyclerAdapter = new BindableRecyclerViewAdapter<>();
            recyclerView.setAdapter(bindableRecyclerAdapter);
            return bindableRecyclerAdapter;
        }
    }

    @androidx.databinding.BindingAdapter("switchRoundedImage")
    public static void loadKindSwitchRoundedImage(ImageView view, String imageUrl) {
        if (imageUrl != null && isImageFile(imageUrl)) {
            loadRoundedImage(view, imageUrl);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @androidx.databinding.BindingAdapter("switchRoundedVideo")
    public static void loadKindSwitchRoundedVideo(VideoView view, String videoUri) {
        if (videoUri == null || isImageFile(videoUri)) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVideoURI(Uri.parse(videoUri));
            view.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    @androidx.databinding.BindingAdapter("plainImage")
    public static void loadPlainImage(ImageView view, String imageUrl) {
        Glide
                .with(MainActivity.Instance)
                .load(imageUrl)
                .placeholder(R.drawable.rect)
                .centerCrop()
                .into(view);
    }

    @androidx.databinding.BindingAdapter("roundedImage")
    public static void loadRoundedImage(ImageView view, String imageUrl) {
        Glide
                .with(MainActivity.Instance)
                .load(imageUrl)
                .placeholder(R.drawable.empty_placeholder)
                .apply(requestOptions)
                .into(view);
    }

    @androidx.databinding.BindingAdapter("circleImage")
    public static void loadCircularImage(ImageView view, String imageUrl) {
        Glide
                .with(MainActivity.Instance)
                .load(imageUrl)
                .placeholder(R.drawable.empty_round_placeholder)
                .circleCrop()
                .into(view);

    }

    @androidx.databinding.BindingAdapter("profileImage")
    public static void loadProfileImage(ImageView view, String imageUrl) {
        Glide
                .with(MainActivity.Instance)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(view);

    }

    @androidx.databinding.BindingAdapter("isVisibleOrGone")
    public static void isVisible(View view, boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @androidx.databinding.BindingAdapter("isVisible")
    public static void isNotHidden(View view, boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }

    @androidx.databinding.BindingAdapter("pausedIcon")
    public static void loadPlayIcon(Button button, boolean isPaused) {
        int d = isPaused ? R.drawable.ic_pause : R.drawable.ic_play;

        button.setCompoundDrawablesWithIntrinsicBounds(d, 0, 0, 0);
    }

    @androidx.databinding.BindingAdapter("strikethrough")
    public static void strikethrough(TextView view, boolean show) {
        view.setPaintFlags(
                show
                        ? view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                        : view.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
        );
    }

    @androidx.databinding.BindingAdapter("textId")
    public static void textFromId(TextView view, int resId){
        view.setText(resId);
    }
}
