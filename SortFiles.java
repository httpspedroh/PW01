import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortFiles {

    public static final long maxSize = 10; // numero maximo de registros que o pc aguenta por sort

    public static void insertionSort(ArrayList<BankAccount> a){
        final int n = a.size();
        for(int i = 1; i < n; ++i){
            BankAccount tmp = a.get(i);
            int j = i - 1;
            while((j >= 0) && (a.get(j).getId() > tmp.getId())){
                a.set(j+1, a.get(j));
                j--;
            }
            a.set(j+1, tmp);
        }
    }

    public static void solve(String source) {
        ArrayList<BankAccount> bar = new ArrayList<>();

        //enquanto i < que a capacidade maxima do pc, pegar blocos do arquivo
        for(int i = 1; i < maxSize; ++i) bar.add(Crud.searchById(source, i)); 
        insertionSort(bar);//como na maior parte dos casos o array vai estar quase ordenado, fica O(n) amortizado
        
        for(var x : bar) System.out.println(x.getId());
        // Arrays.sort(bar);
    }
    public static void main(String[] args) {
        solve("accounts.bin");
    }
}
