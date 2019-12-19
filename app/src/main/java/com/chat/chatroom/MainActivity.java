package com.chat.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.chat.chatroom.Const.MyPREFERENCES;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String TAG = "MainActivity";
    FirebaseUser currentUser;
    SharedPreferences sharedpreferences;
    String mUserId, mName, mGender;
    private RecyclerView recyclerView;
    private List<GroupMessage> groupMessageList;
    private GroupMessageAdapter adapter;
    AppCompatButton btnSend, btnEnterMain;
    AppCompatEditText etMessage;

    private RecyclerView recyclerViewUser;
    private List<User> userList;

    private RecyclerView recyclerViewChatTitle;
    private List<ChatTitle> chatTitleList;

    private FrameLayout adContainerView;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    User adclickUser;



   /* @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
         currentUser = mAuth.getCurrentUser();
//        Log.e("UserId",currentUser.getUid());
        // updateUI(currentUser);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                i.putExtra("touserid", adclickUser.getUserId());
                i.putExtra("tousername", adclickUser.getName());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                i.putExtra("touserid", adclickUser.getUserId());
                i.putExtra("tousername", adclickUser.getName());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        adContainerView = findViewById(R.id.adContainerView);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));
        adContainerView.addView(adView);
        loadBanner();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mUserId = sharedpreferences.getString(Const.USERID, "");
        mName = sharedpreferences.getString(Const.NAME, "");
        mGender = sharedpreferences.getString(Const.GENDER, "");
        etMessage = findViewById(R.id.etMessage);
        btnEnterMain = findViewById(R.id.btnEnter);
        btnSend = findViewById(R.id.btnSend);

        if (TextUtils.isEmpty(mUserId)) {
            btnEnterMain.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
            etMessage.setVisibility(View.GONE);
        } else {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groupchat");
            String messageId = mDatabase.push().getKey();
            GroupMessage groupMessage = new GroupMessage(messageId, mUserId, mName, "enter in the room", 0);
            mDatabase.child(Objects.requireNonNull(messageId)).setValue(groupMessage);
            btnEnterMain.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
            etMessage.setVisibility(View.VISIBLE);
        }

        btnEnterMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_enter);

                final AppCompatEditText etName = dialog.findViewById(R.id.etName);
                final RadioButton radioMale = dialog.findViewById(R.id.rdbMale);
                final RadioButton radioFemale = dialog.findViewById(R.id.rdbFemale);

                final AppCompatButton btnEnter = dialog.findViewById(R.id.btnEnter);
                btnEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (radioMale.isChecked()) {
                            mGender = "Male";
                        } else {
                            mGender = "Female";
                        }

                        final String name = Objects.requireNonNull(etName.getText()).toString();
                        if (TextUtils.isEmpty(name)) {
                            etName.setError("Nick Name required");
                            return;
                        }
                        if (TextUtils.isEmpty(mGender)) {
                            Toast.makeText(MainActivity.this, "Select Gender", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signInAnonymously()
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInAnonymously:success");
                                            FirebaseUser fuser = mAuth.getCurrentUser();
                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                            // String userId = mDatabase.push().getKey();
                                            User user = new User(Objects.requireNonNull(fuser).getUid(), name, mGender, 1);
                                            mDatabase.child(Objects.requireNonNull(fuser).getUid()).setValue(user);

                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString(Const.USERID, fuser.getUid());
                                            editor.putString(Const.NAME, name);
                                            editor.putString(Const.GENDER, mGender);
                                            editor.apply();
                                            mUserId = fuser.getUid();
                                            mName = name;

                                            btnEnterMain.setVisibility(View.GONE);
                                            btnSend.setVisibility(View.VISIBLE);
                                            etMessage.setVisibility(View.VISIBLE);
                                            DatabaseReference mDatabaseMsg = FirebaseDatabase.getInstance().getReference("groupchat");
                                            String messageId = mDatabaseMsg.push().getKey();
                                            GroupMessage groupMessage = new GroupMessage(messageId, mUserId, mName, "has joined this room", 1);
                                            mDatabaseMsg.child(Objects.requireNonNull(messageId)).setValue(groupMessage);
                                            etMessage.setText("");

                                            // updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            //updateUI(null);
                                        }

                                        // ...
                                    }
                                });


                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        userList = new ArrayList<>();
        recyclerViewUser = findViewById(R.id.recyclerViewUser);
        RecyclerView.LayoutManager mLayoutManagerUser = new GridLayoutManager(this, 1);
        // recyclerView.addItemDecoration(new EmployeeActivity.GridSpacingItemDecoration(4, dpToPx(3), true));
        recyclerViewUser.setLayoutManager(mLayoutManagerUser);
        recyclerViewUser.setItemAnimator(new DefaultItemAnimator());


        groupMessageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        // recyclerView.addItemDecoration(new EmployeeActivity.GridSpacingItemDecoration(4, dpToPx(3), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new GroupMessageAdapter(getApplicationContext(), groupMessageList);
        recyclerView.setAdapter(adapter);

        chatTitleList = new ArrayList<>();
        recyclerViewChatTitle = findViewById(R.id.recyclerViewChats);
        LinearLayoutManager mLayoutManagerTitle = new LinearLayoutManager(this);
        mLayoutManagerTitle.setReverseLayout(true);
        mLayoutManagerTitle.setStackFromEnd(true);
        // recyclerView.addItemDecoration(new EmployeeActivity.GridSpacingItemDecoration(4, dpToPx(3), true));
        recyclerViewChatTitle.setLayoutManager(mLayoutManagerTitle);
        recyclerViewChatTitle.setItemAnimator(new DefaultItemAnimator());


        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users");

        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getUserId().equals(mUserId) && user.getIsActive() == 1)
                        userList.add(user);
                }


                recyclerViewUser.setAdapter(new UserAdapter(MainActivity.this, userList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(User user) {
                        adclickUser = user;
                        Log.e("UserId", adclickUser.getUserId());
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();


                        } else {
                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
                            i.putExtra("touserid", adclickUser.getUserId());
                            i.putExtra("tousername", adclickUser.getName());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    }
                }));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


        DatabaseReference databaseReferenceChat = FirebaseDatabase.getInstance().getReference("chats");

        databaseReferenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chatTitleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ChatTitle chatTitle = dataSnapshot.getValue(ChatTitle.class);
                    if (chatTitle.getToUserId().equals(mUserId) || chatTitle.getFromUserId().equals(mUserId))
                        chatTitleList.add(chatTitle);
                }


                recyclerViewChatTitle.setAdapter(new ChatTitleAdapter(MainActivity.this, chatTitleList, mUserId, new OnItemClickListenerChatTitle() {
                    @Override
                    public void onItemClick(ChatTitle chatTitle) {
                        if (chatTitle.getFromUserId().equals(mUserId)) {
                            adclickUser = new User(chatTitle.getToUserId(), chatTitle.getToUser(), "", 1);
                        } else {
                            adclickUser = new User(chatTitle.getFromUserId(), chatTitle.getFromUser(), "", 1);
                        }


                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();


                        } else {
                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
                            i.putExtra("touserid", adclickUser.getUserId());
                            i.putExtra("tousername", adclickUser.getName());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    }
                }));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        Query queryRoom = FirebaseDatabase.getInstance().getReference().child("groupchat").limitToLast(100);
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groupchat");
        queryRoom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        GroupMessage groupMessage = dataSnapshot.getValue(GroupMessage.class);

                        groupMessageList.add(groupMessage);
                        recyclerView.scrollToPosition(groupMessageList.size() - 1);
                        adapter.notifyItemInserted(groupMessageList.size() - 1);


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

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groupchat");
                    String messageId = mDatabase.push().getKey();
                    GroupMessage groupMessage = new GroupMessage(messageId, mUserId, mName, Objects.requireNonNull(etMessage.getText()).toString(), 1);
                    mDatabase.child(Objects.requireNonNull(messageId)).setValue(groupMessage);
                    etMessage.setText("");
                }

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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(mUserId).child("isActive").setValue(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mUserId)) {
            try {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.child(mUserId).child("isActive").setValue(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
