import java.io.*;
import java.net.*;

public class Cliente {

    public static void enviarMensagemEleicao(int idDestino, int idRemetente) {
        enviarMensagem(idDestino, "ELEICAO:" + idRemetente);
    }

    public static void enviarMensagemResposta(int idDestino, int idRemetente) {
        enviarMensagem(idDestino, "RESPOSTA:" + idRemetente);
    }

    public static void enviarMensagemCoordenador(int idDestino, int idCoordenador) {
        enviarMensagem(idDestino, "COORDENADOR:" + idCoordenador);
    }

    private static void enviarMensagem(int idDestino, String mensagem) {
        try (Socket socket = new Socket("localhost", 5000 + idDestino);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(mensagem);
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }
}
