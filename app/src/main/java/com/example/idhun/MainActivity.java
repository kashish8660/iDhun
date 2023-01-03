package com.example.idhun;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() { //below 3 functions are given by auto-complete
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) { //if app has permission then body of function will execute.
//                        Toast.makeText(MainActivity.this, "Permission is given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchSong(Environment.getExternalStorageDirectory()); //calling user-defined "fetchSong()" on sd-card
                        String [] items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size() ; i++) {
                            items[i] = mySongs.get(i).getName().replace(".mp3","");
                        }
                        ArrayAdapter<String> ad = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(ad);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,PlaySong.class); //Can use getApplicationContext() too instead of MainActivity.this
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList",mySongs);
                                intent.putExtra("currentSong",currentSong);
                                intent.putExtra("position",position);
                                startActivity(intent);

                            }
                        });
                        Log.d("mytag",String.valueOf(mySongs.size()));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) { //when user denied permission last time, so what to do when user opened app this time
                        permissionToken.continuePermissionRequest(); //it'll ask from permission again
                    }
                })
                .check();

    }

    public ArrayList<File> fetchSong(File file) { //ArrayList<File> signifies that function will return an ArrayList which has File type elements. 'file' is directory which has some sub-directories and ".mp3" files. File is an abstract implementation which can contain location of file.
        File[] songs = file.listFiles(); //songs array has name of all files including sub-directories. This function needs access of the external storage
        ArrayList arrayList = new ArrayList(); //it'll have all our songs to be played in app
        if (songs != null) {     //cuz sometimes listFiles() can return null
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSong(myFile)); //recursively calling fetchSong(), cuz we found a sub-directory. "addAll()" will add all the items returned by fetchSong() function.
                }
                else {
                    if (!myFile.getName().startsWith(".") && myFile.getName().endsWith(".mp3"))
                    arrayList.add(myFile);
                }
            }
        }
        return arrayList;
    }
}