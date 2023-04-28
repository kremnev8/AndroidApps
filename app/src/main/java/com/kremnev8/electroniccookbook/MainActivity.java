package com.kremnev8.electroniccookbook;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.kremnev8.electroniccookbook.components.ingredient.list.fragment.IngredientListFragment;
import com.kremnev8.electroniccookbook.components.recipe.list.fragment.RecipesListFragment;
import com.kremnev8.electroniccookbook.components.recipe.model.ShowRecipeData;
import com.kremnev8.electroniccookbook.components.recipe.view.fragment.RecipeViewFragment;
import com.kremnev8.electroniccookbook.components.timers.TimersService;
import com.kremnev8.electroniccookbook.contract.TakePictureWithUriReturnContract;
import com.kremnev8.electroniccookbook.databinding.ActivityMainBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements IPhotoProvider {

    private static final int FILES_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    public static final String SHOW_RECIPE_EXTRA = "SHOW_RECIPE";
    public static final String NOTIFICATION_ID_EXTRA = "NotificationId";

    public static MainActivity Instance;

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private final List<Fragment> fragments = new ArrayList<>();
    private IPhotoRequestCallback lastRequester;
    private AlertDialog photoDialog;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (lastRequester != null && uri != null) {
                    lastRequester.onPhotoSelected(uri.toString());
                    lastRequester = null;
                }
            });

    ActivityResultLauncher<Uri> mTakePicture = registerForActivityResult(new TakePictureWithUriReturnContract(),
            result -> {
                if (!result.first)
                {
                    new File(result.second.toString()).delete();
                    return;
                }
                if (lastRequester != null) {
                    lastRequester.onPhotoSelected(result.second.toString());
                    lastRequester = null;
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        photoDialog = createPictureDialog();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topBar.menuButton.setOnClickListener(v -> toggleLeftDrawer());

        binding.drawerMenu.ingredients.setOnClickListener(v -> {
            setFragment(IngredientListFragment.class, null);
            closeMenu();
        });

        binding.drawerMenu.recipes.setOnClickListener(v -> {
            setFragment(RecipesListFragment.class, null);
            closeMenu();
        });

        binding.drawerLayout.closeDrawer(binding.drawerMenu.view);

        fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
        fragments.add(currentFragment);
        if (currentFragment instanceof IMenu) {
            setIMenu((IMenu)currentFragment);
        }
    }

    private void toggleLeftDrawer() {
        if (binding.drawerLayout.isDrawerOpen(binding.drawerMenu.view)) {
            closeMenu();
        } else {
            binding.drawerLayout.openDrawer(binding.drawerMenu.view);
        }
    }

    private void closeMenu() {
        binding.drawerLayout.closeDrawer(binding.drawerMenu.view);
    }

    public <T extends Fragment> void setFragment(Class<T> clazz, @Nullable Bundle args) {
        if (Looper.myLooper() != Looper.getMainLooper()){
            mainHandler.post(() -> Instance.setFragment(clazz, args));
            return;
        }

        Optional<Fragment> fragment = Iterables.tryFind(fragments, frag -> frag.getClass().isInstance(clazz));
        if (!fragment.isPresent()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, clazz, args)
                    .addToBackStack("open fragment")
                    .commit();

            fragmentManager.executePendingTransactions();

            Fragment newFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
            fragments.add(newFragment);
            if (newFragment instanceof IMenu) {
                setIMenu((IMenu)newFragment);
            }
            return;
        }

        fragment.get().setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment.get())
                .addToBackStack("open fragment")
                .commit();

        if (fragment.get() instanceof IMenu) {
            setIMenu((IMenu)fragment.get());
        }
    }

    public void setIMenu(IMenu menu){
        binding.topBar.titleText.setText(menu.getMenuName());
        int text = menu.getActionText();
        int icon = menu.getActionImage();
        binding.topBar.actionButton.setVisibility(text != 0 ? View.VISIBLE : View.INVISIBLE);
        binding.topBar.imageMenuButton.setVisibility(icon != 0 ? View.VISIBLE : View.INVISIBLE);
        binding.topBar.actionButton.setOnClickListener(v -> menu.onAction());
        binding.topBar.imageMenuButton.setOnClickListener(v -> menu.onAction());
    }

    //region Photos
    public void requestPhoto(IPhotoRequestCallback callback) {
        lastRequester = callback;
        photoDialog.show();
    }

    private void tryPickPhoto(){
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
        }else {
            requestCameraPermission();
        }
    }

    private AlertDialog createPictureDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.AddPhotoDialogTitle)
                .setItems(R.array.AddPhotoDialogOptions, (dialog, index) -> {
                    if (index == 0)
                        tryTakePhoto();
                    else if (index == 1)
                        tryPickPhoto();
                    dialog.dismiss();
                });
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
        }else if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && lastRequester != null) {
            tryTakePicture();
        }

    }

    private Uri getTmpFileUri() throws IOException {
        File path = new File(getFilesDir(), "camera");
        path.mkdirs();
        var tmpFile = File.createTempFile("photo", ".png", path);
        tmpFile.createNewFile();

        return FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", tmpFile);
    }
    //endregion

    public TimersService timersService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimersService.TimerBinder binderBridge = (TimersService.TimerBinder) service ;
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

    public void wakeScreen(){
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

        if (intent.hasExtra(NOTIFICATION_ID_EXTRA)){
            int notificationId = intent.getIntExtra(NOTIFICATION_ID_EXTRA, 0);
            CookBookApplication.NotificationManager.cancel(notificationId);
        }

        if (intent.hasExtra(SHOW_RECIPE_EXTRA)){
            ShowRecipeData data = intent.getParcelableExtra(SHOW_RECIPE_EXTRA);
            Bundle args = new Bundle();
            args.putInt(RecipeViewFragment.RECIPE_ID, data.recipeId);
            args.putInt(RecipeViewFragment.STEP_ID, data.stepId);
            MainActivity.Instance.setFragment(RecipeViewFragment.class, args);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("INFO", "Main activity is starting");
        Intent intent = new Intent(this , TimersService.class);
        if (!isServiceRunning(TimersService.class))
            startService(intent);
        bindService(intent , serviceConnection, BIND_IMPORTANT);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }
}