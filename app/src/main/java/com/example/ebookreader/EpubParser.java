package com.example.ebookreader;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EpubParser {
    private final Context context;

    public EpubParser(Context context) {
        this.context = context;
    }

    public ArrayList<String> readChaptersFromUri(Uri ebookContents) {
        ContentResolver contentResolver = context.getContentResolver();
        ArrayList<String> allChapters = new ArrayList<String>();
        try {
            InputStream inputStream = contentResolver.openInputStream(ebookContents);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ArrayList<String> ebookChapters = extractEbookChapters(zipInputStream);
            allChapters = ebookChapters;
        } catch (Exception e) {
            Log.d("READING FROM URI", "FAILED TO OPEN INPUT STREAM");
        }
        return allChapters;
    }

    public String getEpubTitle(Uri ebookContents){
        ContentResolver contentResolver = context.getContentResolver();
        String epubTitle = "placeholder title";
        try {
            InputStream inputStream = contentResolver.openInputStream(ebookContents);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            epubTitle = extractEpubTitle(zipInputStream);
        } catch (Exception e) {
            Log.d("GETEPUBTITLE", "FAILED TO READ EPUB TITLE");
        }

        return epubTitle;
    }

    private ArrayList<String> extractEbookChapters(ZipInputStream zipInputStream){
        ArrayList<String> chapterContent = new ArrayList<String>();
        try{
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while(zipEntry!= null){
                String zipEntryName = zipEntry.getName();

                if(!zipEntry.isDirectory() && (zipEntryName.endsWith(".html") || zipEntryName.endsWith(".xhtml") || zipEntryName.endsWith(".htm"))){
                    StringBuilder chapter = new StringBuilder();
                    String current_line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    while((current_line = reader.readLine())!=null){
                        Document document = Jsoup.parse(current_line, zipEntry.getName());
                        chapter.append(document.body().html());
                    }
                    Log.d("READ CHAPTER", chapter.toString());
                    chapterContent.add(chapter.toString());
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }

        }catch (Exception e){
            Log.d("EXTRACTING EBOOK TEXT", "ERROR EXTRACTING EBOOK TEXT");
        }

        return chapterContent;
    }

    private String extractEpubTitle(ZipInputStream zipInputStream){
        String title = "placeholder title";
        try{
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while(zipEntry!= null){
                String zipEntryName = zipEntry.getName();

                if(!zipEntry.isDirectory() && zipEntryName.endsWith(".opf")){
                    StringBuilder opfContent = new StringBuilder();
                    String current_line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    while((current_line = reader.readLine())!=null){
                        opfContent.append(current_line).append("\n");
                    }
                    Log.d("READ OPF CONTENT", opfContent.toString());
                    Document document = Jsoup.parse(opfContent.toString());
                    title = document.select("dc\\:title").text();
                    break;
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }

        }catch (Exception e){
            Log.d("EXTRACTING EBOOK TITLE", "ERROR EXTRACTING EBOOK TITLE");
        }

        return title;
    }
}
