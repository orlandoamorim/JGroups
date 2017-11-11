import java.util.*;
import java.io.*;

public class Protocol implements Serializable {

  String type;
  Map<String, String[]> map = new HashMap<String, String[]>();

  public Protocol(String type, Map<String, String[]> map) {
    this.type = type;
    this.map = map;
  }
}
