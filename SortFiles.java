import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SortFiles{//extends BankAccount {
   public static final long maxSize = 10; // numero maximo de registros que o pc aguenta por sort

    class MinHeapNode {
        public int element; // elemento a ser guardado
        public int i;// index do array de onde o elemento eh pego
    }

    class MinHeap {
        public MinHeapNode[] harr; // heap array
        public int heapSize;

        public MinHeap(MinHeapNode[] a, int size) {
            heapSize = size;
            harr[0] = a[0];
            int i = (heapSize - 1) / 2;
            while (i >= 0) {
                MinHeapify(i);
                i--;
            }
        }

        public int left(int i) {
            return (2 * i + 1);
        }

        public int right(int i) {
            return (2 * i + 2);
        }

        public MinHeapNode getMin() {
            return harr[0];
        }

        public void replaceMin(MinHeapNode x) {
            harr[0] = x;
            MinHeapify(0);
        }

        public void MinHeapify(int i) {
            int l = left(i);
            int r = right(i);
            int smallest = i;

            if (l < heapSize && harr[l].element < harr[i].element)
                smallest = l;

            if (r < heapSize && harr[r].element < harr[smallest].element)
                smallest = r;
            if (smallest != i) {
                MinHeapNode tmp = harr[i];
                harr[i] = harr[smallest];
                harr[smallest] = tmp;
                MinHeapify(smallest);
            }
        }
    }

    public static void mergeFiles(String outputFile, int n, int k) throws IOException {
        File[] x = new File[k];
        //libera todos os arquivos pra leitura
        for(int i = 0; i < k; ++i) x[i].setReadable(true); 

        File out = new File(outputFile);
        out.setWritable(true); 
        FileOutputStream f = new FileOutputStream(out);
        FileInputStream g = new FileInputStream(out);
        MinHeapNode[] harr = new MinHeapNode[k];
        int i;
        for(i = 0; i < k; ++i){
            if(g.available() == -1) break;
            harr[i].i = i;
        }
    }

    public static void solve(String source) {
        ArrayList<BankAccount> bar = new ArrayList<>();

        //enquanto i < que a capacidade maxima do pc, pegar blocos do arquivo
        for(int i = 1; i < maxSize; ++i) bar.add(Crud.searchById(source, i)); 
        Collections.sort(bar, new Comparator<BankAccount>() {
            public int compare(BankAccount e1, BankAccount e2){
                return e1.getCpf().compareTo(e2.getCpf());
            } 
        });
        for(var x : bar) System.out.println(x.getCpf());
        // Arrays.sort(bar);
    }
    public static void main(String[] args) {
        solve("accounts.bin");
    }
}
