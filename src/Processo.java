import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Processo {
    private int id;
    private AtomicBoolean coordenador;
    private int idCoordenadorAtual;
//    private List<Integer> outrosProcessos; // IDs dos outros processos
    private List<Integer> idsMaisAltos;
    private static final long TEMPO_LIMITE_MS = 2000; // Tempo T
    private static final long TEMPO_ESPERA_COORD_MS = 1000; // Tempo T' para esperar coordenador

    public Processo(int id, List<Integer> todosProcessos) {
        this.id = id;
//        this.outrosProcessos = outrosProcessos;
        this.idsMaisAltos = new ArrayList<>();

        // Armazenar apenas os IDs mais altos
        for (int outroId : todosProcessos) {
            if (outroId > this.id) {
                idsMaisAltos.add(outroId);
            }
        }
        this.coordenador = new AtomicBoolean(false);
    }

    public int getId() {
        return id;
    }

    public List<Integer> getIdsMaisAltos() {
        return idsMaisAltos;
    }

    public boolean isCoordenador() {
        return coordenador.get();
    }

    public void setCoordenador(boolean coordenador) {
        this.coordenador.set(coordenador);
    }

    public void setIdCoordenadorAtual(int id) {
        this.idCoordenadorAtual = id;
    }

    public int getIdCoordenadorAtual() {
        return idCoordenadorAtual;
    }

    public void iniciarEleicao() {
        System.out.println("Processo " + id + " está iniciando uma eleição...");
        boolean recebeuResposta = false;

        // Envia mensagens de eleição para processos com identificadores mais altos
        for (int outroProcessoId : idsMaisAltos) {
            if (outroProcessoId > this.id) {
                Cliente.enviarMensagemEleicao(outroProcessoId, this.id);

                // Aguarda uma resposta dentro do tempo limite T
                recebeuResposta = aguardarResposta(outroProcessoId);

                if (recebeuResposta) {
                    System.out.println("Processo " + id + " recebeu resposta de " + outroProcessoId);
                    break;
                }
            }
        }

        if (!recebeuResposta) {
            System.out.println("Processo " + id + " não recebeu respostas. Aguardando mensagem de coordenador...");

            // Aguarda a chegada da mensagem de coordenador por T' (TEMPO_ESPERA_COORD_MS)
            boolean recebeuCoordenador = aguardarMensagemCoordenador();

            if (!recebeuCoordenador) {
                System.out.println("Processo " + id + " não recebeu mensagem de coordenador. Iniciando nova eleição.");
                iniciarEleicao();
            }
        }
    }


    private boolean aguardarResposta(int outroProcessoId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> futuro = executor.submit(() -> {
            // Implementação para verificar se uma resposta é recebida
            // Esta é uma simulação, então podemos implementar conforme as mensagens reais chegarem
            Thread.sleep(TEMPO_LIMITE_MS); // Espera o tempo limite T
            return false; // Retorna falso se o tempo limite foi alcançado sem resposta
        });

        try {
            return futuro.get(TEMPO_LIMITE_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("Processo " + id + " não recebeu resposta de " + outroProcessoId + " no tempo limite.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            executor.shutdown();
        }
    }

    private boolean aguardarMensagemCoordenador() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> futuro = executor.submit(() -> {
            Thread.sleep(TEMPO_ESPERA_COORD_MS); // Espera o tempo T'
            return false; // Simulação, retorna falso se o tempo T' é alcançado sem mensagem de coordenador
        });

        try {
            return futuro.get(TEMPO_ESPERA_COORD_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("Processo " + id + " não recebeu mensagem de coordenador no tempo limite.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            executor.shutdown();
        }
    }
}
