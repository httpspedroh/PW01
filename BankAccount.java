import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

// ---------------------------------------------------------------------------------------------------------------- //

class BankAccount {

    static int globalId;

    private int id, transfers;
    private String name, user, pass, cpf, city;
    private ArrayList<String> emails;
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

    public static boolean create(BankAccount ba) {

        try {

            RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");

            raf.seek(raf.length());
            raf.writeByte(0);
            raf.writeInt(ba.toByteArray().length);
            raf.writeInt(ba.getId());
            raf.writeUTF(ba.getName());
            raf.writeUTF(ba.getUser());
            raf.writeUTF(ba.getPass());
            raf.writeUTF(ba.getCpf());
            raf.writeUTF(ba.getCity());
            raf.writeFloat(ba.getBalance());
            raf.writeInt(ba.getTransfers());
            raf.writeInt(ba.getEmailsCount());

            for (String email : ba.getEmails()) raf.writeUTF(email);

            raf.seek(0);
            raf.writeInt(globalId);
            raf.close();
            return true;
        }
        catch(Exception e) { return false; }
    }

    public static boolean update(BankAccount ba) {

        try {

            RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");

            raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {

                    int size = raf.readInt();
                    int id = raf.readInt();

                    if(ba.getId() == id) {

                        if(size >= ba.toByteArray().length) {
                            
                            raf.writeUTF(ba.getName());
                            raf.writeUTF(ba.getUser());
                            raf.writeUTF(ba.getPass());
                            raf.writeUTF(ba.getCpf());
                            raf.writeUTF(ba.getCity());
                            raf.writeFloat(ba.getBalance());
                            raf.writeInt(ba.getTransfers());
                            raf.writeInt(ba.getEmailsCount());

                            for (String email : ba.getEmails()) raf.writeUTF(email);

                            raf.close();
                            return true;
                        }
                        else {

                            raf.seek(raf.getFilePointer() - 9);
                            raf.writeByte(1);
                            raf.close();
                            return create(ba);
                        }
                    }
                    else raf.skipBytes(size - 4);
                }
                else raf.skipBytes(raf.readInt());
            }

            raf.close();
            return true;
        }
        catch(Exception e) { return false; }
    }

    public static BankAccount delete(BankAccount ba) {  
            
        try {

            RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");

            raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {

                    int size = raf.readInt();
                    int id = raf.readInt();

                    if(ba.getId() == id) {

                        raf.seek(raf.getFilePointer() - 9);
                        raf.writeByte(1);
                        raf.close();
                        return ba;
                    }
                    else raf.skipBytes(size - 4);
                }
                else raf.skipBytes(raf.readInt());
            }

            raf.close();
            return null;
        }
        catch(Exception e) { return null; }
    }

    public static BankAccount searchById(int id) {

        try {

            RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");
            BankAccount ba = new BankAccount();

            raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {

                    int size = raf.readInt();

                    ba.setId(raf.readInt());

                    if(ba.getId() == id) {

                        ba.setName(raf.readUTF());
                        ba.setUser(raf.readUTF());
                        ba.setPass(raf.readUTF());
                        ba.setCpf(raf.readUTF());
                        ba.setCity(raf.readUTF());
                        ba.setBalance(raf.readFloat());
                        ba.setTransfers(raf.readInt());

                        int emailsCount = raf.readInt();
                        for (int i = 0; i < emailsCount; i++) ba.addEmail(raf.readUTF());

                        raf.close();
                        return ba;
                    }
                    else raf.skipBytes(size - 4);
                }
                else raf.skipBytes(raf.readInt());
            }

            raf.close();
            return null;
        }
        catch(Exception e) { return null; }
    }

    public static BankAccount searchByUser(String user) {

        try {

            RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");
            BankAccount ba = new BankAccount();

            raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {

                    int size = raf.readInt();

                    ba.setId(raf.readInt());
                    ba.setName(raf.readUTF());
                    ba.setUser(raf.readUTF());

                    if(ba.getUser().equals(user)) {

                        ba.setPass(raf.readUTF());
                        ba.setCpf(raf.readUTF());
                        ba.setCity(raf.readUTF());
                        ba.setBalance(raf.readFloat());
                        ba.setTransfers(raf.readInt());

                        int emailsCount = raf.readInt();
                        for (int i = 0; i < emailsCount; i++) ba.addEmail(raf.readUTF());

                        raf.close();
                        return ba;
                    }
                    else raf.skipBytes(size - 8 - ba.getName().length() - ba.getUser().length());
                }
                else raf.skipBytes(raf.readInt());
            }

            raf.close();
            return null;
        }
        catch(Exception e) { return null; }
    }

    // ---------------------------------------------------------------------------------------------------------------- //

    public static void main(String[] args) throws Exception {
        
        RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");

        if(raf.length() == 0) raf.writeInt(0);

        raf.seek(0);
        globalId = raf.readInt();

        // ----------------------------------------------------------- //

        Scanner scr = new Scanner(System.in);

        int option = -1;

        // ----------------------------------------------------------- //

        do {

            System.out.println("============= Menu =============");
            System.out.println("0 - SAIR");
            System.out.println("1 - CRIAR CONTA");
            System.out.println("2 - REALIZAR TRANSFERENCIA");
            System.out.println("3 - LER REGISTRO");
            System.out.println("4 - ATUALIZAR REGISTRO");
            System.out.println("5 - DELETAR REGISTRO");
            System.out.println("6 - ORDENAR REGISTROS");
            System.out.println("================================\n");

            // ----------------------------------------------------------- //
            
            // Option verification
            do {

                try { 
                    
                    option = scr.nextInt(); 

                    if(option < 0 || option > 6) System.out.println("x Opcao invalida!");
                }
                catch (Exception e) { 
                    
                    System.out.println("x Digite apenas numeros!"); 

                    scr.next();
                    break;
                }
            }
            while(option < 0 || option > 6);

            // Option execution
            switch(option) {

                case 0: System.out.println("Saindo..."); break;

                // ----------------------------------------------------------- //

                case 1: {

                    BankAccount ba = new BankAccount();

                    System.out.println("\n========== CRIAR CONTA ==========");
                    
                    System.out.print("> Digite o nome: ");
                    ba.setName(scr.next());

                    System.out.print("> Digite o CPF: ");
                    ba.setCpf(scr.next());

                    System.out.print("> Digite a cidade: ");
                    ba.setCity(scr.next());

                    System.out.print("> Digite o usuario: ");
                    ba.setUser(scr.next());

                    System.out.print("> Digite a senha: ");
                    ba.setPass(scr.next());

                    System.out.print("> Digite seu email: ");
                    ba.addEmail(scr.next());

                    boolean wishAdd = true;

                    do {

                        System.out.print("> Deseja adicionar mais emails? (s/n): ");
                        String answer = scr.next();

                        if(answer.equals("s")) {

                            System.out.print("> Digite seu email: ");
                            ba.addEmail(scr.next());
                        }
                        else wishAdd = false;
                    }
                    while(wishAdd == true);

                    System.out.print("> Digite o saldo: ");
                    ba.setBalance(scr.nextFloat());

                    ba.setId(++globalId);

                    System.out.println("\n>>> Conta criada com sucesso!");
                    System.out.println("==================================\n");

                    create(ba);
                    break;
                }

                // ----------------------------------------------------------- //
                
                case 2: {
                    
                    System.out.println("\n========== REALIZAR TRANSFERENCIA ==========");

                    System.out.print("> Digite o usuário da conta de origem: ");
                    String origin = scr.next();

                    System.out.print("> Digite o usuário da conta de destino: ");
                    String destiny = scr.next();

                    System.out.print("> Digite o valor da transferencia: ");
                    float value = scr.nextFloat();

                    BankAccount ba_origin = searchByUser(origin);
                    BankAccount ba_destiny = searchByUser(destiny);
                    
                    if(ba_origin == null) System.out.println("x Conta de origem nao encontrada!");
                    else if(ba_destiny == null) System.out.println("x Conta de destino nao encontrada!");
                    else if(ba_origin.getBalance() < value) System.out.println("x Saldo insuficiente!");
                    else {

                        ba_origin.setBalance(ba_origin.getBalance() - value);
                        ba_destiny.setBalance(ba_destiny.getBalance() + value);

                        ba_origin.setTransfers(ba_origin.getTransfers() + 1);
                        ba_destiny.setTransfers(ba_destiny.getTransfers() + 1);

                        update(ba_origin);
                        update(ba_destiny);

                        System.out.println("\n>>> Transferencia realizada com sucesso!");
                    }
                    break;
                }

                // ----------------------------------------------------------- //
                
                case 3: {

                    System.out.println("\n========== LER REGISTRO ==========");

                    System.out.print("> Digite o ID da conta desejada: ");
                    int id = scr.nextInt();

                    BankAccount ba = searchById(id);

                    if(ba == null) System.out.println("x Conta nao encontrada!");
                    else {

                        System.out.println("\n>>> Conta encontrada!");
                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");

                    }
                    break;
                }

                // ----------------------------------------------------------- //
                
                case 4: {
                    
                    System.out.println("\n========== ATUALIZAR REGISTRO ==========");

                    System.out.print("> Digite o usuário da conta desejada: ");
                    String user = scr.next();

                    BankAccount ba = searchByUser(user);

                    if(ba == null) System.out.println("x Conta nao encontrada!");
                    else {

                        System.out.println("\n>>> Conta encontrada!");
                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");

                        System.out.println("========== ATUALIZAR ==========");
                        System.out.println("0 - VOLTAR");
                        System.out.println("1 - NOME");
                        System.out.println("2 - CPF");
                        System.out.println("3 - CIDADE");
                        System.out.println("4 - USUARIO");
                        System.out.println("5 - SENHA");
                        System.out.println("6 - EMAIL");
                        System.out.println("7 - SALDO");
                        System.out.println("================================\n");

                        int updateOption = 0;

                        do {

                            try { 
                                
                                updateOption = scr.nextInt(); 

                                if(updateOption < 0 || updateOption > 7) System.out.println("x Opcao invalida!");
                            }
                            catch (Exception e) { 
                                
                                System.out.println("x Digite apenas numeros!"); 

                                scr.next();
                            }
                        }
                        while(updateOption < 0 || updateOption > 7);

                        switch(updateOption) {

                            case 0: break;

                            case 1: {

                                System.out.print("> Digite o novo nome: ");
                                ba.setName(scr.next());
                                break;
                            }

                            case 2: {

                                System.out.print("> Digite o novo CPF: ");
                                ba.setCpf(scr.next());
                                break;
                            }

                            case 3: {

                                System.out.print("> Digite a nova cidade: ");
                                ba.setCity(scr.next());
                                break;
                            }

                            case 4: {

                                System.out.print("> Digite o novo usuario: ");
                                ba.setUser(scr.next());
                            }

                            case 5: {

                                System.out.print("> Digite a nova senha: ");
                                ba.setPass(scr.next());
                                break;
                            }

                            case 6: {

                                System.out.print("> Digite o novo email: ");

                                ba.emails.clear();
                                ba.addEmail(scr.next());

                                System.out.print("> Deseja adicionar mais um email? (s/n): ");
                                String answer = scr.next();

                                while(answer.equals("s")) {

                                    System.out.print("> Digite o novo email: ");
                                    ba.addEmail(scr.next());

                                    System.out.print("> Deseja adicionar mais um email? (s/n): ");
                                    answer = scr.next();
                                }
                                break;
                            }
                        }

                        if(updateOption != 0) {

                            update(ba);

                            System.out.println("\n>>> Conta atualizada com sucesso!");
                            break;
                        }
                    }
                    break;
                }
                
                // ----------------------------------------------------------- //
                
                case 5: {
                    
                    System.out.println("\n========== DELETAR REGISTRO ==========");

                    System.out.print("> Digite o ID da conta desejada: ");
                    int id = scr.nextInt();

                    BankAccount ba = searchById(id);

                    if(ba == null) System.out.println("x Conta nao encontrada!");
                    else {

                        System.out.println("\n>>> Conta encontrada!");
                        System.out.println("ID: " + ba.getId());
                        System.out.println("Nome: " + ba.getName());
                        System.out.println("CPF: " + ba.getCpf());
                        System.out.println("Cidade: " + ba.getCity());
                        System.out.println("Usuario: " + ba.getUser());
                        System.out.println("Senha: " + ba.getPass());
                        System.out.println("Saldo: " + ba.getBalance());
                        System.out.println("Emails: " + ba.getEmails());
                        System.out.println("Transferencias: " + ba.getTransfers());
                        System.out.println("==================================\n");

                        System.out.print("> Deseja realmente deletar esta conta? (s/n): ");
                        String answer = scr.next();

                        if(answer.equals("s")) {

                            delete(ba);

                            System.out.println("\n>>> Conta ID " + ba.getId() + " deletada com sucesso!");
                        }
                    }
                    break;
                }

                // ----------------------------------------------------------- //
                
                case 6: {
                    
                    System.out.println("\n========== ORDENAR ==========");

                    System.out.println("0 - VOLTAR");
                    System.out.println("1 - ID");
                    System.out.println("2 - NOME");
                    System.out.println("3 - CPF");
                    System.out.println("4 - CIDADE");
                    System.out.println("5 - USUARIO");
                    System.out.println("6 - SALDO");

                    // TODO: Ordenar
                    break;
                }
            }
        }
        while(option != 0);

        // ----------------------------------------------------------- //
        
        scr.close();
        raf.close();
    }
}