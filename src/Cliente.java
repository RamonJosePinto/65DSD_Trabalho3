/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java.net.*;

public class Cliente {

    public static void enviarMensagemEleicao(String ipDestino, int idDestino, int idRemetente) {
        enviarMensagem(ipDestino, idDestino, "ELEICAO:" + idRemetente);
    }

    public static void enviarMensagemResposta(String ipDestino, int idDestino, int idRemetente) {
        enviarMensagem(ipDestino, idDestino, "RESPOSTA:" + idRemetente);
    }

    public static void enviarMensagemCoordenador(String ipDestino, int idDestino, int idCoordenador) {
        enviarMensagem(ipDestino, idDestino, "COORDENADOR:" + idCoordenador);
    }

    private static void enviarMensagem(String ipDestino, int idDestino, String mensagem) {
        try (Socket socket = new Socket(ipDestino, 80 + idDestino);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(mensagem);
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem para " + ipDestino + ": " + e.getMessage());
        }
    }
}
