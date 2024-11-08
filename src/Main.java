import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5);

        List<Processo> processos = new ArrayList<>();
        for (int id : ids) {
            processos.add(new Processo(id, new ArrayList<>(ids)));
        }

        // Inicializar o servidor para cada processo
        for (int i = 0; i < processos.size(); i++) {
            Processo processo = processos.get(i);
            new Servidor(processo, 5000 + processo.getId()).start();
        }

        // Iniciar uma eleição por um dos processos (exemplo)
        processos.get(2).iniciarEleicao();
    }
}
