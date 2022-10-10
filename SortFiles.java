import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortFiles {

    public static final int maxSize = 10; // numero maximo de registros que o pc aguenta por sort

    public static void insertionSort(ArrayList<BankAccount> a) {
        final int n = a.size();
        for (int i = 1; i < n; ++i) {
            BankAccount tmp = a.get(i);
            int j = i - 1;
            while ((j >= 0) && (a.get(j).getId() > tmp.getId())) {
                a.set(j + 1, a.get(j));
                j--;
            }
            a.set(j + 1, tmp);
        }
    }

    public static String changeIndex(int k) {
        return "f" + k;
    }

    /*
     * @param source, arquivo fonte, da onde serao tirados os registros e passados
     * pra outros arquivos
     * 
     * @param f_index, file index, proximo id a ser buscado no arquivo
     * 
     * @param int z, index do novo arquivo (no modelo "f" + z)(f1,f2,f3...);
     */
    public static void solve(String source, int f_index, int z) {
        if (f_index <= Crud.globalId+1) {
            System.out.println("DBG" + z);
            ArrayList<BankAccount> bar = new ArrayList<>();
            // enquanto i < que a capacidade maxima do pc, pegar blocos do arquivo
            for (int i = f_index; i < f_index + maxSize; ++i)
                if(Crud.searchById(source, i) != null)
                    bar.add(Crud.searchById(source, i));

            // insertionSort(bar);//como na maior parte dos casos o array vai estar quase
            // ordenado, fica O(n) amortizado

            bar.forEach(i -> Crud.create(changeIndex(z), i));// adiciona o bloco todo em um arquivo no modelo "f"+index

            solve(source, f_index + maxSize, z + 1);

            for (var x : bar)
                System.out.println(x.getId());
            // Arrays.sort(bar);
        }
    }

    public static void main(String[] args) {
        System.out.println(Crud.globalId);
        for (int i = 0; i < 10; ++i) {
            System.out.println(changeIndex(i));
        }
        solve("accounts.bin", 1, 1);
    }
}
