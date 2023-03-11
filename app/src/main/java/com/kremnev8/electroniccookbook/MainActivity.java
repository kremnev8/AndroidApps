package com.kremnev8.electroniccookbook;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.kremnev8.electroniccookbook.contract.TakePictureWithUriReturnContract;
import com.kremnev8.electroniccookbook.databinding.ActivityMainBinding;
import com.kremnev8.electroniccookbook.fragments.IngredientListFragment;
import com.kremnev8.electroniccookbook.fragments.RecipesListFragment;
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
    public static MainActivity Instance;

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private List<Fragment> fragments = new ArrayList<>();
    private IPhotoRequestCallback lastRequester;

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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topBar.menuButton.setOnClickListener(v -> {
            toggleLeftDrawer();
        });

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
        fragments.add(fragmentManager.findFragmentById(R.id.fragmentContainerView));
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
        Optional<Fragment> fragment = Iterables.tryFind(fragments, frag -> frag.getClass().isInstance(clazz));
        if (!fragment.isPresent()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, clazz, args)
                    .addToBackStack("open fragment")
                    .commit();

            Fragment newFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
            Log.i("INFO", "Created fragment: " + newFragment.getClass().getName());
            fragments.add(newFragment);
            return;
        }

        fragment.get().setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment.get())
                .addToBackStack("open fragment")
                .commit();

    }

    public void requestPhoto(IPhotoRequestCallback callback) {
        lastRequester = callback;
        if (ContextCompat.checkSelfPermission(MainActivity.Instance, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mGetContent.launch("image/*");
        } else {
            requestPhotoPermission();
        }
    }

    public void takePicture(IPhotoRequestCallback callback) {
        lastRequester = callback;
        if (ContextCompat.checkSelfPermission(MainActivity.Instance, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            tryTakePicture();
        }else {
            requestCameraPermission();
        }

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
}