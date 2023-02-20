package com.kremnev8.electroniccookbook.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.R;


public class BarFragment extends Fragment {

    public BarFragment() {
        // Required empty public constructor
    }


    public static BarFragment newInstance() {
        return new BarFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bar, container, false);
    }
}