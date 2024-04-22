package org.example.Ex1;

import java.util.Scanner;

/* У вас есть стол, на который один робоманипулятор кладет деталь, а второй забирает эту деталь.
Когда Робот1 положит деталь на стол, он должен обязательно ждать, пока Робот2 заберет эту деталь и только тогда
класть следующую. Постройте программу, которая автоматизирует эту работу.
Количество деталей, которые должны обработать манипуляторы, пользователь задает с клавиатуры. */

public class Task1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество деталей: ");
        int count = scanner.nextInt();

        Table table = new Table();

        ManipulatorIncrementDetails manipulatorIncrementDetails = new ManipulatorIncrementDetails(table);
        manipulatorIncrementDetails.setCount(count);
        ManipulatorDecrementDetails manipulatorDecrementDetails = new ManipulatorDecrementDetails(table);
        manipulatorDecrementDetails.setCount(count);

        Thread thread1 = new Thread(manipulatorIncrementDetails);
        Thread thread2 = new Thread(manipulatorDecrementDetails);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Мы переложили все детали!");
    }
}

class Table {
    private int detailCount = 0;

    public synchronized void getDetail() {
        if (detailCount < 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        detailCount--;
        System.out.println("Робоманипулятор забрал одну деталь");
        System.out.println("Количество деталей на столе = " + detailCount);
        notify();
    }

    public synchronized void putDetail() {
        if (detailCount == 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        detailCount++;
        System.out.println("Робоманипулятор положил одну деталь");
        System.out.println("Количество деталей на столе = " + detailCount);
        notify();
    }
}

class ManipulatorIncrementDetails implements Runnable {
    private int count;
    Table table;

    public ManipulatorIncrementDetails(Table table) {
        this.table = table;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            table.putDetail();
        }
    }

    public void setCount(int count) {
        this.count = count;
    }
}

class ManipulatorDecrementDetails implements Runnable {
    private int count;
    Table table;

    public ManipulatorDecrementDetails(Table table) {
        this.table = table;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            table.getDetail();
        }
    }

    public void setCount(int count) {
        this.count = count;
    }
}




