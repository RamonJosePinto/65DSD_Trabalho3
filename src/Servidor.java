import java.net.*;
import java.io.*;
import java.util.Map;

public class Servidor extends Thread {
    private Processo processo;
    private int porta;
    private Map<Integer, String> idParaIp;

    public Servidor(Processo processo, int porta, Map<Integer, String> idParaIp) {
        this.processo = processo;
        this.porta = porta;
        this.idParaIp = idParaIp;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String mensagem = input.readLine();
                    processarMensagem(mensagem);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private void processarMensagem(String mensagem) {
        String[] partes = mensagem.split(":");
        String tipo = partes[0];
        int idRemetente = Integer.parseInt(partes[1]);
        String ipRemetente = idParaIp.get(idRemetente);

        switch (tipo) {
            case "ELEICAO":
                if (ipRemetente != null) {
                    Cliente.enviarMensagemResposta(ipRemetente, idRemetente, processo.getId(), processo);
                } else {
                    System.err.println("IP do remetente não encontrado para ID: " + idRemetente);
                }

                if (!processo.isEleicaoEmAndamento()) {
                    processo.iniciarEleicao();
                }
                break;
            case "RESPOSTA":
                System.out.println("Processo " + processo.getId() + " recebeu resposta de " + idRemetente);
                processo.setRespostaRecebida(true);
                break;
            case "COORDENADOR":
                processo.setCoordenador(idRemetente == processo.getId());
                processo.setIdCoordenadorAtual(idRemetente);
                processo.setEleicaoEmAndamento(false);
                System.out.println("Processo " + processo.getId() + " reconhece " + idRemetente + " como novo coordenador.");
                break;
            case "PING":
                Cliente.enviarMensagemResposta(ipRemetente, idRemetente, processo.getId(), processo);
                break;
        }
    }
}
