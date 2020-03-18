package com.mpr.dalvikrtm1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    // We want to start just 2 threads at the same time, but let's control that
    // timing from the main thread. That's why we have 3 "parties" instead of 2.
    final CyclicBarrier gate = new CyclicBarrier(4);
//create fair lock
    //after running this code change it to
    //ReentrantLock(false); to see what happens

    final Lock lock = new ReentrantLock(true);

    private Button mbutton;
    TextView textView;
    private String FILENAME;
    private File _file;
    int licznik = 0;
    private String dane;
    private long wartoscThreat1;
    private long wartoscThreat2;
    private long wartoscThreat3;
    long start1 = System.currentTimeMillis();
    long start2 = System.currentTimeMillis();
    long start3 = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        mbutton = findViewById(R.id.button_send);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //pierwszy wątek
        final Thread thread1 = new Thread() {

            @Override
            public void run() {
                try {
                    gate.await();

                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 50; i++) {
                    lock.lock();
                    System.out.println(i + "pętla1");
                    lock.unlock();

                }
                long stopTime = System.currentTimeMillis();
                long elapsedTime1 = stopTime - start1;
                System.out.println(elapsedTime1);
                wartoscThreat1 = elapsedTime1;
            }

        };
        thread1.setName("watek1_Thread1");
        thread1.setPriority(Thread.MIN_PRIORITY);

        // thread1.start();

        //drugi wątek
        Thread thread2 = new Thread() {

            @Override
            public void run() {
                try {
                    gate.await();

                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 50; i++) {
                    lock.lock();
                    System.out.println(i + "pętla2");
                    lock.unlock();

                }
                long stopTime = System.currentTimeMillis();
                long elapsedTime2 = stopTime - start2;
                System.out.println(elapsedTime2);
                wartoscThreat2 = elapsedTime2;
            }

        };
        thread2.setName("watek2_Thread2");
        // thread2.setPriority(Thread.NORM_PRIORITY);
        //  thread2.start();


        //trzeci wątek
        Thread thread3 = new Thread() {

            @Override
            public void run() {

                try {
                    gate.await();

                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 50; i++) {
                    lock.lock();
                    System.out.println(i + "pętla3");
                    lock.unlock();

                }
                long stopTime = System.currentTimeMillis();
                long elapsedTime3 = stopTime - start3;
                System.out.println(elapsedTime3);
                wartoscThreat3 = elapsedTime3;
            }

        };
        thread3.setName("watek3_Thread3");
        thread3.setPriority(10);

        // thread3.start();

        System.out.println("priorytet thread1: " + thread1.getPriority());
        System.out.println("priorytet thread2: " + thread2.getPriority());
        System.out.println("priorytet thread3: " + thread3.getPriority());


        // thread1.setPriority(Thread.MIN_PRIORITY);
        // thread2.setPriority(Thread.NORM_PRIORITY);
        //thread3.setPriority(Thread.MAX_PRIORITY);


        thread2.start();
        thread1.start();
        thread3.start();


        // At this point, t1 & t2 & t3 are blocking on the gate.
        // Since we gave "4" as the argument, gate is not opened yet.
        // Now if we block on the gate from the main thread, it will open
        // and all threads will start to do stuff!


        try {
            gate.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("all threads started");


        //dane jakie są zapisywane do pliku
        dane = Thread.currentThread().getName() + " " + Thread.currentThread().getPriority();


        // Utworzenie pliku
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        FILENAME = "Data_Usage_THREAD_" + dateFormat.format(new Date()) + ".txt";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Projekt/");
            if (!file.exists()) {
                file.mkdirs();
            }
            _file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILENAME);
            _file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (licznik < 10) {
            DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                String dataToSave = dateTimeFormat.format(new Date()) + dane + " //" +
                        "  wartosc 1 petli i priorytet  to: " + wartoscThreat1 + " " + thread1.getPriority()
                        + " wartosc 2 petli i priorytet to: " + wartoscThreat2 + " " + thread2.getPriority()
                        + " wartosc 3 petli i priorytet to: " + wartoscThreat3 + " " + thread3.getPriority();
                SaveToFile(dataToSave);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //SystemClock.sleep(1000);
            licznik++;
        }


    }

    private void SaveToFile(String data) {
        try {
            FileOutputStream fileinput = new FileOutputStream(_file, true);
            PrintStream printstream = new PrintStream(fileinput);
            printstream.print(data + "\n\n");
            fileinput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
