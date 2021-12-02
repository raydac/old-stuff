package R_HTTPResponse;

public class ClientRecord {
  public String password;
  public String name;
  public int MaxConnectNumber;

  public ClientRecord(String nm, String pssw, int cn) {
    password = pssw;
    name = nm;
    MaxConnectNumber = cn;
  }

}
