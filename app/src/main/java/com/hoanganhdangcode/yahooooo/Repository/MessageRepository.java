package com.hoanganhdangcode.yahooooo.Repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoanganhdangcode.yahooooo.Model.UserMessage;
import com.hoanganhdangcode.yahooooo.Service.NotificationServices;
import com.hoanganhdangcode.yahooooo.Service.SendMediaMessageService;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MessageRepository {

    private final DatabaseReference realtimeDb = FirebaseDatabase.getInstance().getReference();
    private final FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    private ChildEventListener messageListener;
    private boolean isreading = false;
    private final List<UserMessage> messageCache = new ArrayList<>();

    public interface OnMessagesUpdated {
        void onUpdated(List<UserMessage> messages);
    }

    public interface OnSenderInfoLoaded {
        void onLoaded(String uid, String name, String avatar);
    }

    public void listenMessages(String chatId, String currentUid, OnMessagesUpdated callback) {
        clear();
        messageCache.clear();
        isreading = true;
        messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserMessage msg = snapshot.getValue(UserMessage.class);
                if (msg != null) {
                    messageCache.add(msg);
                    if (msg.getStatus() == 1 && (!msg.getSender().equals(currentUid)) && isreading) {
                        snapshot.getRef().child("status").setValue(2);
                    }
                    sortMessagesByTimestamp();
                    callback.onUpdated(new ArrayList<>(messageCache));
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserMessage updated = snapshot.getValue(UserMessage.class);
                if (updated != null) {
                    for (int i = 0; i < messageCache.size(); i++) {
                        if (messageCache.get(i).getMessageid().equals(updated.getMessageid())) {
                            messageCache.set(i, updated);
                            break;
                        }

                    }
                    sortMessagesByTimestamp();
                    callback.onUpdated(new ArrayList<>(messageCache));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                UserMessage removed = snapshot.getValue(UserMessage.class);
                if (removed != null) {
                    messageCache.removeIf(m -> m.getMessageid().equals(removed.getMessageid()));
                    callback.onUpdated(new ArrayList<>(messageCache));
                }
            }
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        realtimeDb.child("messages").child(chatId).addChildEventListener(messageListener);
    }

    private void sortMessagesByTimestamp() {
        messageCache.sort(Comparator.comparingLong(UserMessage::getTimestamp));
    }

    public void getTokenNoti(String uid, String chatId, OnTokenNotiLoaded callback) {
        firestoreDb.collection("userchat")
                .document(uid)
                .collection("chat")
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> tokens = new ArrayList<>();
                        firestoreDb.collection("token")
                                .whereEqualTo("uid", documentSnapshot.getString("uid2"))
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    tokens.add(documentSnapshot.getString("token"));
                                    callback.onLoaded(tokens);
                                })
                                .addOnFailureListener(e -> {
                                            Log.e("MessageRepository", "Error getting tokens: ", e);
                                            callback.onError("Failed to get tokens: " + e.getMessage());
                                        }
                                );
                    } else {
                        callback.onError("Chat document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MessageRepository", "Error getting tokens: ", e);
                    callback.onError("Failed to get chat document: " + e.getMessage());
                });

    }
    public interface OnTokenNotiLoaded {
        void onLoaded(List<String> tokens);
        void onError(String error);
    }
    public void sendMessage(Context context, UserMessage message) {
        realtimeDb.child("messages")
                .child(message.getChatid())
                .child(message.getMessageid())
                .setValue(message);

        if (message.getType() >= 2 && message.getLocaluri() != null) {
            SendMediaMessageService.start(context, Uri.parse(message.getLocaluri()), message.getChatid(), message.getMessageid());
        }

        firestoreDb.collection("chatbox").document(message.getChatid())
                .update("lastmessage", message.getType() == 1 ? message.getContent() : "Đã gửi một tệp đính kèm");
        firestoreDb.collection("chatbox").document(message.getChatid())
                .update("lasttimestamp", message.getTimestamp());

        getSenderInfo(message.getSender(), (uid, name, avatar) ->{
            firestoreDb.collection("chatbox").document(message.getChatid())
                    .update("userlast", name != null ? name : "Không rõ");
            getTokenNoti(message.getSender(), message.getChatid(), new OnTokenNotiLoaded() {
                @Override
                public void onLoaded(List<String> tokens) {
                    if (!tokens.isEmpty()) {
                        Utils.sendBulkNotification(tokens,"Tin nhắn mới",name +": "+ (message.getType()==1? message.getContent():"Đã gửi một tệp đính kèm"));
                        Log.d("MessageRepository", "sendMessage: tokens size: " + tokens.size());
                    }

                }

                @Override
                public void onError(String error) {

                }
            });
        });


    }

        public void getSenderInfo(String uid, OnSenderInfoLoaded callback) {
            FirebaseFirestore.getInstance()
                    .collection("user")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String name = document.getString("name");
                            String avatar = document.getString("avatar");
                            callback.onLoaded(uid, name, avatar);
                        } else {
                            callback.onLoaded(uid, "", "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onLoaded(uid, "", "");
                    });
        }



    public void clear() {
        isreading = false;
        if (messageListener != null) {
            realtimeDb.removeEventListener(messageListener);
        }
    }

    public void resume() {
        isreading = true;

    }


}