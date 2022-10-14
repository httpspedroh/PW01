import java.io.RandomAccessFile;

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
}