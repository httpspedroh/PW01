import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// ---------------------------------------------------------------------------------------------------------------- //

public class Order {
    
    public static boolean byId(int m_registros, int n_caminhos) throws Exception {

        return sort(m_registros, n_caminhos, new Comparator<BankAccount>() {
            @Override
            public int compare(BankAccount o1, BankAccount o2) {
                return o1.getId() - o2.getId();
            }
        });
    }

    public static boolean byName(int m_registros, int n_caminhos) throws Exception {

        return sort(m_registros, n_caminhos, new Comparator<BankAccount>() {
            @Override
            public int compare(BankAccount o1, BankAccount o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public static boolean byCpf(int m_registros, int n_caminhos) throws Exception {

        return sort(m_registros, n_caminhos, new Comparator<BankAccount>() {
            @Override
            public int compare(BankAccount o1, BankAccount o2) {
                return o1.getCpf().compareTo(o2.getCpf());
            }
        });
    }

    public static boolean byCity(int m_registros, int n_caminhos) throws Exception {

        return sort(m_registros, n_caminhos, new Comparator<BankAccount>() {
            @Override
            public int compare(BankAccount o1, BankAccount o2) {
                return o1.getCity().compareTo(o2.getCity());
            }
        });
    }

    public static boolean byUser(int m_registros, int n_caminhos) throws Exception {

        return sort(m_registros, n_caminhos, new Comparator<BankAccount>() {
            @Override
            public int compare(BankAccount o1, BankAccount o2) {
                return o1.getUser().compareTo(o2.getUser());
            }
        });
    }

    public static boolean byBalance(int m_registros, int n_caminhos) throws Exception {

        return sort(m_registros, n_caminhos, new Comparator<BankAccount>() {
            @Override
            public int compare(BankAccount o1, BankAccount o2) {
                return Float.compare(o1.getBalance(), o2.getBalance());
            }
        });
    }

    public static boolean sort(int m_registros, int n_caminhos, Comparator<BankAccount> comp_model) throws Exception {

        RandomAccessFile[] d_rafs = new RandomAccessFile[n_caminhos];

        for(int i = 0; i < n_caminhos; i++) d_rafs[i] = new RandomAccessFile("d_" + i + ".bin", "rw");
            
        // Distribuição
        try {

            int i = 0;
            int index = 0;
            boolean stop = false;

            while(stop == false) {

                if(stop) break;

                ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();

                for(int x = 0; x < m_registros; i++, x++) {

                    BankAccount ba = getBaIndexOf(Main.DEFAULT_FILE, i);

                    if(ba != null) accounts.add(ba);
                    else {

                        stop = true;
                        break;
                    }
                }

                Collections.sort(accounts, comp_model);

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
        catch(Exception e) { return false; }

        // ---------------------------------------------------------------------------------- //

        // Intercalação
        try {

            RandomAccessFile[] i_rafs = new RandomAccessFile[n_caminhos];

            // ------------------------------------------------------------------ //

            int intercalation = 0;

            while(true) { // While das intercalações

                for(int i = 0; i < n_caminhos; i++) i_rafs[i] = new RandomAccessFile("i" + intercalation + "_" + i + ".bin", "rw");

                // ---------------------------------------------------------------------------- //
        
                int index_cam = 0;
                int index_col = 0;
                int regs;
                boolean stop = false;

                while(true) { // While das colunas

                    if(stop) break;
                    
                    ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();

                    if(intercalation == 0) regs = m_registros;
                    else regs = m_registros * (int)Math.pow(2, intercalation);

                    int start = (index_col == 0 ? 0 : (index_col * regs));
                    int end = regs + start;

                    for(int x = 0; x < n_caminhos; x++) {

                        if(stop) break;

                        for(int y = start; y < end; y++) {

                            BankAccount ba;

                            if(intercalation == 0) ba = getBaIndexOf("d_" + x + ".bin", y);
                            else ba = getBaIndexOf("i" + (intercalation - 1) + "_" + x + ".bin", y);

                            if(ba == null) {

                                stop = true;
                                break;
                            }

                            accounts.add(ba);
                        }
                    }

                    Collections.sort(accounts, comp_model);

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
                    index_col++;

                    if(index_cam == n_caminhos) index_cam = 0;
                }

                // ---------------------------------------------------------------------------- //

                if(i_rafs[1].length() == 0) {
                    
                    RandomAccessFile randFrom = new RandomAccessFile("i" + (intercalation) + "_0" + ".bin", "r");
                    RandomAccessFile randTo = new RandomAccessFile(Main.DEFAULT_FILE, "rw");
                    
                    byte[] arr = new byte[(int)randFrom.length()];

                    randFrom.readFully(arr);
                    randTo.setLength(0);
                    randTo.writeInt(Main.globalId);
                    randTo.write(arr);

                    randFrom.close();
                    randTo.close();

                    // --------------------------------------------- //

                    for(int i = 0; i < n_caminhos; i++) {

                        i_rafs[i].close();

                        new File("d" + "_" + i + ".bin").delete();
                        
                        for(int j = 0; j < intercalation + 1; j++) new File("i" + j + "_" + i + ".bin").delete();
                    }

                    // --------------------------------------------- //

                    Inverted.create();

                    break;
                }

                intercalation++;
            }
        }
        catch(Exception e) { return false; }
        return true;
    }

    // ------------------------------------------------------------------------------------------------------ // 

    public static ArrayList<BankAccount> readAll(String source) {

        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");
            ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();

            if(source.equals("accounts.bin")) raf.seek(4);
            else raf.seek(0);

            while(raf.getFilePointer() < raf.length() - 1) {

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
        catch(Exception e) { e.printStackTrace(); }
        return null;
    }

    // ------------------------------------------------------------------------------------------------------ // 
    
    public static BankAccount getBaIndexOf(String source, int index) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(source, "rw");
        BankAccount ba = new BankAccount();

        if(source.equals("accounts.bin")) raf.seek(4);
        else raf.seek(0);

        int validos = 0;
            
        try {

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {
    
                    validos++;
    
                    if(validos == index + 1) {
    
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
                else raf.skipBytes(raf.readInt());
            }
            
            raf.close();
        }
        catch(Exception e) { return null; }
        return null;
    }

    // ------------------------------------------------------------------------------------------------------ // 

    public static int getTotalAccounts() {

        try {

            RandomAccessFile raf = new RandomAccessFile(Main.DEFAULT_FILE, "rw");
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

    // ------------------------------------------------------------------------------------------------------ // 
}
