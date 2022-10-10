import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.FileHandler;

import javax.swing.plaf.FontUIResource;

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
     * @param raf, raf do arquivo
     * 
     * @param pointer, ponteiro pro usuario
     * 
     * le o usuario de numero @pointer de um arquivo @raf
     */
    public static BankAccount readBankAccount(RandomAccessFile raf, int pointer) throws IOException {
        BankAccount ba = new BankAccount();
        while (raf.getFilePointer() < raf.length()) {

            if (raf.readByte() == 0) {

                int size = raf.readInt();

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
                return ba;
            } else
                raf.skipBytes(raf.readInt());
        }
        return null;
    }

    public static void mergeFiles(String output) throws IOException {
        // minHeap de contas bancarias ordenadas por ID
        PriorityQueue<BankAccount> pq = new PriorityQueue<>(Comparator.comparing(BankAccount::getId));

        // tamanho maximo de arquivos criados
        int size = (Crud.getTotalAccounts() / maxSize) - 1;
        // lista com os arquivos
        List<File> files = new ArrayList<>();
        // adiciona todos os arquivos do modelo 'f' + i no array de arquivos
        for (int i = 1; i <= size; ++i) {
            File tmp = new File("f" + i);
            files.add(tmp);
        }

        while (true) {
            int[] pointer = new int[size + 1];// array mostrando pra onde o ponteiro deve ir em cada file
            int count = 1; // ponteiro
            for (var x : files) {
                RandomAccessFile raf = new RandomAccessFile(x, "r");
                BankAccount ba = readBankAccount(raf, pointer[count]);
                pointer[count]++;
                pq.add(ba);
                // FileInputStream fin = new FileInputStream(x);
                raf.close();
                count++;
            }
            Crud.create(output, pq.peek());// cria uma conta com o primeiro elemento do minHeap
            pq.remove();// tira um elemento do heap
            if (pq.isEmpty())
                break;
        }
    }

    /*
     * @param source, arquivo fonte, da onde serao tirados os registros e passados
     * pra outros arquivos
     * 
     * @param f_index, file index, proximo id a ser buscado no arquivo
     * 
     * @param int z, index do novo arquivo (no modelo "f" + z)(f1,f2,f3...);
     */
    public static void solve(String source, int f_index, int z) throws IOException {
        if (f_index <= Crud.getTotalAccounts()) {
            System.out.println("DBG" + z);
            ArrayList<BankAccount> bar = new ArrayList<>();
            // enquanto i < que a capacidade maxima do pc, pegar blocos do arquivo
            for (int i = f_index; i < f_index + maxSize; ++i)
                if (Crud.searchById(source, i) != null)
                    bar.add(Crud.searchById(source, i));

            // insertionSort(bar);//como na maior parte dos casos o array vai estar quase
            // ordenado, fica O(n) amortizado

            bar.forEach(i -> Crud.create(changeIndex(z), i));// adiciona o bloco todo em um arquivo no modelo "f"+index

            solve(source, f_index + maxSize, z + 1);
            mergeFiles("output");

            for (var x : bar)
                System.out.println(x.getId());
            // Arrays.sort(bar);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(Crud.globalId);
        for (int i = 0; i < 10; ++i) {
            System.out.println(changeIndex(i));
        }
        solve("accounts.bin", 1, 1);
    }
}
