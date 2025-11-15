package swj3.serialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Writer {

    public static void main(String[] args) {
        Data d = new Data("Hello, Disk!");
        try (FileOutputStream fs = new FileOutputStream("hello.ser");
             ObjectOutputStream os = new ObjectOutputStream(fs)) {
            os.writeObject(d);
            System.out.printf("wrote >>>%s<<<\n", d);
        } catch (IOException e) {
            System.err.println("Could not write output file");
            e.printStackTrace();
        }
        System.out.println("done");
    }

}
