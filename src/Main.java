import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "config.txt";
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o ID do processo que deseja utilizar: ");
        int id = scanner.nextInt();

//        Map<Integer, String> idParaIp = new HashMap<>();
//            idParaIp.put(1, "192.168.56.1");
//            idParaIp.put(2, "192.168.56.1");
//            idParaIp.put(3, "192.168.56.1");
//            idParaIp.put(4, "192.168.56.1");
//            idParaIp.put(5, "192.168.56.1");

        Map<Integer, String> idParaIp = null;
        try {
            idParaIp = carregarMapaIps(caminhoArquivo);
        } catch (IOException e) {
            System.err.println("Erro na execução do processo: " + e.getMessage());
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
    public static Map<Integer, String> carregarMapaIps(String caminhoArquivo) throws IOException {
        Map<Integer, String> idParaIp = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;
                String[] partes = linha.split("\\s+");
                int id = Integer.parseInt(partes[0].trim());
                String ip = partes[1].trim();
                idParaIp.put(id, ip);
            }
        }
        return idParaIp;
    }

}
