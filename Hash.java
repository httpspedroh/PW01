import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Hash {

    public static ArrayList<ArrayList<Pair<Integer, Long>>> buckets = new ArrayList<>();
    public static int profundidade = 1;
    
    // ------------------------------------------------------------------------- //

    public static class Pair<T1, T2> {

        public T1 first;
        public T2 second;

        Pair(T1 fi, T2 sc) {

            first = fi;
            second = sc;
        }
    }

    // ------------------------------------------------------------------------- //

    /**
     * @param x_reg
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean create(int x_reg) throws FileNotFoundException, IOException {

        try {

            RandomAccessFile raf = new RandomAccessFile(Main.DEFAULT_FILE, "rw");

            raf.seek(4);

            while(raf.getFilePointer() < raf.length()) {

                if(raf.readByte() == 0) {

                    long fp = raf.getFilePointer();
                    int size = raf.readInt();
                    int id = raf.readInt();

                    raf.skipBytes(size - 4);
                    
                    Pair<Integer, Long> pair = new Pair<>(id, fp);

                    int bucket_dest = id % (int)Math.pow(2, profundidade);
                    int last_bucket = buckets.size() - 1;

                    for(int i = last_bucket + 1; i <= bucket_dest; i++) {

                        buckets.add(new ArrayList<>());
                    }

                    buckets.get(bucket_dest).add(pair);

                    if(buckets.get(bucket_dest).size() > x_reg) {

                        boolean stop = false;

                        while(!stop) {

                            if(stop) break;

                            for(int i = 0; i < buckets.size(); i++) {

                                if(buckets.get(i).size() > x_reg) {

                                    profundidade++;

                                    ArrayList<Pair<Integer, Long>> tmp = new ArrayList<>(buckets.get(i));

                                    buckets.get(i).clear();

                                    for(Pair<Integer, Long> p : tmp) {

                                        bucket_dest = p.first % (int)Math.pow(2, profundidade);
                                        last_bucket = buckets.size() - 1;

                                        if(bucket_dest > last_bucket) {

                                            for(int x = 0; x < bucket_dest - last_bucket; x++) buckets.add(new ArrayList<>());
                                        }

                                        buckets.get(bucket_dest).add(p);
                                    }
                                    break;
                                }

                                if(i == buckets.size() - 1) stop = true;
                            }
                        }
                    }
                } 
                else raf.skipBytes(raf.readInt());
            }

            raf.close();

            // --------------------------------------------------- //

            try {

                RandomAccessFile raf_hash = new RandomAccessFile(Main.DEFAULT_FILE_HASH, "rw");

                raf_hash.seek(0);

                for(int i = 0; i < buckets.size(); i++) {

                    if(buckets.get(i).size() > 0) {

                        System.out.println("Bucket " + i + ": " + buckets.get(i).size() + " registro(s)");
                        
                        raf_hash.writeInt(i);
                        raf_hash.writeInt(buckets.get(i).size());

                        for(Pair<Integer, Long> p : buckets.get(i)) {

                            raf_hash.writeInt(p.first);
                            raf_hash.writeLong(p.second);
                        }
                    }
                }

                raf_hash.close();
            }
            catch(FileNotFoundException e) { System.out.println("Arquivo não encontrado."); }
            catch(IOException e) { System.out.println("Erro de I/O."); }
        }
        catch(Exception e) { return false; }
        return true;
    }

    // ------------------------------------------------------------------------- //

    public static void main(String[] args) {

        try {

            create(4);

            System.out.println("Profundidade: " + profundidade + "\n");

            for(int i = 0; i < buckets.size(); i++) {

                if(buckets.get(i).size() > 0) {

                    System.out.println("Bucket " + i + ": " + buckets.get(i).size() + " registro(s)");
                    
                    for(Pair<Integer, Long> p : buckets.get(i)) {

                        System.out.println("ID: " + p.first + " | FP: " + p.second);
                    }

                    System.out.println();
                }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
    }
}