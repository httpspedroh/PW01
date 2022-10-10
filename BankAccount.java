import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

// ---------------------------------------------------------------------------------------------------------------- //

class BankAccount {

    private int id, transfers;
    private String name, user, pass, cpf, city;
    private float balance;
    public ArrayList<String> emails;
    
    // --------------------------------------------------- // 
    
    public BankAccount() {

        this.id = -1;
        this.name = this.user = this.pass = this.cpf = this.city = null;
        this.balance = -1;
        this.transfers = 0;
        this.emails = new ArrayList<String>();
    }

    // --------------------------------------------------- // 
    
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

    // --------------------------------------------------- // 
    
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUser(String user) { this.user = user; }
    public void setPass(String pass) { this.pass = pass; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCity(String city) { this.city = city; }
    public void setBalance(float balance) { this.balance = balance; }
    public void setTransfers(int transfers) { this.transfers = transfers; }
    public void addEmail(String email) { this.emails.add(email); }

    // --------------------------------------------------- // 
    
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

    public void print() {

        System.out.println("ID: " + this.getId());
        System.out.println("Name: " + this.getName());
        System.out.println("User: " + this.getUser());
        System.out.println("Pass: " + this.getPass());
        System.out.println("CPF: " + this.getCpf());
        System.out.println("City: " + this.getCity());
        System.out.println("Balance: " + this.getBalance());
        System.out.println("Transfers: " + this.getTransfers());
        System.out.println("Emails: " + this.getEmailsCount());

        for (String email : this.getEmails()) System.out.println("Email: " + email);
    }
}