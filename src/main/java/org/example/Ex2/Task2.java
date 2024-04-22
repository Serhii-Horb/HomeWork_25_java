package org.example.Ex2;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/* На склад приезжает машина, в которой загружено N ящиков (пользователь вводит с клавиатуры).
Автоматизированный разгрузчик вытягивает с машины ящик и кладет на рампу с определенным интервалом времени, на
которой может одновременно разместиться например 2 ящика. В это время подъезжает автоматизированный погрузчик и везет
его на место хранения. Он за раз может взять всего 1 ящик. Каждый участник процесса представлен в виде отдельного
потока Java. Создайте программу, которая синхронизирует работу в данном процессе разгрузки автомобиля, с условием
того, что Разгрузчик может быть только один, а Погрузчиков может быть 2 и более (на ваше усмотрение). */

public class Task2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество ящиков: ");
        int n = scanner.nextInt();

        AtomicInteger auto = new AtomicInteger(n);
        AtomicInteger ramp = new AtomicInteger(0);

        System.out.println("В машине приехало " + n + " ящиков!");

        FirstThread thread1 = new FirstThread(ramp, auto);
        MyThread thread2 = new MyThread(ramp, auto, 1);
        MyThread thread3 = new MyThread(ramp, auto, 2);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Машина разгружена!");
    }
}

class FirstThread extends Thread {
    private AtomicInteger ramp;
    private AtomicInteger auto;

    public FirstThread(AtomicInteger ramp, AtomicInteger auto) {
        this.ramp = ramp;
        this.auto = auto;
    }

    @Override
    public void run() {
        while (auto.get() > 0) {
            synchronized (ramp) {
                while (ramp.get() < 2) {
                    if (auto.get() > 0) {
                        System.out.println("Кладу ящик на рампу");
                        auto.getAndDecrement();
                        ramp.getAndIncrement();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Рампа " + ramp.get());
                        System.out.println("В машине осталось " + auto.get() + " ящиков");
                    }
                }
                try {
                    ramp.notifyAll();
                    ramp.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

class MyThread extends Thread {
    private AtomicInteger ramp;
    private AtomicInteger auto;
    private int numThread;

    public MyThread(AtomicInteger ramp, AtomicInteger auto, int numThread) {
        this.ramp = ramp;
        this.auto = auto;
        this.numThread = numThread;
    }

    @Override
    public void run() {
        while (auto.get() >= 0) {
            synchronized (ramp) {
                if (ramp.get() > 0) {
                    ramp.getAndDecrement();
                    System.out.println("Погрузчик № " + numThread + " забрал ящик");
                }
                try {
                    ramp.notify();
                    if (auto.get() == 0) {
                        return;
                    }
                    ramp.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


