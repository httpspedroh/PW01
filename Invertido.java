import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;


public class Invertido {

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

    public static void main(String[] args) throws IOException {
        String source = "accounts.bin";
        String output = "cityIndex.bin";
        HashMap<String, ArrayList<Long>> hst = new HashMap<>();

        try {
            // O(n) pra percorrer o arquivo
            for (int i = 0; i < Order.getTotalAccounts(); ++i) {
                Pair<String, Long> p = getBaIndexOf(source, i); // readBankAccountCity(source, i);
                ArrayList<Long> tmp = new ArrayList<>();
                if (p == null) {
                    continue;
                }
                tmp.add(p.second);
                if (hst.get(p.first) == null) {
                    hst.put(p.first, tmp);
                } else {
                    ArrayList<Long> k = hst.get(p.first);
                    k.add(p.second);
                    hst.put(p.first, k);
                }
            }

            for (var x : hst.keySet()) {
                String key = x.toString();
                System.out.print(key + " ");
                System.out.print(hst.get(x).size() + " ");
                for (var y : hst.get(x)) {
                    System.out.print(y + " ");
                }
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* 
     for(var x : hst.keySet()){ 
                String key = x.toString();
                System.out.print(key + " ");
                System.out.print(hst.get(x).size() + " ");
                for(var y : hst.get(x)){
                    System.out.print(y + " ");
                }
                System.out.println();
            }
     * 
    */

    public static Pair<String, Long> getBaIndexOf(String source, int index) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(source, "rw");
        BankAccount ba = new BankAccount();

        if (source.equals("accounts.bin"))
            raf.seek(4);
        else
            raf.seek(0);

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
                        Pair<String, Long> p = new Pair<>(ba.getCity(), k);
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
