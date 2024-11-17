import java.util.Map;

public class MonitorCoordenador extends Thread {
    private final Processo processo;
    private final Map<Integer, String> idParaIp;
    private static final long INTERVALO_PING = 5000; // Intervalo entre pings ao coordenador

    public MonitorCoordenador(Processo processo, Map<Integer, String> idParaIp) {
        this.processo = processo;
        this.idParaIp = idParaIp;
    }

    @Override
    public void run() {
        while (true) {
            if (!processo.isCoordenador() && processo.getIdCoordenadorAtual() > 0 && !processo.isEleicaoEmAndamento()) {
                try {
                    // Envia um ping para o coordenador
                    String ipCoordenador = idParaIp.get(processo.getIdCoordenadorAtual());
                    Cliente.enviarMensagemPing(ipCoordenador, processo.getIdCoordenadorAtual(), processo.getId(), processo);

                    // Aguarda uma resposta do coordenador
                    if (!processo.aguardarResposta()) {
                        System.out.println("Processo " + processo.getId() + " detectou falha do coordenador " + processo.getIdCoordenadorAtual() + ".");
                        processo.iniciarEleicao();
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao enviar ping para o coordenador: " + e.getMessage());
                }
            }
            try {
                Thread.sleep(INTERVALO_PING);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
