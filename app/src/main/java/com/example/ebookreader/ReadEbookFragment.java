package com.example.ebookreader;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.ebookreader.databinding.ReadEbookFragmentBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadEbookFragment extends Fragment {

    private ReadEbookFragmentBinding binding;
    Ebook currentEbook;
    NestedScrollView nestedScrollView;
    int lastScrollPos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ReadEbookFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nestedScrollView = view.findViewById(R.id.read_ebook_scrollview);
        currentEbook = (Ebook) getArguments().getSerializable("ebook");
        Log.d("PASSED EBOOK TITLE", currentEbook.getTitle());
        if(currentEbook.getEbookUri() == null){
            Log.d("PASSED URI", "is null");
        }else{
            Log.d("PASSED URI", currentEbook.getEbookUri().toString());
        }
        lastScrollPos = currentEbook.getLastScrollPos();

        TextView titleString = view.findViewById(R.id.title_string);
        titleString.setText(currentEbook.getTitle());

        String type = requireContext().getContentResolver().getType(currentEbook.getEbookUri());
        String ebookContent = "placeholder";
        TextView ebookContentText = view.findViewById(R.id.content_string);

        //read contents based on filetype
        Log.d("FILE TYPE", type);
        if(type.equals("text/plain")){
            ebookContent = readTextEbook(currentEbook.getEbookUri());
            ebookContentText.setText(ebookContent);
        }else if(type.equals("application/epub+zip")){
            StringBuilder contentText = new StringBuilder();
            ArrayList<String> chapters = readEpubEbookChapters(currentEbook.getEbookUri());
            int numChapters = chapters.size();
            for(int i = 0; i < numChapters; i++){
                Log.d("TEXT", chapters.get(i));
                String chapterString;
                if(i == numChapters-1){
                    chapterString = Html.fromHtml(chapters.get(i), Html.FROM_HTML_MODE_COMPACT).toString();
                }else{
                    chapterString = Html.fromHtml(chapters.get(i), Html.FROM_HTML_MODE_COMPACT).toString() + "\n\n";
                }
                contentText.append(chapterString);

            }
            ebookContent = contentText.toString();

        }else{
            Log.d("OPENING FILE", "FILE WITH NO EXTENSION SELECTED");
        }
        ebookContentText.setText(ebookContent);

        //resume reading at last position
        nestedScrollView.post(() -> nestedScrollView.scrollTo(0, lastScrollPos));
    }

    private ArrayList<String> readEpubEbookChapters(Uri ebookContents){
        EpubParser parser = new EpubParser(requireContext());
        return parser.readChaptersFromUri(ebookContents);
    }

    private String readTextEbook(Uri ebookUri){
        try {
            Log.d("READING FROM URI", ebookUri.toString());
            InputStream inputStream = getActivity().getContentResolver().openInputStream(ebookUri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder ebookContentLines = new StringBuilder();
            int linesRead = 0;
            String currentLine;

            while((currentLine = bufferedReader.readLine()) != null){
                if(linesRead != 0){
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

    private void saveScrollPos(){

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("EbooksInfo", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String ebookInfoString = sharedPreferences.getString(currentEbook.getTitle(), null);
        if (ebookInfoString != null) {
            try {
                lastScrollPos = nestedScrollView.getScrollY();
                JSONObject ebookInfoJson = new JSONObject(ebookInfoString);
                String uriString = ebookInfoJson.getString("URI");
                String jsonString = "{'URI':'" + uriString + "', 'lastScrollPos':'" + lastScrollPos + "'}";
                editor.putString(currentEbook.getTitle(), jsonString);
                editor.apply();
            } catch (Exception e) {
                Log.d("SAVESCROLLPOS", "ERROR SAVING INFO");
            }
        } else {
            Log.d("SAVESCROLLPOS", "ERROR READING EBOOK INFO");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause(){
        super.onPause();
        saveScrollPos();
    }

}