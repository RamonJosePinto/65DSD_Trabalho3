import java.util.Scanner;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o ID do processo que deseja utilizar: ");
        int id = scanner.nextInt();

//        List<Integer> todosIds = Arrays.asList(1, 2, 3, 4, 5); // IDs de processos predefinidos

        Map<Integer, String> idParaIp = new HashMap<>();
        idParaIp.put(1, "10.15.120.80"); // IP da máquina do processo 1
        idParaIp.put(2, "10.15.120.171");  // IP da máquina do processo 2
        idParaIp.put(3, "10.15.120.95");  // Exemplo de IP para o processo 3
        idParaIp.put(4, "10.15.120.12");  // Exemplo de IP para o processo 4
        idParaIp.put(5, "10.15.120.23");  // Exemplo de IP para o processo 5


        // Filtra os IDs maiores para o processo corrente
        List<Integer> idsMaiores = new ArrayList<>();
        for (int outroId : idParaIp.keySet()) {
            if (outroId > id) {
                idsMaiores.add(outroId);
            }
        }

        Processo processo = new Processo(id, idParaIp);
        new Servidor(processo, 82, idParaIp).start();

        // Inicia uma eleição ao ser "consertado" e iniciado
        processo.iniciarEleicao();

        // Mantém o processo ativo indefinidamente
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
