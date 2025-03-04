package com.example.ebookreader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

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

    public void addEbook(Ebook newBook){
        ebookAdapter.add(newBook);
        ebookAdapter.notifyDataSetChanged();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button openReadButton = view.findViewById(R.id.button_first);
        logEbookInfo();
        ebooks = getSavedEbooks();
        ebookDisplay = view.findViewById(R.id.ebook_grid);
        ebookAdapter = new EbookAdapter(getContext(), ebooks);
        ebookDisplay.setAdapter(ebookAdapter);

        if(BuildConfig.TEST && ebooks.isEmpty()){
            addEbook(new Ebook("TEST", null));
        }

        ebookAdapter.notifyDataSetChanged();
        NavController navController = NavHostFragment.findNavController(EbooksViewFragment.this);

        openReadButton.setText("clear saved ebooks");
        openReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSavedEbooks(ebookAdapter);
                ebookAdapter.notifyDataSetChanged();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"text/plain","application/epub+zip"});
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
                String type = requireContext().getContentResolver().getType(ebookContents);
                Log.d("FILE TYPE", type);
                if(type.equals("text/plain")){
                    Ebook newEbook = processTextEbook(ebookContents, data);
                    ebookAdapter.add(newEbook);
                    ebookAdapter.notifyDataSetChanged();
                }else if(type.equals("application/epub+zip")){
                    Ebook newEbook = processEpubEbook(ebookContents, data);
                    ebookAdapter.add(newEbook);
                    ebookAdapter.notifyDataSetChanged();
                    Log.d("OPENING FILE", "EPUB FILE SELECTED");
                }else{
                    Log.d("OPENING FILE", "FILE WITH NO EXTENSION SELECTED");
                }
                logEbookInfo();
            }
        }
    }

    private Ebook processTextEbook(Uri ebookContents, Intent intent){

        try {
            Log.d("URI", ebookContents.toString());
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(ebookContents);
            requireActivity().getContentResolver().takePersistableUriPermission(ebookContents, intent.FLAG_GRANT_READ_URI_PERMISSION);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String ebookTitle = "";
            String currentLine = bufferedReader.readLine();
            if(currentLine!= null){
                ebookTitle = currentLine.trim();
            }
            Ebook newEbook = new Ebook(ebookTitle, ebookContents);
            saveEbookInfo(newEbook);
            return new Ebook(ebookTitle, ebookContents);
        }catch (Exception e){
            Log.e("ERROR", "Error reading ebook contents");
            return new Ebook("error placeholder title");
        }
    }

    private Ebook processEpubEbook(Uri ebookContents, Intent intent){
        EpubParser parser = new EpubParser(requireContext());
        String epubTitle = parser.getEpubTitle(ebookContents);
        Ebook newEbook = new Ebook(epubTitle, ebookContents, 0);
        requireActivity().getContentResolver().takePersistableUriPermission(ebookContents, intent.FLAG_GRANT_READ_URI_PERMISSION);
        saveEbookInfo(newEbook);
        return newEbook;
    }

    private void saveEbookInfo(Ebook ebook){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("EbooksInfo", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String uriString;
        if(ebook.getEbookUri() != null){
            uriString = ebook.getEbookUri().toString();
        }else{
           uriString = "invalid_uri";
        }
        String jsonString = "{'URI':'"+uriString+"', 'lastScrollPos':'0', 'currentChapter':'0'}";

        editor.putString(ebook.getTitle(), jsonString);
        editor.apply();
    }

    private void logEbookInfo(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("EbooksInfo", getContext().MODE_PRIVATE);
        Map<String, ?> allEbooks = sharedPreferences.getAll();

        for(Map.Entry<String, ?> ebook : allEbooks.entrySet()){
            String title = ebook.getKey();
            try{
                JSONObject valueJson = new JSONObject(ebook.getValue().toString());
                String uriString = valueJson.getString("URI");
                String lastScrollPos = valueJson.getString("lastScrollPos");
                Log.d("READ FROM EBOOKSINFO", "title: "+title+" | Uri: "+uriString+" | lastScrollPos: "+lastScrollPos);
            }catch (Exception e){
                Log.d("READ FROM EBOOKSINFO","ERROR READING DATA");
            }
        }
    }

    private void clearSavedEbooks(EbookAdapter adapter){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("EbooksInfo", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        adapter.clear();
        editor.apply();
    }

    private ArrayList<Ebook> getSavedEbooks(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("EbooksInfo", getContext().MODE_PRIVATE);
        Map<String, ?> allEbooks = sharedPreferences.getAll();
        ArrayList<Ebook> savedEbooks = new ArrayList<Ebook>();
        JSONObject ebookJson;
        for(Map.Entry<String, ?> ebook : allEbooks.entrySet()){
            String title = ebook.getKey();
            try {
                ebookJson = new JSONObject(ebook.getValue().toString());
                String uriString = ebookJson.getString("URI");
                int currentChapter = Integer.parseInt(ebookJson.getString("currentChapter"));
                int lastScrollPos = Integer.parseInt(ebookJson.getString("lastScrollPos"));
                if(!uriString.equals("invalid_uri")){
                    savedEbooks.add(new Ebook(title, Uri.parse(uriString), lastScrollPos, currentChapter));
                }
            }catch (Exception e){
                savedEbooks.add(new Ebook(title, null));
                Log.d("ERROR", "Error creating JSON object");
            }
        }
        return savedEbooks;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}