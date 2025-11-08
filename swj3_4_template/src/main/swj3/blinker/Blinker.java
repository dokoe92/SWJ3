package swj3.blinker;

// IST EIGENTLICH NICHT AM PLAN ABER TROTZDEM FÜR UNS ZUR INFO DRINNEN

/**
 * complete example for implementing stop, suspend, resume and interrupt
 * handling from http://docs.oracle.com/javase/7/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
 */
public class Blinker implements Runnable {

    private final int interval = 10;

    private Thread blinker;
    private volatile boolean threadSuspended;

    public synchronized void suspend() {
        System.out.println("suspend(): suspending...");
        threadSuspended = true;
    }

    public synchronized void resume() {
        System.out.println("resume(): resuming...");
        threadSuspended = false;
        notify();
    }

    public void start() {
        System.out.println("start(): starting...");
        blinker = new Thread(this);
        blinker.start();
    }

    public synchronized void stop() {
        System.out.println("stop(): stopping...");
        blinker = null;
        notify();
    }

    public synchronized void interrupt() {
        blinker.interrupt();
    }

    public void run() {
        System.out.println("run(): Blinker running");
        Thread thisThread = Thread.currentThread();
        while (blinker == thisThread) {
            try {
                System.out.println("run(): sleeping...");
                Thread.sleep(interval);
                synchronized (this) {
                    while (threadSuspended) { // `if'
                        System.out.println("run(): waiting...");
                        wait();
                    }
                }
                System.out.println("run(): continuing...");
            } catch (InterruptedException e) {
                System.out.println("run(): was interrupted");
            }
            System.out.println("run(): BLINK");
        }
        System.out.println("run(): Blinker done.");
    }

    public static void main(String[] args) throws InterruptedException {
        Blinker b = new Blinker();
        b.start();
        Thread.sleep(15);
        b.suspend();
        Thread.sleep(15);
        b.resume();
        Thread.sleep(15);
        b.interrupt();
        b.stop();
        Thread.sleep(100);
    }
}
