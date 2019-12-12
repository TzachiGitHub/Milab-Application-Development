package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.Notification;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import static com.example.notification.baseAplication.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    Notification notification;
    String[] Quotes;
    private int i;
    private final String title = "Life Lessons for the wise and willing:";

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);
        qouteMaker();

    }

    public void sendOnChannel1(View v) {
        final View notificationView = v;
        i = 0;
        new CountDownTimer(2147483647, 1000 * 5 * 60 ) {
            public void onTick(long millisecondsUntilDone) {
                Log.i("So we have about ", String.valueOf(millisecondsUntilDone / 15000) + " notifications left");
                setNotification(notificationView, i);
                i = (i + 1) % 20;
                notificationManager.notify(1, notification);
            }

            public void onFinish() {
                Log.i("We're done!", "Finished!");
            }

        }.start();

    }

    // Setting the notification
    public void setNotification(View v, int index) {
        String message = Quotes[index];

        this.notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(""))
                .build();
    }

    // Arranging the Qoute array
    public void qouteMaker() {
        this.Quotes = new String[21];
        this.Quotes[0] = "There is nothing permanent except change.";
        this.Quotes[1] = "You cannot shake hands with a clenched fist.";
        this.Quotes[2] = "Let us sacrifice our today so that our children can have a better tomorrow.";
        this.Quotes[3] = "It is better to be feared than loved, if you cannot be both.";
        this.Quotes[4] = "The most difficult thing is the decision to act, the rest is merely tenacity. The fears are paper tigers. You can do anything you decide to do. You can act to change and control your life; and the procedure, the process is its own reward.";
        this.Quotes[5] = "Do not mind anything that anyone tells you about anyone else. Judge everyone and everything for yourself.";
        this.Quotes[6] = "Learning never exhausts the mind.";
        this.Quotes[7] = "There is no charm equal to tenderness of heart.";
        this.Quotes[8] = "All that we see or seem is but a dream within a dream.";
        this.Quotes[9] = "Lord, make me an instrument of thy peace. Where there is hatred, let me sow love.";
        this.Quotes[10] = "The only journey is the one within.";
        this.Quotes[11] = "Good judgment comes from experience, and a lot of that comes from bad judgment.";
        this.Quotes[12] = "Think in the morning. Act in the noon. Eat in the evening. Sleep in the night.";
        this.Quotes[13] = "Life without love is like a tree without blossoms or fruit.";
        this.Quotes[14] = "No act of kindness, no matter how small, is ever wasted.";
        this.Quotes[15] = "Love cures people - both the ones who give it and the ones who receive it.";
        this.Quotes[16] = "Work like you don't need the money. Love like you've never been hurt. Dance like nobody's watching.";
        this.Quotes[17] = "It is far better to be alone, than to be in bad company.";
        this.Quotes[18] = "If you cannot do great things, do small things in a great way.";
        this.Quotes[19] = "Permanence, perseverance and persistence in spite of all obstacles, discouragements, and impossibilities: It is this, that in all things distinguishes the strong soul from the weak.";
        this.Quotes[20] = "Independence is happiness.";
    }
}