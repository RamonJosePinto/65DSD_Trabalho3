/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java.net.*;

public class Cliente {

    public static void enviarMensagemEleicao(String ipDestino, int idDestino, int idRemetente, Processo process) {
        enviarMensagem(ipDestino, idDestino, "ELEICAO:" + idRemetente, process);
    }

    public static void enviarMensagemResposta(String ipDestino, int idDestino, int idRemetente, Processo process) {
        enviarMensagem(ipDestino, idDestino, "RESPOSTA:" + idRemetente, process);
    }

    public static void enviarMensagemCoordenador(String ipDestino, int idDestino, int idCoordenador, Processo process) {
        enviarMensagem(ipDestino, idDestino, "COORDENADOR:" + idCoordenador, process);
    }

    private static void enviarMensagem(String ipDestino, int idDestino, String mensagem, Processo processo) {
        try (Socket socket = new Socket(ipDestino, 80 + idDestino);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(mensagem);
            processo.setRespostaRecebida(true);
        } catch (IOException e) {
            System.err.println("Sem resposta do processo " + idDestino + ": " + e.getMessage());
            processo.setRespostaRecebida(false);
        }
    }

    public static void enviarMensagemPing(String ipDestino, int idDestino, int idRemetente, Processo processo) {
        enviarMensagem(ipDestino, idDestino, "PING:" + idRemetente, processo);
    }
}
