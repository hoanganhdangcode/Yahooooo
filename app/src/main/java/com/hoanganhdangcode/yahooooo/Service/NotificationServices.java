//// MyFirebaseMessagingService.java
//package com.hoanganhdangcode.yahooooo.Service;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
//import androidx.core.app.NotificationCompat;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.hoanganhdangcode.yahooooo.Activity.HomeActivity;
//import com.hoanganhdangcode.yahooooo.R;
//import com.hoanganhdangcode.yahooooo.Util.Utils;
//
//public class NotificationServices extends FirebaseMessagingService {
//    private static final String TAG = "MyFirebaseMsgService";
//    private static final String CHANNEL_ID = "fcm_default_channel";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        String currentuid = Utils.getpref(this, "logined", "uid");
//        String tagetuid = remoteMessage.getData().get("uid");
//
//        // Check if message contains a notification payload
//        if (remoteMessage.getNotification() == null) {
//            Log.d(TAG, "Notification payload is null");
//            return;
//        }
//        if (remoteMessage.getNotification() != null
//                && currentuid != null
//                && currentuid.equals(tagetuid)) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            // Hiển thị notification khi app foreground
//            sendNotification(remoteMessage.getNotification().getTitle(),
//                    remoteMessage.getNotification().getBody());
//        }
//
//        // Check if message contains a data payload
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//    }
//
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // Send token to your app server
//        sendRegistrationToServer(token);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        Log.d(TAG, "sendRegistrationToServer(" + token + ")");
//    }
//
//    private void sendNotification(String title, String messageBody) {
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_IMMUTABLE);
//
//        createNotificationChannel();
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_notification) // Cần tạo icon này
//                        .setContentTitle(title)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "FCM Default Channel";
//            String description = "Channel for FCM notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//}