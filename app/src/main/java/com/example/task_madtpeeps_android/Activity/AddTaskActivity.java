package com.example.task_madtpeeps_android.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_madtpeeps_android.Adapters.ImageAdapter;
import com.example.task_madtpeeps_android.Database.AppDatabase;
import com.example.task_madtpeeps_android.Database.DAO;
import com.example.task_madtpeeps_android.Interfaces.RecyclerListClickListener;
import com.example.task_madtpeeps_android.Model.Category;
import com.example.task_madtpeeps_android.Model.Task;
import com.example.task_madtpeeps_android.R;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {
    EditText et_name, et_desc, et_deadline, et_gallery;
    Task task;
    boolean editMode = false;
    Button btn_add;
    private DAO dao;
    private SimpleDateFormat sdf;
    Long categoryId;
    ArrayList<Bitmap> imgLists;
    private List<String> permissionsToRequest;
    private List<String> permissions = new ArrayList<>();
    private List<String> permissionsRejected = new ArrayList<>();
    private static final int REQUEST_CODE = 1;
    public static final int READ_EXTERNAL_STORAGE_CODE = 2;
    ImageAdapter imgAdapter;
    int PICK_IMAGE_MULTIPLE = 1;
    int img_total = 0;
    ImageButton btnPlay;
    TextView tv_record;
    AudioManager audioManager;
    MediaRecorder mediaRecorder;
    SeekBar scrubber;
    MediaPlayer mediaPlayer;
    String pathSave = "", recordFile = null;
    Boolean isRecording = false, isPlaying = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dao = AppDatabase.getDb(this).getDAO();
        sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        if(getIntent().getSerializableExtra("task")!=null) {
            task = (Task) getIntent().getSerializableExtra("task");
            editMode = true;
        }
        categoryId = getIntent().getLongExtra("categoryId", 0);

        btnPlay = findViewById(R.id.playerBtn);
        tv_record = findViewById(R.id.tv_record);
        scrubber = findViewById(R.id.scrubber);
        et_name = findViewById(R.id.et_taskname);
        et_desc = findViewById(R.id.et_desc);
        et_deadline = findViewById(R.id.et_deadline);
        et_gallery = findViewById(R.id.et_gallery);
        btn_add = findViewById(R.id.btn_add);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissionsToRequest = permissionsToRequest(permissions);
        if (permissionsToRequest.size() > 0)
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CODE);

        initToolbar();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        imgLists = new ArrayList<>();
        imgAdapter = new ImageAdapter(getApplicationContext(), imgLists,  new RecyclerListClickListener() {
            @Override
            public void itemClick(View view, Object item, int position) {
                Bitmap bitmap = (Bitmap) item;
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AddTaskActivity.this);
                builder.setMessage("Are you sure you want to delete this image?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        img_total--;
                        imgLists.remove(bitmap);
                        if(img_total == 0) {
                            et_gallery.setText("");
                        } else {
                            et_gallery.setText(img_total+" photos selected");
                        }
                        imgAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

            @Override
            public void moreItemClick(View view, Object item, int position, MenuItem menuItem) {

            }
        });
        recyclerView.setAdapter(imgAdapter);


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying && mediaPlayer != null) {
                    scrubber.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 300);


        scrubber.setVisibility(View.GONE);
        scrubber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**************Ends scrubber function here**************/



        // set the audio volume using streamVolume
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);


        tv_record.setOnClickListener(v -> {
            if (!isRecording) {
                if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.RECORD_AUDIO)) {
                    recordFile = "/" + UUID.randomUUID().toString() + ".3gp";
                    pathSave = getExternalCacheDir().getAbsolutePath()  + recordFile ;
                    //Reference:https://stackoverflow.com/questions/58311691/the-android-app-crashes-after-6-seconds-when-i-press-record-button
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setOutputFile(pathSave);

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        btnPlay.setEnabled(false);
                        btnPlay.setVisibility(View.GONE);
                        scrubber.setVisibility(View.GONE);
                        tv_record.setText("Stop Recording");
                        tv_record.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24, 0);
                        Toast.makeText(AddTaskActivity.this, "Recording started...", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mediaRecorder.stop();
                btnPlay.setEnabled(true);
                btnPlay.setVisibility(View.VISIBLE);
                scrubber.setVisibility(View.VISIBLE);
                tv_record.setText("Start Recording");
                tv_record.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mic_24, 0, 0, 0);
            }
            isRecording = !isRecording;
        });

        btnPlay.setVisibility(View.GONE);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    tv_record.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                        scrubber.setProgress(0);
                        scrubber.setMax(mediaPlayer.getDuration());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.pause);
                    Toast.makeText(AddTaskActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            tv_record.setEnabled(true);
                            btnPlay.setImageResource(R.drawable.play);
                            scrubber.setProgress(0);
                        }
                    });
                } else {
                    //if playing
                    tv_record.setEnabled(true);
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.play);
                }
                isPlaying = !isPlaying;
            }
        });

        if(editMode) {
            getSupportActionBar().setTitle("Edit Task");
            et_name.setText(""+task.getTaskName());
            et_desc.setText(""+task.getTaskDesc());
            et_deadline.setText(sdf.format(task.getTaskDeadline()));
            btn_add.setText("Update Task");
            if(task.getTaskImages()!=null) {
                img_total = task.getTaskImages().size();
                if (img_total > 0) {
                    et_gallery.setText(img_total + " photos selected");
                    for (int i = 0; i < img_total; i++) {
                        imgLists.add(StringToBitMap(task.getTaskImages().get(i)));
                    }
                    imgAdapter.notifyDataSetChanged();
                }
            }
            if(task.getTaskRecordingPath()!=null) {
                recordFile = task.getTaskRecordingPath();
                if(recordFile != null){
                    pathSave  = getExternalCacheDir().getAbsolutePath() + recordFile;
                    btnPlay.setVisibility(View.VISIBLE);
                    scrubber.setVisibility(View.VISIBLE);
                }
            }
        }


        et_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar cur_calender = Calendar.getInstance();
