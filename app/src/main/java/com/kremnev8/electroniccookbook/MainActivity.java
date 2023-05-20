package com.kremnev8.electroniccookbook;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kremnev8.electroniccookbook.components.profile.dialog.fragment.LoginFragment;
import com.kremnev8.electroniccookbook.components.profile.edit.fragment.ProfileEditFragment;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.recipe.model.ShowRecipeData;
import com.kremnev8.electroniccookbook.components.recipe.view.fragment.RecipeViewFragment;
import com.kremnev8.electroniccookbook.components.timers.TimersService;
import com.kremnev8.electroniccookbook.contract.TakePictureWithUriReturnContract;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.databinding.ActivityMainBinding;
import com.kremnev8.electroniccookbook.interfaces.IAction;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.ILoginSuccessCallback;
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;
import com.kremnev8.electroniccookbook.interfaces.IMediaRequestCallback;
import com.kremnev8.electroniccookbook.interfaces.IMenu;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

@AndroidEntryPoint
@OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
public class MainActivity
        extends AppCompatActivity
        implements IMediaProvider, IDrawerController, IFragmentController, FullscreenListener, DialogInterface.OnClickListener {

    private static final int FILES_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    public static final String SHOW_RECIPE_EXTRA = "SHOW_RECIPE";
    public static final String NOTIFICATION_ID_EXTRA = "NotificationId";

    public static MainActivity Instance;

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private LoginFragment loginFragment;

    private boolean isFullscreen;

    private IMediaRequestCallback lastRequester;
    private AlertDialog photoDialog;
    private AlertDialog mediaDialog;
    private AlertDialog videoUriDialog;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<IAction> drawerStateListeners = new ArrayList<>();

    @Inject
    DatabaseExecutor executor;
    @Inject
    IProfileProvider profileProvider;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (lastRequester != null && uri != null) {
                    lastRequester.onMediaSelected(uri.toString());
                    lastRequester = null;
                }
            });

    ActivityResultLauncher<Uri> mTakePicture = registerForActivityResult(new TakePictureWithUriReturnContract(),
            result -> {
                if (!result.first) {
                    new File(result.second.toString()).delete();
                    return;
                }
                if (lastRequester != null) {
                    lastRequester.onMediaSelected(result.second.toString());
                    lastRequester = null;
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        photoDialog = createMediaDialog(R.array.add_photo_dialog_options);
        mediaDialog = createMediaDialog(R.array.add_media_dialog_options);
        videoUriDialog = createSelectVideoDialog();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topBar.menuButton.setOnClickListener(v -> toggleDrawer(DrawerKind.NAVIGATION));

        binding.drawerLayout.closeDrawer(binding.drawerNavigationView);

        binding.startUsingButton.setOnClickListener(v -> {
            profileProvider.setIsFirstLoad(false);
            profileProvider.getCurrentProfile()
                    .firstOrError()
                    .subscribeOn(Schedulers.computation())
                    .subscribe(profile -> {
                        mainHandler.post(() -> {
                            binding.firstLoadPage.setVisibility(View.GONE);
                            Bundle args = new Bundle();
                            args.putParcelable(ProfileEditFragment.ProfileData, profile);
                            setFragment(ProfileEditFragment.class, args);
                        });
                    }, throwable -> Log.e("App", "Error while getting profile", throwable));
        });

        fragmentManager = getSupportFragmentManager();
        updateCurrentFragment();
        loginFragment = new LoginFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("INFO", "Main activity is starting");
        Intent intent = new Intent(this, TimersService.class);
        if (!isServiceRunning(TimersService.class))
            startService(intent);
        bindService(intent, serviceConnection, BIND_IMPORTANT);
        fragmentManager.addOnBackStackChangedListener(this::updateCurrentFragment);

        profileProvider.getIsFirstLoad()
                .subscribeOn(Schedulers.computation())
                .subscribe(this::onFirstTimeLoadValue, throwable -> Log.e("App", "Error while getting is first load", throwable));
    }

    private void onFirstTimeLoadValue(Boolean firstTime) {
        mainHandler.post(() -> {
            if (firstTime){
                binding.firstLoadPage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        fragmentManager.removeOnBackStackChangedListener(this::updateCurrentFragment);
    }

    private void updateCurrentFragment() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof IMenu) {
            setIMenu((IMenu) currentFragment);
        }
    }

    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentById(R.id.fragmentContainerView);
    }

    @Override
    public boolean isViewingInFullScreen() {
        return isFullscreen;
    }

    public <T extends Fragment> void setFragment(Class<T> clazz, @Nullable Bundle args) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(() -> Instance.setFragment(clazz, args));
            return;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, clazz, args)
                .addToBackStack("open fragment")
                .commit();

        fragmentManager.executePendingTransactions();
        updateCurrentFragment();
    }

    public void setIMenu(IMenu menu) {
        binding.topBar.titleText.setText(menu.getMenuName());
        int text = menu.getActionText();
        int icon = menu.getActionImage();

        binding.topBar.actionButton.setVisibility(text != 0 ? View.VISIBLE : View.INVISIBLE);
        if (text != 0)
            binding.topBar.actionButton.setText(text);

        binding.topBar.imageMenuButton.setVisibility(icon != 0 ? View.VISIBLE : View.INVISIBLE);
        if (icon != 0)
            binding.topBar.imageMenuButton.setImageResource(icon);

        binding.topBar.actionButton.setOnClickListener(v -> menu.onAction());
        binding.topBar.imageMenuButton.setOnClickListener(v -> menu.onAction());
    }

    public View getDrawerView(DrawerKind kind) {
        switch (kind) {
            case NAVIGATION:
                return binding.drawerNavigationView;
            case FILTERS:
                return binding.drawerFiltersView;
        }
        return null;
    }

    public void toggleDrawer(DrawerKind kind) {
        View drawer = getDrawerView(kind);
        if (drawer == null) return;

        if (binding.drawerLayout.isDrawerOpen(drawer)) {
            binding.drawerLayout.closeDrawer(drawer);
        } else {
            binding.drawerLayout.openDrawer(drawer);
        }
        notifyDrawerStateListeners();
    }

    public void closeDrawer(DrawerKind kind) {
        View drawer = getDrawerView(kind);
        if (drawer == null) return;

        binding.drawerLayout.closeDrawer(drawer);
        notifyDrawerStateListeners();
    }

    private void notifyDrawerStateListeners(){
        for (var stateListener: drawerStateListeners) {
            stateListener.action();
        }
    }

    @Override
    public void addOnDrawerStateChangedListener(IAction action) {
        drawerStateListeners.add(action);
    }

    @Override
    public void removeListener(IAction action) {
        drawerStateListeners.remove(action);
    }

    public void showLoginDialog(ILoginSuccessCallback callback, Profile profile) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle args = new Bundle();
        args.putParcelable(LoginFragment.ProfileData, profile);

        loginFragment.setArguments(args);
        loginFragment.setLoginSuccessCallback(callback);

        loginFragment.show(ft, "dialog");
    }

    @Override
    public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> exitFullscreen) {
        isFullscreen = true;

        // the video will continue playing in fullscreenView
        //lastPlayer.setVisibility(View.GONE);
        binding.fullScreenViewContainer.setVisibility(View.VISIBLE);
        binding.fullScreenViewContainer.addView(fullscreenView);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onExitFullscreen() {
        isFullscreen = false;

        // the video will continue playing in the player
        //youTubePlayerView.visibility = View.VISIBLE
        binding.fullScreenViewContainer.removeAllViews();
        binding.fullScreenViewContainer.setVisibility(View.GONE);

       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    //region Photos
    public void requestPhoto(IMediaRequestCallback callback) {
        lastRequester = callback;
        photoDialog.show();
    }

    public void requestMedia(IMediaRequestCallback callback) {
        lastRequester = callback;
        mediaDialog.show();
    }

    private void tryPickPhoto() {
        if (ContextCompat.checkSelfPermission(MainActivity.Instance, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mGetContent.launch("image/*");
        } else {
            requestPhotoPermission();
        }
    }

    public void tryTakePhoto() {
        if (ContextCompat.checkSelfPermission(MainActivity.Instance, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            tryTakePicture();
        } else {
            requestCameraPermission();
        }
    }

    private AlertDialog createMediaDialog(int items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_media_label)
                .setItems(items, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int index) {
        if (index == 0)
            MainActivity.this.tryTakePhoto();
        else if (index == 1)
            MainActivity.this.tryPickPhoto();
        else if (index == 2)
            videoUriDialog.show();
        dialog.dismiss();
    }

    private AlertDialog createSelectVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_media_uri);

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (lastRequester != null) {
                lastRequester.onMediaSelected(input.getText().toString());
                lastRequester = null;
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        return builder.create();
    }

    private void tryTakePicture() {
        try {
            mTakePicture.launch(getTmpFileUri());
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    public void requestPhotoPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                FILES_REQUEST_CODE);
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA
                },
                CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FILES_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && lastRequester != null) {
            mGetContent.launch("image/*");
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && lastRequester != null) {
            tryTakePicture();
        }

    }

    private Uri getTmpFileUri() throws IOException {
        File path = new File(getFilesDir(), "camera");
        path.mkdirs();
        var tmpFile = File.createTempFile("photo", ".jpg", path);
        tmpFile.createNewFile();

        return FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", tmpFile);
    }
    //endregion

    public TimersService timersService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimersService.TimerBinder binderBridge = (TimersService.TimerBinder) service;
            timersService = binderBridge.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timersService = null;
        }
    };

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void wakeScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true);
        } else {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NOTIFICATION_ID_EXTRA)) {
            int notificationId = intent.getIntExtra(NOTIFICATION_ID_EXTRA, 0);
            CookBookApplication.NotificationManager.cancel(notificationId);
        }

        if (intent.hasExtra(SHOW_RECIPE_EXTRA)) {
            ShowRecipeData data = intent.getParcelableExtra(SHOW_RECIPE_EXTRA);
            Bundle args = new Bundle();
            args.putInt(RecipeViewFragment.RECIPE_ID, data.recipeId);
            args.putInt(RecipeViewFragment.STEP_ID, data.stepId);
            MainActivity.Instance.setFragment(RecipeViewFragment.class, args);
        }
    }
}