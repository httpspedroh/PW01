import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

// ---------------------------------------------------------------------------------------------------------------- //

public class Crud {

    // --------------------------------------------------- // 
    
    public static boolean create(String source, BankAccount ba) {

        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            raf.seek(raf.length());
            raf.writeByte(0);
            raf.writeInt(ba.toByteArray().length);
            raf.writeInt(++Main.globalId);
            raf.writeUTF(ba.getName());
            raf.writeUTF(ba.getUser());
            raf.writeUTF(ba.getPass());
            raf.writeUTF(ba.getCpf());
            raf.writeUTF(ba.getCity());
            raf.writeFloat(ba.getBalance());
            raf.writeInt(ba.getTransfers());
            raf.writeInt(ba.getEmailsCount());

            for(String email : ba.getEmails()) raf.writeUTF(email);

            raf.seek(0);
            raf.writeInt(Main.globalId);
            raf.close();
            return true;
        }
        catch(Exception e) { return false; }
    }

    // --------------------------------------------------- // 

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

                            return create("accounts.bin", ba);
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

    // --------------------------------------------------- // 
    
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

    // --------------------------------------------------- // 
    
    public static BankAccount searchById(String source, int id) {

        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");
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

    // --------------------------------------------------- // 
    
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

    // --------------------------------------------------- // 
    
    public static void sortById(String source, int m_registros, int n_caminhos) throws Exception {

        RandomAccessFile[] d_rafs = new RandomAccessFile[n_caminhos];

        for(int i = 0; i < n_caminhos; i++) d_rafs[i] = new RandomAccessFile("d_" + i + ".bin", "rw");
            
        // Distribuição
        try {

            int i = 0, n = getTotalAccounts();
            int index = 0;

            while(i < n) {

                ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();

                for(int x = 0; x < m_registros; i++, x++) {

                    if(i == getTotalAccounts()) break;

                    accounts.add(getBaIndexOf("accounts.bin", i));
                }

                Collections.sort(accounts);

                for(BankAccount ba : accounts) {

                    d_rafs[index].seek(d_rafs[index].length());
                    d_rafs[index].writeByte(0);
                    d_rafs[index].writeInt(ba.toByteArray().length);
                    d_rafs[index].writeInt(ba.getId());
                    d_rafs[index].writeUTF(ba.getName());
                    d_rafs[index].writeUTF(ba.getUser());
                    d_rafs[index].writeUTF(ba.getPass());
                    d_rafs[index].writeUTF(ba.getCpf());
                    d_rafs[index].writeUTF(ba.getCity());
                    d_rafs[index].writeFloat(ba.getBalance());
                    d_rafs[index].writeInt(ba.getTransfers());
                    d_rafs[index].writeInt(ba.getEmailsCount());

                    for(String email : ba.getEmails()) d_rafs[index].writeUTF(email);
                }

                index++;

                if(index == n_caminhos) index = 0;

                accounts.clear();
            }

            for(int x = 0; x < n_caminhos; x++) d_rafs[x].close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // ---------------------------------------------------------------------------------- //

        // Intercalação
        try {

            RandomAccessFile[] i_rafs = new RandomAccessFile[n_caminhos];

            // ------------------------------------------------------------------ //

            int intercalacao = 0;

            while(true) {

                for(int i = 0; i < n_caminhos; i++) {
                    
                    i_rafs[i] = new RandomAccessFile("i" + intercalacao + "_" + i + ".bin", "rw");

                    System.out.println("i" + intercalacao + "_" + i + ".bin");
                }

                // ---------------------------------------------------------------------------- //
        
                int index_cam = 0;
                int index_reg = 1;
                boolean stop = false;

                while(true) {

                    if(stop) break;
                    
                    ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();

                    for(int x = 0; x < n_caminhos; x++) {

                        if(stop) break;

                        for(int y = (index_reg == 1 ? 0 : index_reg); y < m_registros * index_reg; y++) {

                            BankAccount ba;

                            if(intercalacao == 0) ba = getBaIndexOf("d_" + x + ".bin", y);
                            else ba = getBaIndexOf("i" + (intercalacao - 1) + "_" + x + ".bin", y);

                            if(ba == null) {

                                stop = true;
                                break;
                            }

                            accounts.add(ba);
                        }
                    }

                    Collections.sort(accounts);

                    for(BankAccount ba : accounts) {

                        i_rafs[index_cam].seek(i_rafs[index_cam].length());
                        i_rafs[index_cam].writeByte(0);
                        i_rafs[index_cam].writeInt(ba.toByteArray().length);
                        i_rafs[index_cam].writeInt(ba.getId());
                        i_rafs[index_cam].writeUTF(ba.getName());
                        i_rafs[index_cam].writeUTF(ba.getUser());
                        i_rafs[index_cam].writeUTF(ba.getPass());
                        i_rafs[index_cam].writeUTF(ba.getCpf());
                        i_rafs[index_cam].writeUTF(ba.getCity());
                        i_rafs[index_cam].writeFloat(ba.getBalance());
                        i_rafs[index_cam].writeInt(ba.getTransfers());
                        i_rafs[index_cam].writeInt(ba.getEmailsCount());

                        for(String email : ba.getEmails()) i_rafs[index_cam].writeUTF(email);
                    }

                    accounts.clear();

                    index_cam++;
                    index_reg *= 2;

                    if(index_cam == n_caminhos) index_cam = 0;
                }

                // ---------------------------------------------------------------------------- //

                if(i_rafs[1].length() == 0) break;
        
                intercalacao++;

                for(int i = 0; i < n_caminhos; i++) i_rafs[i].close();

                // if(intercalacao == 1) {

                //     for(int i = 0; i < n_caminhos; i++) {

                //         i_rafs[i].close();

                //         File file = new File("d_" + i + ".bin");
                //         file.delete();
                //     }                    
                // }
                // else {

                //     for(int i = 0; i < n_caminhos; i++) {

                //         i_rafs[i].close();

                //         File file = new File("i" + (intercalacao - 2) + "_" + i + ".bin");
                //         file.delete();
                //     }
                // }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    // --------------------------------------------------- // 
    
    public static ArrayList<BankAccount> readAll(String source) {

        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");
            ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();

            if(source.equals("accounts.bin")) raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {

                    raf.readInt();

                    BankAccount ba = new BankAccount();

                    ba.setId(raf.readInt());
                    ba.setName(raf.readUTF());
                    ba.setUser(raf.readUTF());
                    ba.setPass(raf.readUTF());
                    ba.setCpf(raf.readUTF());
                    ba.setCity(raf.readUTF());
                    ba.setBalance(raf.readFloat());
                    ba.setTransfers(raf.readInt());

                    int emailsCount = raf.readInt();
                    for(int i = 0; i < emailsCount; i++) ba.addEmail(raf.readUTF());
                    
                    accounts.add(ba);
                }
                else raf.skipBytes(raf.readInt());
            }

            raf.close();
            return accounts;
        }
        catch(Exception e) { return null; }
    }

    // --------------------------------------------------- // 
    
    public static BankAccount getBaIndexOf(String source, int index) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(source, "rw");
        BankAccount ba = new BankAccount();

        if(source.equals("accounts.bin")) raf.seek(4);

        while(raf.getFilePointer() < raf.length()) {

            for(int x = 0; x < index; x++) {

                raf.readByte();
                raf.skipBytes(raf.readInt());
            }

            try {
                
                while(true) {

                    if(raf.readByte() == 0) {

                        raf.readInt();

                        ba.setId(raf.readInt());
                        ba.setName(raf.readUTF());
                        ba.setUser(raf.readUTF());
                        ba.setPass(raf.readUTF());
                        ba.setCpf(raf.readUTF());
                        ba.setCity(raf.readUTF());
                        ba.setBalance(raf.readFloat());
                        ba.setTransfers(raf.readInt());

                        int emailsCount = raf.readInt();
                        for(int i = 0; i < emailsCount; i++) ba.addEmail(raf.readUTF());

                        raf.close();

                        return ba;
                    }
                    else raf.skipBytes(raf.readInt());
                }
            }
            catch (Exception e) { return null; }
        }
        
        raf.close();
        return null;
    }

    // --------------------------------------------------- // 

    public static int getTotalAccounts() {

        try {

            RandomAccessFile raf = new RandomAccessFile("accounts.bin", "rw");
            int total = 0;

            raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                raf.skipBytes(1);
                raf.skipBytes(raf.readInt());

                total++;
            }

            raf.close();
            return total;
        }
        catch(Exception e) { return 0; }
    }

    // --------------------------------------------------- // 
}