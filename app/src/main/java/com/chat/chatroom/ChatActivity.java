package com.chat.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.chat.chatroom.Const.MyPREFERENCES;

public class ChatActivity extends AppCompatActivity {
    String toUserId, toUserName, fromUserId, fromUserName;

    private RecyclerView recyclerView;
    private List<Chat> chatList;
    private ChatAdapter adapter;
    AppCompatButton btnSend;
    AppCompatEditText etMessage;
    SharedPreferences sharedpreferences;
    String refSender, refReceiver;
    private FrameLayout adContainerView;
    private AdView adView;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private StorageReference mStorageRef;
    AppCompatImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.adContainerView);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));
        adContainerView.addView(adView);
        loadBanner();
        toUserId = getIntent().getStringExtra("touserid");
        toUserName = getIntent().getStringExtra("tousername");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fromUserId = sharedpreferences.getString(Const.USERID, "");
        fromUserName = sharedpreferences.getString(Const.NAME, "");

        refSender = fromUserId + toUserId;
        refReceiver = toUserId + fromUserId;
        chatList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        // recyclerView.addItemDecoration(new EmployeeActivity.GridSpacingItemDecoration(4, dpToPx(3), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatAdapter(getApplicationContext(), chatList, fromUserId);

        recyclerView.setAdapter(adapter);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("chats");

        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int count = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ChatTitle chatTitle = dataSnapshot.getValue(ChatTitle.class);
                    if ((chatTitle.getFromUserId().equals(fromUserId) && chatTitle.getToUserId().equals(toUserId)) ||
                            (chatTitle.getFromUserId().equals(toUserId) && chatTitle.getToUserId().equals(fromUserId))) {
                        count++;
                    } else {


                    }
                }
                if (count == 0) {
                    Log.e("Chat0", "Already Exist0");
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chats");
                    String chatId = mDatabase.push().getKey();
                    //  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
                    //String currentDateandTime = sdf.format(new Date());
                    ChatTitle chat = new ChatTitle(chatId, fromUserId, fromUserName, toUserId, toUserName);
                    mDatabase.child(Objects.requireNonNull(chatId)).setValue(chat);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(refSender);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        Chat model = dataSnapshot.getValue(Chat.class);

                        chatList.add(model);
                        recyclerView.scrollToPosition(chatList.size() - 1);
                        adapter.notifyItemInserted(chatList.size() - 1);


                    } catch (Exception ex) {
                        Log.e("ChatActivity", ex.getMessage());
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(Objects.requireNonNull(etMessage.getText()).toString())) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(refSender);
                    String messageId = mDatabase.push().getKey();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    Chat chat = new Chat(messageId, etMessage.getText().toString(), toUserId, toUserName, fromUserId, fromUserName, currentDateandTime, 0);
                    mDatabase.child(Objects.requireNonNull(messageId)).setValue(chat);


                    DatabaseReference mDatabaseReceiver = FirebaseDatabase.getInstance().getReference(refReceiver);
                    String messageIdReceiver = mDatabaseReceiver.push().getKey();

                    Chat chatMsg = new Chat(messageId, etMessage.getText().toString(), toUserId, toUserName, fromUserId, fromUserName, currentDateandTime, 0);
                    mDatabaseReceiver.child(Objects.requireNonNull(messageIdReceiver)).setValue(chatMsg);


                }

                TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
                NOTIFICATION_TITLE = fromUserName;
                NOTIFICATION_MESSAGE = etMessage.getText().toString();

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }
                //   sendNotification(notification);
                etMessage.setText("");
            }
        });
        ivImage = findViewById(R.id.ivImage);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(ChatActivity.this);
                userChoosenTask = "Choose from Library";
                if (result)
                    galleryIntent();
            }
        });

    }

    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest =
                new AdRequest.Builder() //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        //  edtTitle.setText("");
                        //  edtMessage.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ChatActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());

        ref.putFile(data.getData())

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: uri= " + uri.toString());
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(refSender);
                                String messageId = mDatabase.push().getKey();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
                                String currentDateandTime = sdf.format(new Date());
                                Chat chat = new Chat(messageId, uri.toString(), toUserId, toUserName, fromUserId, fromUserName, currentDateandTime, 1);
                                mDatabase.child(Objects.requireNonNull(messageId)).setValue(chat);


                                DatabaseReference mDatabaseReceiver = FirebaseDatabase.getInstance().getReference(refReceiver);
                                String messageIdReceiver = mDatabaseReceiver.push().getKey();

                                Chat chatMsg = new Chat(messageId, uri.toString(), toUserId, toUserName, fromUserId, fromUserName, currentDateandTime, 1);
                                mDatabaseReceiver.child(Objects.requireNonNull(messageIdReceiver)).setValue(chatMsg);
                                progressDialog.dismiss();
                            }
                        });
                    }
                });

        // ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
        /*ref.putFile(data.getData())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                        ivImage.setImageURI(downloadUrl);

                        Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });*/
        ref.putFile(data.getData())

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: uri= " + uri.toString());
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(refSender);
                                String messageId = mDatabase.push().getKey();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
                                String currentDateandTime = sdf.format(new Date());
                                Chat chat = new Chat(messageId, uri.toString(), toUserId, toUserName, fromUserId, fromUserName, currentDateandTime, 1);
                                mDatabase.child(Objects.requireNonNull(messageId)).setValue(chat);


                                DatabaseReference mDatabaseReceiver = FirebaseDatabase.getInstance().getReference(refReceiver);
                                String messageIdReceiver = mDatabaseReceiver.push().getKey();

                                Chat chatMsg = new Chat(messageId, uri.toString(), toUserId, toUserName, fromUserId, fromUserName, currentDateandTime, 1);
                                mDatabaseReceiver.child(Objects.requireNonNull(messageIdReceiver)).setValue(chatMsg);
                                progressDialog.dismiss();
                            }
                        });
                    }
                });

        //ivImage.setImageBitmap(bm);
    }
}
