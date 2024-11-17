import java.util.Scanner;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o ID do processo que deseja utilizar: ");
        int id = scanner.nextInt();

        Map<Integer, String> idParaIp = new HashMap<>();
            idParaIp.put(1, "192.168.56.1");
            idParaIp.put(2, "192.168.56.1");
            idParaIp.put(3, "192.168.56.1");
            idParaIp.put(4, "192.168.56.1");
            idParaIp.put(5, "192.168.56.1");


        // Filtra os IDs maiores para o processo corrente
        List<Integer> idsMaiores = new ArrayList<>();
        for (int outroId : idParaIp.keySet()) {
            if (outroId > id) {
                idsMaiores.add(outroId);
            }
        }

        Processo processo = new Processo(id, idParaIp);
        new Servidor(processo, 80 + id, idParaIp).start();

        // Inicia uma eleição ao ser "consertado" e iniciado
        processo.iniciarEleicao();
        new MonitorCoordenador(processo, idParaIp).start();

        // Mantém o processo ativo
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
