import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

// ----------------------------------------------------------------------------------------------------------------- //

public class Inverted {

    // ------------------------------------------------------------------------- //

    static class Pair<T1, T2> {

        public T1 first;
        public T2 second;

        Pair() {
        }

        Pair(T1 fi, T2 sc) {

            this.first = fi;
            this.second = sc;
        }

        public T1 getFirst() {
            return this.first;
        }

        public T2 getSecond() {
            return this.second;
        }
    }

    // ------------------------------------------------------------------------- //

    public static void main(String[] args) throws IOException {

        String outputCity = "cityIndex.bin";
        String outputName = "nameIndex.bin";

        HashMap<String, ArrayList<Long>> harr = new HashMap<>();
        HashMap<String, ArrayList<Long>> harr2 = new HashMap<>();

        try {

            //se possivel, dividir isso daqui em uma funcao @pedro!!!
            //parte das cidades
            for (int i = 0; i < Order.getTotalAccounts(); ++i) {

                Pair<BankAccount, Long> p = getBaIndexOf(i);
                ArrayList<Long> tmp = new ArrayList<>();

                if (p == null)
                    continue;

                tmp.add(p.second);

                if (harr.get(p.first.getCity()) == null)
                    harr.put(p.first.getCity(), tmp);
                else {

                    ArrayList<Long> k = harr.get(p.first.getCity());

                    k.add(p.second);
                    harr.put(p.first.getCity(), k);
                }
            }

            for (var x : harr.keySet()) {
                String key = x.toString();
                System.out.print(key + " ");
                System.out.print(harr.get(x).size() + " ");

                for (var y : harr.get(x))
                    System.out.print(y + " ");

                System.out.println();
            }

            //parte dos nomes
            for (int i = 0; i < Order.getTotalAccounts(); ++i) {

                Pair<BankAccount, Long> p = getBaIndexOf(i);
                ArrayList<Long> tmp = new ArrayList<>();

                if (p == null)
                    continue;

                tmp.add(p.second);

                if (harr2.get(p.first.getName()) == null)
                    harr2.put(p.first.getName(), tmp);
                else {

                    ArrayList<Long> k = harr2.get(p.first.getName());

                    k.add(p.second);
                    harr2.put(p.first.getName(), k);
                }
            }

            for (var x : harr2.keySet()) {
                String key = x.toString();
                System.out.print(key + " ");
                System.out.print(harr2.get(x).size() + " ");

                for (var y : harr2.get(x))
                    System.out.print(y + " ");

                System.out.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------- //

    public static Pair<BankAccount, Long> getBaIndexOf(int index) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(Main.DEFAULT_FILE, "rw");
        BankAccount ba = new BankAccount();

        raf.seek(4);

        int validos = 0;

        try {

            while (raf.getFilePointer() < raf.length()) {

                if (raf.readByte() == 0) {

                    validos++;

                    if (validos == index + 1) {

                        long k = raf.getFilePointer();

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
                        for (int i = 0; i < emailsCount; i++)
                            ba.addEmail(raf.readUTF());

                        raf.close();

                        Pair<BankAccount, Long> p = new Pair<>(ba, k);
                        return p;
                    } else
                        raf.skipBytes(raf.readInt());
                } else
                    raf.skipBytes(raf.readInt());
            }

            raf.close();
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}

// -----------------------------------------------------------------------------------------------------------------
// //