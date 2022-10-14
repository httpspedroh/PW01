public class Generator {
    
    public static void main(String[] args) {
        
        for(int i = 1; i <= 9; i++) {
            
            BankAccount ba = new BankAccount();
            
            ba.setName("Name_" + i);
            ba.setUser("user_" + i);
            ba.setPass("senha123");
            ba.setCpf("cpf123");
            ba.setCity("BH");
            ba.setBalance((float)((Math.random() * (9000 - 1000)) + 1000));
            ba.setTransfers(0);
            ba.addEmail("email@example.com");

            Crud.create("accounts.bin", ba);
        }
    }
}
