package swj3.serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Data implements Serializable {


  private static final long serialVersionUID = 5346863017330658272L;

  private String value;
  transient private Socket s = new Socket();

  public Data(String value) {
    System.out.println("creating Data(" + value + ")");
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("Data(\"%s\"), socket = %s", value, s);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    System.out.println("Writing value");
    out.defaultWriteObject();
  }

  private void writeObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    System.out.println("Reading value");
    in.defaultReadObject();
    s = new Socket();
  }

  // NOTE: called if class needs to be instantiated as superclass of another object
  private void readObjectNoData() {
    s = new Socket();
  }
}


