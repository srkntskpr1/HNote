package com.hnote.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hnote.R;
import com.hnote.database.NotesDatabase;
import com.hnote.entities.Note;

import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
public class NotOlusturActivity extends AppCompatActivity {
    private EditText inputNoteTitle,inputNoteSubtitle,inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleindicator;
    private ImageView imageNote;
    //Secilen Renk
    private String selectedNoteColor;
    private String selectedImagePath;
    //Istek kodu tanımlama
    private static final int REQUEST_CODE_STORAGE_PERMISSON = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogDeleteNote;
    private Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_olustur);

        ImageView imgback = findViewById(R.id.imgGeri);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        inputNoteTitle = findViewById(R.id.inputNonetitle);
        inputNoteSubtitle = findViewById(R.id.inputNotAltBaslik);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleindicator = findViewById(R.id.viewSubtitleindicator);
        imageNote = findViewById(R.id.imageNote);
        textDateTime.setText(
                new java.text.SimpleDateFormat("dd MMMM yyyy EEEE, a MM:mm ", Locale.getDefault())
                .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imgKayit);
        imageSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                NotEkle();
            }
        });
        // Varsayılan not rengi
        selectedNoteColor = "#333333";
        selectedImagePath = "";
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        initCesitli();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
        if(alreadyAvailableNote.getImagePath()!=null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }

    }

    private void NotEkle(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Not başlığı boş bırakılamaz !",Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty() &&
                inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Not alanı boş bırakılamaz !",Toast.LENGTH_SHORT).show();
            return;
        }else{
            //Toast.makeText(this,"Yeni Not Eklendi !",Toast.LENGTH_SHORT).show();
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);

        if(alreadyAvailableNote != null){
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }
    private void initCesitli(){
        final LinearLayout layoutCesitli = findViewById(R.id.layoutCesitli);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutCesitli);
        layoutCesitli.findViewById(R.id.textCesitli).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutCesitli.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutCesitli.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutCesitli.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutCesitli.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutCesitli.findViewById(R.id.imageColor5);
        layoutCesitli.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectedNoteColor="#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutCesitli.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectedNoteColor="#FDBE3B";
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor1.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutCesitli.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectedNoteColor="#FF4842";
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutCesitli.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectedNoteColor="#3A52FC";
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutCesitli.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectedNoteColor="#000000";
                imageColor5.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor1.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        if(alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch(alreadyAvailableNote.getColor()){
                case "FDBE3B":
                    layoutCesitli.findViewById(R.id.viewColor2).performClick();
                    break;
                case "FF4842":
                    layoutCesitli.findViewById(R.id.viewColor3).performClick();
                    break;
                case "3A52FC":
                    layoutCesitli.findViewById(R.id.viewColor4).performClick();
                    break;
                case "000000":
                    layoutCesitli.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }
        layoutCesitli.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            NotOlusturActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSON
                    );
                }else{
                    selectImage();
                }
            }
        });
        if(alreadyAvailableNote != null){
            layoutCesitli.findViewById(R.id.layoutDelete).setVisibility(View.VISIBLE);
            layoutCesitli.findViewById(R.id.layoutDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }
    private void showDeleteNoteDialog(){
        if(dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(NotOlusturActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,(ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleindicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSON && grantResults.length> 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this,"Erişim Reddedildi !",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUrl = data.getData();
                if(selectedImageUrl != null){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUrl);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUrl);
                    }
                    catch(Exception exception){
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private String getPathFromUri(Uri contentUri){
        String filePath;;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);
        if(cursor == null){
            filePath = contentUri.getPath();

        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;

    }
}