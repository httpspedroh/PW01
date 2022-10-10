import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortFiles {

    public static final long maxSize = 10; // numero maximo de registros que o pc aguenta por sort

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
