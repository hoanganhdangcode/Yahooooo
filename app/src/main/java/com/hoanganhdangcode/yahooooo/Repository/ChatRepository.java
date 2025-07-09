package com.hoanganhdangcode.yahooooo.Repository;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.accesstoken;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.serverip;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.tokensaphethan;
import static com.hoanganhdangcode.yahooooo.Util.HttpUtils.getJsonWithToken;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.hoanganhdangcode.yahooooo.Activity.ChatActivity;
import com.hoanganhdangcode.yahooooo.Activity.SplashActivity;
import com.hoanganhdangcode.yahooooo.Model.Chat;
import com.hoanganhdangcode.yahooooo.Model.ChatDisplay;
import com.hoanganhdangcode.yahooooo.Model.UserChat;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface loadChatCallback {
        void onComplete(List<ChatDisplay> list);
        void onFailure(Exception e);
    }


    public void loadChat(String uid, loadChatCallback callback) {
        db.collection("userchat").document(uid).collection("chat")
                .get()
                .addOnSuccessListener(chatSnapshots -> {
                    if (chatSnapshots.isEmpty()) {
                        callback.onComplete(new ArrayList<>());
                        return;
                    }

                    Map<String, UserChat> userChatMap = new HashMap<>();

                    for (DocumentSnapshot doc : chatSnapshots) {
                        UserChat uc = doc.toObject(UserChat.class);
                        if (uc != null) userChatMap.put(uc.getChatid(), uc);
                    }

                    List<ChatDisplay> resultList = new ArrayList<>();
                    final int total = userChatMap.size();
                    final int[] processed = {0};

                    for (String chatid : userChatMap.keySet()) {
                        db.collection("chatbox").document(chatid).get()
                                .addOnSuccessListener(chatDoc -> {
                                    Chat chat = chatDoc.toObject(Chat.class);
                                    if (chat != null) {
                                        UserChat uc = userChatMap.get(chatid);

                                        String nameInit = uc.getNamechat() != null ? uc.getNamechat() : "";
                                        String avatarInit = uc.getAvatarchat() != null ? uc.getAvatarchat() : "";

                                        if (nameInit.isEmpty() || avatarInit.isEmpty()) {
                                            db.collection("user").document(uc.getUid2())
                                                    .get()
                                                    .addOnSuccessListener(userDoc -> {
                                                        String name = nameInit;
                                                        String avatar = avatarInit;

                                                        String fallbackName = userDoc.getString("name");
                                                        String fallbackAvatar = userDoc.getString("avatar");

                                                        if (name.isEmpty()) name = fallbackName != null ? fallbackName : "";
                                                        if (avatar.isEmpty()) avatar = fallbackAvatar != null ? fallbackAvatar : "";

                                                        resultList.add(buildChatDisplay(chat, uid, uc, name, avatar));
                                                        processed[0]++;
                                                        if (processed[0] == total) callback.onComplete(resultList);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        resultList.add(buildChatDisplay(chat, uid, uc, nameInit, avatarInit));
                                                        processed[0]++;
                                                        if (processed[0] == total) callback.onComplete(resultList);
                                                    });
                                        } else {
                                            resultList.add(buildChatDisplay(chat, uid, uc, nameInit, avatarInit));
                                            processed[0]++;
                                            if (processed[0] == total) callback.onComplete(resultList);
                                        }
                                    } else {
                                        processed[0]++;
                                        if (processed[0] == total) callback.onComplete(resultList);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    processed[0]++;
                                    if (processed[0] == total) callback.onComplete(resultList);
                                });
                    }

                })
                .addOnFailureListener(callback::onFailure);
    }


    private ChatDisplay buildChatDisplay(Chat chat, String currentUid, UserChat uc, String name, String avatar) {
        return new ChatDisplay(
                chat.getChatid(),
                chat.getType(),
                currentUid,
                uc.getUid2(),
                name,
                avatar,
                chat.getLastmessage(),
                chat.getUserlast(),
                chat.getLasttimestamp()
        );
    }

    // ===== CREATE PRIVATE CHAT =====
    public interface OnChatCreated {
        void onSuccess(String chatid);
        void onFailure(Exception e);
    }

    public void createPrivateChatIfNotExists(String uid1, String uid2, int type, @Nullable OnChatCreated callback) {
        if (uid1.equals(uid2)) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Can't chat with self"));
            return;
        }

        String chatId = uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
        long now = Utils.gettime();

        db.collection("chatbox").document(chatId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Map<String, Object> update = new HashMap<>();
                update.put("type", type);
                update.put("lasttimestamp", now);
                db.collection("chatbox").document(chatId).set(update, SetOptions.merge())
                        .addOnSuccessListener(unused -> {
                            if (callback != null) callback.onSuccess(chatId);
                        })
                        .addOnFailureListener(e -> {
                            if (callback != null) callback.onFailure(e);
                        });
                return;
            }
            // Tạo mới nếu chưa tồn tại
            db.collection("user").document(uid1).get().addOnSuccessListener(user1Doc -> {
                db.collection("user").document(uid2).get().addOnSuccessListener(user2Doc -> {

                    String name1 = user1Doc.getString("name");
                    String avatar1 = user1Doc.getString("avatar");
                    String name2 = user2Doc.getString("name");
                    String avatar2 = user2Doc.getString("avatar");

                    Chat chat = new Chat(chatId, type, "", name1, now);
                    UserChat userChat1 = new UserChat(uid1, chatId, uid2, name2, avatar2, false, false);
                    UserChat userChat2 = new UserChat(uid2, chatId, uid1, name1, avatar1, false, false);

                    WriteBatch batch = db.batch();
                    batch.set(db.collection("chatbox").document(chatId), chat);
                    batch.set(db.collection("userchat").document(uid1).collection("chat").document(chatId), userChat1);
                    batch.set(db.collection("userchat").document(uid2).collection("chat").document(chatId), userChat2);
                    batch.commit()
                            .addOnSuccessListener(unused -> {
                                if (callback != null) callback.onSuccess(chatId);
                            })
                            .addOnFailureListener(e -> {
                                if (callback != null) callback.onFailure(e);
                            });

                }).addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
            }).addOnFailureListener(e -> {
                if (callback != null) callback.onFailure(e);
            });

        }).addOnFailureListener(e -> {
            if (callback != null) callback.onFailure(e);
        });
    }

}
