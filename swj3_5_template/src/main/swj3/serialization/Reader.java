package swj3.serialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Reader {

    public static void main(String[] args) {
        try (FileInputStream fs = new FileInputStream("hello.ser");
             ObjectInputStream os = new ObjectInputStream(fs)) {
            Object o = os.readObject();
            if (o instanceof Data) {
                Data d = (Data) o;
                System.out.printf("read >>>%s<<<\n", d);
            }
        } catch (IOException e) {
            System.err.println("could not read input file");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("cloud not find target class");
            e.printStackTrace();
        }
        System.out.println("done");
    }
}