//                    cur_calender.add(Calendar.DAY_OF_YEAR, 1);
                    DatePickerDialog datePicker = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    et_deadline.setText(sdf.format(calendar.getTime()));
                                }
                            },
                            cur_calender.get(Calendar.YEAR),
                            cur_calender.get(Calendar.MONTH),
                            cur_calender.get(Calendar.DAY_OF_MONTH)
                    );
                    datePicker.setThemeDark(false);
                    datePicker.setAccentColor(getResources().getColor(R.color.purple_500));
//                    datePicker.setMinDate(cur_calender);
                    datePicker.show(getFragmentManager(), "Deadline");
            }
        });

        et_gallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
                } else {
                    pickImageFromGalary();//if permission allowed then pick image
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
    }

    private void pickImageFromGalary() {
        Intent intent = new Intent();
        // setting type to select to be image
        intent.setType("image/*");
        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    InputStream is = null;
                    try {
                        is = getContentResolver().openInputStream(imageurl);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imgLists.add(bitmap);
                    img_total++;
                }
                // setting 1st selected image into image switcher
            } else {
                Uri imageurl = data.getData();
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(imageurl);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imgLists.add(bitmap);
                img_total++;
            }
            et_gallery.setText(img_total+" photos selected");
            imgAdapter.notifyDataSetChanged();
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void validateFields() {
        if (et_name.getText().toString().equals("")) {
            et_name.setError("Please enter task Name");
        } else if (et_desc.getText().toString().equals("")) {
            et_desc.setError("Please enter task description");
        } else if (et_deadline.getText().toString().equals("")) {
            et_deadline.setError("Please select task deadline");
        } else {
            Task task1;
            String message = "";
            if(editMode) {
                task1 = task;
                message = "Task updated!";
            } else {
                task1 = new Task();
                message = "Task added!";
            }
            if(img_total>0) {
                ArrayList<String> imgList = new ArrayList<>();
                for (int i=0;i<imgLists.size();i++) {
                    imgList.add(BitMapToString(imgLists.get(i)));
                }
                task1.setTaskImages(imgList);
            }
            if(recordFile != null)
                task1.setTaskRecordingPath(recordFile);
            task1.setCategoryId(String.valueOf(categoryId));
            task1.setTaskName(et_name.getText().toString());
            task1.setTaskDesc(et_desc.getText().toString());
            try {
                task1.setTaskDeadline(sdf.parse(et_deadline.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            task1.setTaskStatusCode(0);
            task1.setExpanded(false);
            task1.setTaskCreateDate(new Date());
            dao.insertTask(task1);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /************** Start permissions  methods **************************/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> permissionsToRequest(List<String> permissions) {
        ArrayList<String> results = new ArrayList<>();
        for (String perm : permissions) {
            if (!hasPermission(perm))
                results.add(perm);
        }

        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            for (String perm : permissions) {
                if (!hasPermission(perm))
                    permissionsRejected.add(perm);
            }

            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(AddTaskActivity.this)
                            .setMessage("The permission is mandatory")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), REQUEST_CODE);
                                }
                            }).setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGalary();
            }
        }
    }
    /************** Ends permissions  methods **************************/

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        if(temp==null)
        {
            return null;
        }
        else
            return temp;
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte[] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            if(bitmap==null)
            {
                return null;
            }
            else
            {
                return bitmap;
            }

        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}