package com.example.ebookreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ebookreader.databinding.EbooksViewFragmentBinding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public void updateEbooks(Ebook newBook){
        ebookAdapter.add(newBook);
        ebookAdapter.notifyDataSetChanged();
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
        NavController navController = NavHostFragment.findNavController(EbooksViewFragment.this);

        openReadButton.setText("test button");
        openReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle placeholderBundle = new Bundle();
                placeholderBundle.putSerializable("ebook", new Ebook("placeholder title"));
                navController.navigate(R.id.action_EbooksViewFragment_to_ReadEbookFragment, placeholderBundle);
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(Intent.createChooser(intent, "Open An Ebook"), 0);
            }
        });

        ebookDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Bundle clickedEbookInfo = new Bundle();
                clickedEbookInfo.putSerializable("ebook", ebooks.get(position));
                navController.navigate(R.id.action_EbooksViewFragment_to_ReadEbookFragment, clickedEbookInfo);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==0 && resultCode == Activity.RESULT_OK){
            if(data != null){
                Uri ebookContents = data.getData();
                Ebook newBook = processEbook(ebookContents);
                ebookAdapter.add(newBook);
                ebookAdapter.notifyDataSetChanged();
            }
        }
    }

    private Ebook processEbook(Uri ebookContents){
        try {
            Log.d("URI", ebookContents.toString());
            InputStream inputStream = getActivity().getContentResolver().openInputStream(ebookContents);
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
            String ebookContent = ebookContentLines.toString();

            return new Ebook(ebookTitle, ebookContents);
        }catch (Exception e){
            Log.e("ERROR", "Error reading ebook contents");
            return new Ebook("error placeholder title");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}