package com.example.fotoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static ImageView imageView;
    Button b1;
    Button b2;
    Uri photoURI;
    TextView text;
    String currentPhotoPath;
    String rutas[];
    int l = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leerRutas();
        b1 = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        b2 = findViewById(R.id.button2);
        text = findViewById(R.id.textView);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                //apuntaarRuta();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leerRutas();
                System.out.println(l);
                System.out.println(rutas.length);

                try {
                    if (rutas.length != 0) {
                        if (l < rutas.length) {
                            imagenes(l);
                            l++;
                            if (l == rutas.length) {
                                l = 0;

                            }
                        }

                    }
                } catch (Exception ex) {
                    System.out.println("no has guardado ninguna foto");
                }
            }
        });
    }

    public void leerRutas() {
        try {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("rutas.txt")));
            String texto = fin.readLine();
            fin.close();


            rutas = texto.split("@");
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
    }

    public void imagenes(int pos) {
        text.setText(rutas[pos]);
        //File f=new File(rutas[pos]);
        //Bitmap bitmap = BitmapFactory.decodeFile(rutas[pos]);
        imageView.setImageURI(Uri.parse(rutas[pos]));
    }

    public void apuntaarRuta(Uri ruta) {
        try {
            FileWriter fw = new FileWriter(new File(this.getFilesDir(), "rutas.txt"), true);
            //OutputStreamWriter fout=
            //new OutputStreamWriter(
            //openFileOutput("rutas.txt", Context.MODE_PRIVATE));
            fw.write(ruta.toString() + "@");
            //fout.write(currentPhotoPath+"@");
            fw.close();
            //fout.write(ruta.toString() + "@");
            //fout.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir=getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("no estasw creando el fichero de la imagen");
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.fotoapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageURI(photoURI);
            apuntaarRuta(photoURI);
        }
    }
}
