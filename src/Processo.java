import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Processo {
    private int id;
    private AtomicBoolean isCoordenador;
    private int idCoordenadorAtual;
    private Map<Integer, String> idParaIp;
    private static final long TEMPO_LIMITE = 2000; // Tempo limite para esperar respostas
    private AtomicBoolean respostaRecebida;
    private AtomicBoolean eleicaoEmAndamento;

    public Processo(int id, Map<Integer, String> idParaIp) {
        this.id = id;
        this.isCoordenador = new AtomicBoolean(false);
        this.idParaIp = idParaIp;
        this.respostaRecebida = new AtomicBoolean(false);
        this.eleicaoEmAndamento = new AtomicBoolean(false);
    }

    public int getId() {
        return id;
    }

    public boolean isCoordenador() {
        return isCoordenador.get();
    }

    public void setCoordenador(boolean coordenador) {
        this.isCoordenador.set(coordenador);
    }

    public boolean isEleicaoEmAndamento() {
        return eleicaoEmAndamento.get();
    }

    public void iniciarEleicao() {
        if (eleicaoEmAndamento.get()) {
            return; // Se já houver uma eleição em andamento, ignore
        }

        eleicaoEmAndamento.set(true); // Marca que a eleição está em andamento
        System.out.println("Processo " + id + " está iniciando uma eleição...");
        respostaRecebida.set(false);

        // Envia mensagens de eleição apenas para IDs maiores
        for (int outroId : idParaIp.keySet()) {
            if (outroId > this.id) {
                Cliente.enviarMensagemEleicao(idParaIp.get(outroId), outroId, this.id);

                // Aguarda resposta de cada ID maior individualmente
                if (aguardarResposta()) {
                    System.out.println("Processo " + id + " recebeu resposta de " + outroId + " e aguardará o coordenador.");
                    eleicaoEmAndamento.set(false); // Libera a eleição em andamento
                    return; // Sai da eleição se receber uma resposta
                }
            }
        }

        // Se não recebeu resposta de nenhum ID maior, torna-se coordenador
        System.out.println("Processo " + id + " não recebeu respostas. Se declara coordenador.");
        setCoordenador(true);
        idCoordenadorAtual = id;

        // Envia mensagem de coordenador para IDs menores
        for (int outroId : idParaIp.keySet()) {
            if (outroId < id) {
                Cliente.enviarMensagemCoordenador(idParaIp.get(outroId), outroId, id);
            }
        }

        eleicaoEmAndamento.set(false); // Libera a eleição em andamento ao final
    }

    private boolean aguardarResposta() {
        long inicio = System.currentTimeMillis();
        while ((System.currentTimeMillis() - inicio) < TEMPO_LIMITE) {
            if (respostaRecebida.get()) {
                return true; // Retorna verdadeiro se uma resposta for recebida dentro do limite
            }
            try {
                Thread.sleep(100); // Aguardar um pouco antes de verificar novamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false; // Retorna falso se não receber resposta no tempo limite
    }

    public void setRespostaRecebida(boolean valor) {
        respostaRecebida.set(valor);
    }
}
