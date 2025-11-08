package swj3.printer;

import swj3.util.Util;

import java.util.Random;

public class Printer implements Runnable {
  private char ch;
  private int rows;
  private int cols;

  public Printer(char ch, int rows, int cols) {
    this.ch = ch;
    this.rows = rows;
    this.cols = cols;
  }

  public void print1() {
    Random r = new Random();

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        System.out.print(ch);
        Util.sleep(r.nextInt(10));
      }
      System.out.println();
    }
  }

  private static final Object lock = new Object();

  public void print2() {
    Random r = new Random();

    for (int i = 0; i < rows; i++) {
      synchronized (lock) {
        for (int j = 0; j < cols; j++) {
          System.out.print(ch);
          Util.sleep(r.nextInt(10));
        }
        System.out.println();
      }
      //    Thread.yield();
      Util.sleep(r.nextInt(10));
    }

  }

  @Override
  public void run() {
    print2();
  }

  public static void main(String[] args) {
    System.out.printf("start main (carrier thread: %s)%n", Thread.currentThread());

    Printer p1 = new Printer('x', 10, 60);
    Printer p2 = new Printer('o', 10, 60);

    // schwergewichtige Threads vom Betriebssystem -teuer wegen Context Switch etc.
//    Thread t1 = Thread.ofPlatform().name("P1").start(p1);
//    Thread t2 = Thread.ofPlatform().name("P2").start(p2);

    // leichtgewichtige Threads
    Thread t1 = Thread.ofVirtual().name("P1").start(p1);
    Thread t2 = Thread.ofVirtual().name("P2").start(p2);


    // p1.run();
    // p2.run();

    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {

    }

    System.out.printf("end main (carrier thread: %s)%n", Thread.currentThread());
  }
}
