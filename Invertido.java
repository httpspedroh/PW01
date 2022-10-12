import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

        public T1 getFirst(){
            return this.first;
        }
        public T2 getSecond(){
            return this.second;
        }

    }

    public static void main(String[] args) throws IOException {
        String source = "accounts.bin";
        String output = "cityIndex.bin";
        HashMap<String, ArrayList<Long>> hst = new HashMap<>();
        try {
            RandomAccessFile raf = new RandomAccessFile(source, "rw");
            // RandomAccessFile out = new RandomAccessFile(output, "rw");
            // O(n) pra percorrer o arquivo
            for (int i = 0; i < Crud.getTotalAccounts(source); ++i) {
                Pair<String, Long> p = readBankAccountCity(raf, i);
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
                // bh <--> 120 130
                // hst.get(p.first).add(p.second);//.forEach(x -> st.add(x));// o(n) amortizado
                // no pior caso
            }
            /* 
            for(var x : hst.values()){ 
                System.out.println(x);
                for(var y : x){
                    System.out.println(y);
                }
            }*/
            
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
         * bh 100 200 sp 123 !120 130 10
         * 
         * sp 123 940 820 123
         * 
         */

    }

    /*
     * @param raf, raf do arquivo
     * 
     * @param pointer, ponteiro pro usuario
     * 
     * le o usuario de numero @pointer de um arquivo @raf
     */
    public static Pair<String, Long> readBankAccountCity(RandomAccessFile raf, int pointer) throws IOException {

        BankAccount ba = new BankAccount();
        raf.seek(4);
        while (raf.getFilePointer() < raf.length()) {
            for (int x = 0; x < pointer; x++) {

                raf.readByte();
                raf.skipBytes(raf.readInt());
            }

            try {
                long k = raf.getFilePointer();
                raf.readInt();
                raf.readByte();
                raf.readInt();

                ba.setId(raf.readInt());
                ba.setName(raf.readUTF());
                ba.setUser(raf.readUTF());
                ba.setPass(raf.readUTF());
                ba.setCpf(raf.readUTF());
                ba.setCity(raf.readUTF());
                ba.setBalance(raf.readFloat());
                ba.setTransfers(raf.readInt());

                for (int i = 0; i < raf.readInt(); i++)
                    ba.addEmail(raf.readUTF());

                raf.close();
                Pair<String, Long> p = new Pair<String, Long>();
                p.first = ba.getCity();
                p.second = k;
                return p;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
