package com.example.ebookreader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ebookreader.databinding.EbooksViewFragmentBinding;

import java.util.ArrayList;

public class EbooksViewFragment extends Fragment {
    ArrayList<Ebook> ebooks;
    GridView ebookDisplay;
    EbookAdapter ebookAdapter;

    private EbooksViewFragmentBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = EbooksViewFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button openReadButton = view.findViewById(R.id.button_first);
        ebooks = new ArrayList<Ebook>();
        ebookDisplay = view.findViewById(R.id.ebook_grid);
        ebookAdapter = new EbookAdapter(getContext(), ebooks);
        ebookDisplay.setAdapter(ebookAdapter);

        Ebook temp = new Ebook("test");
        ebookAdapter.add(temp);
        Ebook temp2 = new Ebook("test2");
        ebookAdapter.add(temp2);
        ebookAdapter.notifyDataSetChanged();

        Log.d("BooksViewFragment", "Button: " + openReadButton);
        openReadButton.setText("test");
        openReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EbooksViewFragment.this)
                        .navigate(R.id.action_BooksViewFragment_to_ReadBookFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}