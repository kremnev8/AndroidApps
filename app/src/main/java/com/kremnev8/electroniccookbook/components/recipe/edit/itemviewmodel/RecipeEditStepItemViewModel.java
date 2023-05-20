package com.kremnev8.electroniccookbook.components.recipe.edit.itemviewmodel;

import android.net.Uri;
import android.view.View;

import androidx.databinding.Bindable;

import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ContextMenuKind;
import com.kremnev8.electroniccookbook.common.recycler.ICanBeReordered;
import com.kremnev8.electroniccookbook.common.recycler.IHasContextMenu;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;
import com.kremnev8.electroniccookbook.interfaces.IMediaRequestCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeEditStepItemViewModel
        extends ItemViewModel
        implements IMediaRequestCallback, IHasContextMenu, ICanBeReordered {

    public RecipeStep step;
    private int hours;
    private int minutes;

    private final IMediaProvider photoProvider;

    public RecipeEditStepItemViewModel(RecipeStep item, IMediaProvider photoProvider){
        setItem(item);
        this.photoProvider = photoProvider;
    }

    @Bindable
    public boolean getHasMedia(){
        return !Strings.isNullOrEmpty(step.mediaUri);
    }

    @Bindable
    public String getMediaPreviewURI() {
        if (Strings.isNullOrEmpty(step.mediaUri)) return "";

        String videoId = getVideoId();

        if (!Strings.isNullOrEmpty(videoId)){
            return "https://img.youtube.com/vi/" + videoId + "/mqdefault.jpg";
        }else{
            return step.mediaUri;
        }
    }

    private String getVideoId() {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(step.mediaUri);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }

    public void clearMedia(){
        step.mediaUri = "";
        notifyChange();
    }

    @Bindable
    public String getHours(){
        return Integer.toString(hours);
    }

    public void setHours(String value){
        try {
            hours = Integer.parseInt(value);
            step.timer = hours * 60L + minutes;
            notifyChange();
        }catch (NumberFormatException ignored){
        }
    }

    @Bindable
    public String getMinutes(){
        return Integer.toString(minutes);
    }

    public void setMinutes(String value){
        try {
            minutes = Integer.parseInt(value);
            step.timer = hours * 60L + minutes;
            notifyChange();
        }catch (NumberFormatException ignored){
        }
    }

    @Override
    public void setItem(Object item) {
        step = (RecipeStep) item;
        hours = Math.round(step.timer / 60f);
        minutes = (int)(step.timer % 60);
    }

    public void addMediaButtonClicked(View view){
        photoProvider.requestMedia(this, true);
    }

    @Override
    public void onMediaSelected(String imageUri) {
        step.mediaUri = imageUri;
        notifyChange();
    }

    @Override
    public long getItemId() {
        return step.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_step_edit;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    @Override
    public int getMenuResId() {
        return R.menu.delete_menu;
    }

    @Override
    public ContextMenuKind getMenuKind() {
        return ContextMenuKind.RECIPE_STEP;
    }

    @Override
    public int getDragHandleId() {
        return R.id.dragHandle;
    }
}
