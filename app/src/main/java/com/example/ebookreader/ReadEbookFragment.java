package com.example.ebookreader;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ebookreader.databinding.ReadEbookFragmentBinding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadEbookFragment extends Fragment {

    private ReadEbookFragmentBinding binding;
    Ebook currentEbook;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ReadEbookFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentEbook = (Ebook) getArguments().getSerializable("ebook");
        Log.d("PASSED EBOOK TITLE", currentEbook.getTitle());
        if(currentEbook.getEbookUri() == null){
            Log.d("PASSED URI", "is null");
        }else{
            Log.d("PASSED URI", currentEbook.getEbookUri().toString());
        }

        TextView titleString = view.findViewById(R.id.title_string);
        titleString.setText(currentEbook.getTitle());
        String ebookContent = readEbook(currentEbook.getEbookUri());
        TextView ebookContentText = view.findViewById(R.id.content_string);
        ebookContentText.setText(ebookContent);
    }

    private String readEbook(Uri ebookUri){
        try {
            Log.d("READING FROM URI", ebookUri.toString());
            InputStream inputStream = getActivity().getContentResolver().openInputStream(ebookUri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder ebookContentLines = new StringBuilder();
            String ebookTitle = "";
            int linesRead = 0;
            String currentLine;

            while((currentLine = bufferedReader.readLine()) != null){
                if(linesRead == 0){
                    ebookTitle = currentLine.trim();
                }else{
                    ebookContentLines.append(currentLine).append("\n");
                }
                linesRead++;
            }
            return ebookContentLines.toString();
        }catch (Exception e){
            Log.e("ERROR", "Error reading ebook contents");
            return "placeholder ebook content";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}