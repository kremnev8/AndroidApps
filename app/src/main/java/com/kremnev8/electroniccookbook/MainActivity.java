package com.kremnev8.electroniccookbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.kremnev8.electroniccookbook.database.AppRepository;
import com.kremnev8.electroniccookbook.databinding.ActivityMainBinding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity Instance;

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private List<Fragment> fragments = new ArrayList<>();

    public AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        repository = new AppRepository(getApplication());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        fragments.add(fragmentManager.findFragmentById(R.id.fragmentContainerView));
    }

    public <T extends Fragment> void setFragment(Class<T> clazz, @Nullable Bundle args) {
        Optional<Fragment> fragment = Iterables.tryFind(fragments, frag -> frag.getClass().isInstance(clazz));
        if (!fragment.isPresent()){
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
}