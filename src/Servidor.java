import java.net.*;
import java.io.*;

public class Servidor extends Thread {
    private Processo processo;
    private int porta;

    public Servidor(Processo processo, int porta) {
        this.processo = processo;
        this.porta = porta;
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

        switch (tipo) {
            case "ELEICAO":
                Cliente.enviarMensagemResposta(idRemetente, processo.getId());
                if (!processo.isCoordenador()) {
                    processo.iniciarEleicao();
                }
                break;
            case "RESPOSTA":
                System.out.println("Processo " + processo.getId() + " recebeu resposta de " + idRemetente);
                break;
            case "COORDENADOR":
                processo.setIdCoordenadorAtual(idRemetente);
                processo.setCoordenador(idRemetente == processo.getId());
                System.out.println("Novo coordenador é o processo " + idRemetente);
                break;
        }
    }
}
