import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Processo {
    private int id;
    private boolean isCoordenador;
    private int idCoordenadorAtual;
    private Map<Integer, String> idParaIp;
    private static final long TEMPO_LIMITE = 2000; // Tempo limite para esperar respostas
    private static final long TEMPO_AGUARDA_COORDENADOR = 7000; // Tempo extra para aguardar mensagem de coordenador
    private boolean respostaRecebida;
    private boolean eleicaoEmAndamento;
    private final Lock lock = new ReentrantLock();

    public Processo(int id, Map<Integer, String> idParaIp) {
        this.id = id;
        this.isCoordenador = false;
        this.idParaIp = idParaIp;
        this.respostaRecebida = false;
        this.eleicaoEmAndamento = false;
    }

    public int getId() {
        return id;
    }

    public boolean isCoordenador() {
        return isCoordenador;
    }

    public void setCoordenador(boolean coordenador) {
        isCoordenador = coordenador;
    }

    public boolean isEleicaoEmAndamento() {
        lock.lock();
        try {
            return eleicaoEmAndamento;
        } finally {
            lock.unlock();
        }
    }

    public void iniciarEleicao() {
        if (isEleicaoEmAndamento()) {
            return; // Ignora se já há uma eleição em andamento
        }

        setEleicaoEmAndamento(true);
        System.out.println("Processo " + id + " está iniciando uma eleição...");
        setRespostaRecebida(false);

        boolean houveResposta = enviarMensagensEleicaoParaIdsMaiores();

        if (houveResposta && !aguardarCoordenador()) {
            System.out.println("Processo " + id + " não recebeu mensagem de coordenador. Iniciando nova eleição.");
            reiniciarEleicao();
            return;
        }

        if (!houveResposta) {
            proclamarCoordenador();
        }

        setEleicaoEmAndamento(false);
    }

    private boolean enviarMensagensEleicaoParaIdsMaiores() {
        boolean houveResposta = false;

        for (int outroId : idParaIp.keySet()) {
            if (outroId > this.id) {
                Cliente.enviarMensagemEleicao(idParaIp.get(outroId), outroId, this.id, this);
                if (aguardarResposta()) {
                    System.out.println("Processo " + id + " recebeu resposta de " + outroId + " e aguardará o coordenador.");
                    houveResposta = true;
                    break;
                }
            }
        }
        return houveResposta;
    }


    private boolean aguardarCoordenador() {
        esperar(TEMPO_AGUARDA_COORDENADOR);
        return getRespostaRecebida(); // Verifica se houve resposta após o tempo T'
    }

    private void reiniciarEleicao() {
        setEleicaoEmAndamento(false);
        iniciarEleicao();
    }

    private void proclamarCoordenador() {
        System.out.println("Processo " + id + " não recebeu respostas. Se declara coordenador.");
        setCoordenador(true);
        idCoordenadorAtual = id;
        notificarProcessosMenores();
    }

    private void notificarProcessosMenores() {
        for (int outroId : idParaIp.keySet()) {
            if (outroId < id) {
                Cliente.enviarMensagemCoordenador(idParaIp.get(outroId), outroId, id, this);
            }
        }
    }

    private void esperar(long tempo) {
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized boolean getRespostaRecebida() {
        return respostaRecebida;
    }

    public boolean aguardarResposta() {
        long inicio = System.currentTimeMillis();
        while ((System.currentTimeMillis() - inicio) < TEMPO_LIMITE) {

                if (respostaRecebida) {
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

    private boolean aguardarMensagemCoordenador() {
        try {
            Thread.sleep(TEMPO_AGUARDA_COORDENADOR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return respostaRecebida; // Verifica se houve resposta após o tempo T'
    }

    public void setRespostaRecebida(boolean respostaRecebida) {
        this.respostaRecebida = respostaRecebida;
    }

    public int getIdCoordenadorAtual() {
        lock.lock();
        try {
            return idCoordenadorAtual;
        } finally {
            lock.unlock();
        }
    }

    public void setIdCoordenadorAtual(int idCoordenadorAtual) {
        lock.lock();
        try {
            this.idCoordenadorAtual = idCoordenadorAtual;
        } finally {
            lock.unlock();
        }
    }

    public boolean getEleicaoEmAndamento() {
        return eleicaoEmAndamento;
    }

    public void setEleicaoEmAndamento(boolean eleicaoEmAndamento) {
        this.eleicaoEmAndamento = eleicaoEmAndamento;
    }
}
