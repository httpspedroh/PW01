import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

// ---------------------------------------------------------------------------------------------------------------- //

class BankAccount{


    private int id, transfers;
    private String name, user, pass, cpf, city;
    ArrayList<String> emails;
    private float balance;

    public BankAccount() {

        this.id = -1;
        this.name = this.user = this.pass = this.cpf = this.city = null;
        this.balance = -1;
        this.transfers = 0;
        this.emails = new ArrayList<String>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUser() { return user; }
    public String getPass() { return pass; }
    public String getCpf() { return cpf; }
    public String getCity() { return city; }
    public float getBalance() { return balance; }
    public int getEmailsCount() { return emails.size(); }
    public int getTransfers() { return transfers; }
    public ArrayList<String> getEmails() { return emails; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUser(String user) { this.user = user; }
    public void setPass(String pass) { this.pass = pass; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCity(String city) { this.city = city; }
    public void setBalance(float balance) { this.balance = balance; }
    public void setTransfers(int transfers) { this.transfers = transfers; }
    public void addEmail(String email) { this.emails.add(email); }

    public byte[] toByteArray() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.getId());
        dos.writeUTF(this.getName());
        dos.writeInt(this.getEmailsCount());

        for (String email : this.getEmails()) dos.writeUTF(email);

        dos.writeUTF(this.getUser());
        dos.writeUTF(this.getPass());
        dos.writeUTF(this.getCpf());
        dos.writeUTF(this.getCity());
        dos.writeInt(this.getTransfers());
        dos.writeFloat(this.getBalance());

        dos.close();
        baos.close();
        return baos.toByteArray();
    }
  
// ---------------------------------------------------------------------------------------------------------------- //
}