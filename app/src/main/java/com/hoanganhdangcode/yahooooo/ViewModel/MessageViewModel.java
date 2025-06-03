package com.hoanganhdangcode.yahooooo.ViewModel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hoanganhdangcode.yahooooo.Model.UserMessage;
import com.hoanganhdangcode.yahooooo.Repository.MessageRepository;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageViewModel extends ViewModel {

    private final MessageRepository repository = new MessageRepository();
    private final MutableLiveData<List<UserMessage>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());

    private final HashMap<String, String> nameMap = new HashMap<>();
    private final HashMap<String, String> avatarMap = new HashMap<>();
    private final MutableLiveData<String> namechat = new MutableLiveData<>();
    private final MutableLiveData<String> avatarchat = new MutableLiveData<>();
    private final MutableLiveData<String> chatstatus = new MutableLiveData<>();



    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String chatId;
    private String currentUid;
    private Context appContext;

    public LiveData<String> getChatstatus() {
        return chatstatus;
    }

    public LiveData<String> getNamechat() {
        return namechat;
    }

    public LiveData<String> getAvatarchat() {
        return avatarchat;
    }

    public void getdatachat(String myuid, String chatId) {
        firestore.collection("userchat").document(myuid).collection("chat").document(chatId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    String avatar = documentSnapshot.getString("avatar");
                    String uid2 = documentSnapshot.getString("uid2");

                    if (name == null || name.isEmpty()) {
                        firestore.collection("user").document(uid2).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        String name1 = doc.getString("name");
                                        namechat.setValue(name1 != null ? name1 : "Không xác định");
                                    } else {
                                        namechat.setValue("Không xác định");
                                    }
                                })
                                .addOnFailureListener(e -> namechat.setValue("Không xác định"));
                    } else {
                        namechat.setValue(name);
                    }

                    if (avatar == null || avatar.isEmpty()) {
                        firestore.collection("user").document(uid2).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        String avatar1 = doc.getString("avatar");
                                        avatarchat.setValue(avatar1 != null ? avatar1 : Utils.urlavatardefault);
                                    } else {
                                        avatarchat.setValue(Utils.urlavatardefault);
                                    }
                                })
                                .addOnFailureListener(e -> avatarchat.setValue(Utils.urlavatardefault));
                    } else {
                        avatarchat.setValue(avatar);
                    }
                })
                .addOnFailureListener(e -> {
                    namechat.setValue("Không xác định");
                    avatarchat.setValue(Utils.urlavatardefault);
                });
    }

    public void initialize(String chatId, String currentUid, Context context) {
        this.chatId = chatId;
        this.currentUid = currentUid;
        this.appContext = context.getApplicationContext();
        loadMessages();
    }



    private void loadMessages() {
        repository.listenMessages(chatId, currentUid, list -> {
            messagesLiveData.postValue(list);
            preloadUserInfo(list);
        });


    }

    private void preloadUserInfo(List<UserMessage> messages) {
        for (UserMessage msg : messages) {
            String uid = msg.getSender();
            if (!nameMap.containsKey(uid)) {
                repository.getSenderInfo(uid, (uid1, name, avatar) -> {
                    nameMap.put(uid1, (name != null && !name.isEmpty()) ? name : "Không rõ");
                    avatarMap.put(uid1, (avatar != null && !avatar.isEmpty()) ? avatar : Utils.urlavatardefault);

                    List<UserMessage> current = messagesLiveData.getValue();
                    if (current != null) {
                        messagesLiveData.postValue(new ArrayList<>(current));
                    }
                });
            }
        }
    }


    public void sendMessage( UserMessage message) {
        repository.sendMessage(appContext,message);
    }

    public void clear() {
        repository.clear();
    }

    public void resume( ) {
        repository.resume();
    }

    public LiveData<List<UserMessage>> getMessages() {
        return messagesLiveData;
    }

    public HashMap<String, String> getNameMap() {
        return nameMap;
    }

    public HashMap<String, String> getAvatarMap() {
        return avatarMap;
    }
}