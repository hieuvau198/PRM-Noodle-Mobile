package com.example.prm_v3.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.prm_v3.R;
import com.example.prm_v3.ui.orders.OrderDetailActivity;
import com.example.prm_v3.ui.payment.PaymentDetailActivity;

public class NotificationHelper {
    private static final String CHANNEL_ID = "ORDER_UPDATES";
    private static final String CHANNEL_NAME = "Order Updates";
    private static final String CHANNEL_DESCRIPTION = "Notifications for order status updates";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showOrderUpdateNotification(int orderId, String customerName, String newStatus) {
        String title = "Cập nhật đơn hàng #" + orderId;
        String message = customerName + " - " + getStatusText(newStatus);

        // Create intent to open order detail
        Intent intent = OrderDetailActivity.newIntent(context, orderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                orderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        notificationManager.notify(orderId, builder.build());
    }

    public void showNewOrderNotification(int orderId, String customerName, double totalAmount) {
        String title = "Đơn hàng mới #" + orderId;
        String message = customerName + " - " + String.format("%.0f₫", totalAmount);

        Intent intent = OrderDetailActivity.newIntent(context, orderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                orderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 250, 250, 250})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        notificationManager.notify(orderId, builder.build());
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "preparing": return "Đang chuẩn bị";
            case "delivered": return "Đang vận chuyển";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return "Không xác định";
        }
    }
    public void showPaymentCreatedNotification(int paymentId, int orderId, String customerName, double amount) {
        String title = "Tạo thanh toán thành công";
        String message = String.format("Thanh toán #%d cho đơn hàng #%d\n%s - %,.0f₫",
                paymentId, orderId, customerName, amount);

        Intent intent = PaymentDetailActivity.newIntent(context, paymentId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                paymentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_payment)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.green_600))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        notificationManager.notify(paymentId + 20000, builder.build());
    }
}