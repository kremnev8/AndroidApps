package com.kremnev8.electroniccookbook.common.recycler;

import android.annotation.SuppressLint;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

class BindableViewHolder<VT extends ItemViewModel>
        extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, View.OnTouchListener {

    private final IStartDragListener<VT> startDragListener;

    public int position;
    public ViewDataBinding binding;
    public ItemViewModel viewModel;

    @Nullable
    private YouTubePlayer youTubePlayer = null;
    private final LifecycleOwner lifecycleOwner;

    public BindableViewHolder(ViewDataBinding binding, IStartDragListener<VT> startDragListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.startDragListener = startDragListener;
        lifecycleOwner = MainActivity.Instance.getCurrentFragment();
    }


    public void bind(VT itemViewModel, int position) {
        binding.setVariable(BR.itemViewModel, itemViewModel);
        this.position = position;
        viewModel = itemViewModel;
        if (viewModel instanceof IHasContextMenu) {
            IHasContextMenu hasContextMenu = (IHasContextMenu) viewModel;
            ItemView itemView = (ItemView) binding.getRoot();

            itemView.setInfo(position, hasContextMenu.getMenuKind());
            itemView.setOnCreateContextMenuListener(this);
        }
        if (viewModel instanceof ICanBeReordered){
            ICanBeReordered canBeReordered = (ICanBeReordered) viewModel;
            View dragHandle = binding.getRoot().findViewById(canBeReordered.getDragHandleId());
            dragHandle.setOnTouchListener(this);
        }

        if (viewModel instanceof IHasYouTubePlayer){
            IHasYouTubePlayer hasYouTubePlayer = (IHasYouTubePlayer) viewModel;
            String videoId = hasYouTubePlayer.getYouTubeVideoID();
            if (Strings.isNullOrEmpty(videoId)) return;

            initYouTubePlayer(hasYouTubePlayer, videoId);
        }
    }

    private void initYouTubePlayer(IHasYouTubePlayer hasYouTubePlayer, String videoId) {
        YouTubePlayerView youTubePlayerView = binding.getRoot().findViewById(hasYouTubePlayer.getYouTubePlayerID());

        if (youTubePlayer != null){
            youTubePlayerView.onStateChanged(lifecycleOwner, Lifecycle.Event.ON_START);
            youTubePlayer.cueVideo(videoId, 0f);
            return;
        }

        lifecycleOwner.getLifecycle().addObserver(youTubePlayerView);

        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .autoplay(0)
                .fullscreen(1) // enable full screen button
                .build();

        youTubePlayerView.addFullscreenListener(MainActivity.Instance);

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer newYouTubePlayer) {
                super.onReady(newYouTubePlayer);
                youTubePlayer = newYouTubePlayer;

                youTubePlayer.cueVideo(videoId, 0f);
            }
        }, iFramePlayerOptions);
    }

    public void onRecycled(){
        itemView.setOnLongClickListener(null);
        if (viewModel instanceof ICanBeReordered){
            ICanBeReordered canBeReordered = (ICanBeReordered) viewModel;
            View dragHandle = binding.getRoot().findViewById(canBeReordered.getDragHandleId());
            dragHandle.setOnTouchListener(null);
        }
        if (viewModel instanceof IHasYouTubePlayer){
            IHasYouTubePlayer hasYouTubePlayer = (IHasYouTubePlayer) viewModel;
            if (youTubePlayer != null) {
                YouTubePlayerView youTubePlayerView = binding.getRoot().findViewById(hasYouTubePlayer.getYouTubePlayerID());
                youTubePlayerView.onStateChanged(lifecycleOwner, Lifecycle.Event.ON_STOP);
            }
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startDragListener.requestDrag(this);
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (viewModel != null && viewModel instanceof IHasContextMenu){
            var menuModel = (IHasContextMenu)viewModel;
            MenuInflater inflater = MainActivity.Instance.getMenuInflater();
            inflater.inflate(menuModel.getMenuResId(), menu);
        }
    }
}
