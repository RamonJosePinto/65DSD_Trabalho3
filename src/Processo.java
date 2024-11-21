import java.util.*;

public class Processo {
    private int id;
    private boolean isCoordenador;
    private int idCoordenadorAtual;
    private Map<Integer, String> idParaIp;
    private static final long TEMPO_LIMITE = 2000; // Tempo limite para esperar respostas
    private static final long TEMPO_AGUARDA_COORDENADOR = 10000; // Tempo para aguardar mensagem de coordenador
    private boolean respostaRecebida;
    private boolean eleicaoEmAndamento;

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
        return eleicaoEmAndamento;
    }

    public void iniciarEleicao() {
        if (isEleicaoEmAndamento()) {
            return;
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
        return getRespostaRecebida();
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

    public boolean aguardarResposta() {
        long inicio = System.currentTimeMillis();
        while ((System.currentTimeMillis() - inicio) < TEMPO_LIMITE) {

                if (respostaRecebida) {
                    return true;
                }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized boolean getRespostaRecebida() {
        return respostaRecebida;
    }

    public void setRespostaRecebida(boolean respostaRecebida) {
        this.respostaRecebida = respostaRecebida;
    }

    public int getIdCoordenadorAtual() {
        return idCoordenadorAtual;
    }

    public void setIdCoordenadorAtual(int idCoordenadorAtual) {
        this.idCoordenadorAtual = idCoordenadorAtual;
    }

    public boolean getEleicaoEmAndamento() {
        return eleicaoEmAndamento;
    }

    public void setEleicaoEmAndamento(boolean eleicaoEmAndamento) {
        this.eleicaoEmAndamento = eleicaoEmAndamento;
    }
}
