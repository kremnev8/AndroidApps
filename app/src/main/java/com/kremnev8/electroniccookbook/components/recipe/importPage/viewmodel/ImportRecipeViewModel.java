package com.kremnev8.electroniccookbook.components.recipe.importPage.viewmodel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;
import androidx.databinding.Bindable;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.kremnev8.electroniccookbook.CookBookApplication;
import com.kremnev8.electroniccookbook.common.MainThreadExecutor;
import com.kremnev8.electroniccookbook.common.ObservableViewModel;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditFragment;
import com.kremnev8.electroniccookbook.components.recipe.importPage.ImportRecipeWorker;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ImportRecipeViewModel extends ObservableViewModel implements FutureCallback<WorkInfo> {

    private static final String IMPORT_WORK_TAG = "CookBook:ImportRecipeWork";

    private String url = "";
    private String resultText = "";
    private boolean waitingForResult;

    private OneTimeWorkRequest workRequest;
    private final DatabaseExecutor databaseExecutor;
    private final IFragmentController fragmentController;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    public ImportRecipeViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IFragmentController controller) {
        this.databaseExecutor = databaseExecutor;
        this.fragmentController = controller;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyChange();
    }

    @Bindable
    public boolean isWaitingForResult() {
        return waitingForResult;
    }

    @Bindable
    public String getResultText() {
        return resultText;
    }

    public void importRecipe(){
        waitingForResult = true;

        var downloadConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        var data = new Data.Builder();
        data.putString("url", url);

        workRequest = new OneTimeWorkRequest
                .Builder(ImportRecipeWorker.class)
                .setInputData(data.build())
                .setConstraints(downloadConstraints)
                .addTag(IMPORT_WORK_TAG)
                .build();

         CookBookApplication.manager.beginUniqueWork(IMPORT_WORK_TAG, ExistingWorkPolicy.KEEP, workRequest).enqueue();
        CookBookApplication.manager.getWorkInfoByIdLiveData(workRequest.getId())
                        .observeForever(this::onSuccess);

        notifyChange();
    }

    @Override
    public void onSuccess(WorkInfo result) {
        if (result == null) return;

        if (result.getState() == WorkInfo.State.SUCCEEDED) {
            waitingForResult = false;
            resultText = "Import done, opening!";
            var data = result.getOutputData();
            int id = data.getInt("recipeId", -1);
            if (id >= 0) {
                databaseExecutor.getRecipe(id)
                        .firstOrError()
                        .subscribeOn(Schedulers.computation())
                        .subscribe((recipe, throwable) -> {
                            handler.post(() -> {
                                Bundle args = new Bundle();
                                args.putParcelable(RecipeEditFragment.TARGET_RECIPE, recipe);
                                fragmentController.setFragment(RecipeEditFragment.class, args);
                            });
                        });
            }
        }else if (result.getState() == WorkInfo.State.FAILED){
            waitingForResult = false;
            resultText = "Oh no, fiddlesticks, what now?";
        }
        notifyChange();
    }

    @Override
    public void onFailure(Throwable t) {
        waitingForResult = false;
        resultText = "Oh no, fiddlesticks, what now?";
        notifyChange();
    }
}