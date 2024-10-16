package com.example.ebookreader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ebookreader.databinding.ReadEbookFragmentBinding;

public class ReadEbookFragment extends Fragment {

    private ReadEbookFragmentBinding binding;
    Ebook currentEbook;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentEbook = (Ebook) getArguments().getSerializable("ebook");
        binding = ReadEbookFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleString = view.findViewById(R.id.title_string);
        titleString.setText(currentEbook.getTitle());
        TextView bookContent = view.findViewById(R.id.content_string);
        bookContent.setText(currentEbook.getContent());
//        binding.buttonSecond.setOnClickListener(v ->
//                NavHostFragment.findNavController(ReadEbookFragment.this)
//                        .navigate(R.id.action_ReadEbookFragment_to_EbooksViewFragment)
//        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}