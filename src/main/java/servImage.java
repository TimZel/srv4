
import jdk.jfr.ContentType;

import java.io.*;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class servImage {

    public static final  String notFound = "HTTP/1.0 404 Not Found\r\nContent-Type: text/html\r\n\r\n";
    public static final String badRequest = "HTTP/1.0 400 Bad Request\r\nContent-Type: text/html\r\n\r\n";
    public static String pathToProperties = "/home/zelyanin/IdeaProjects/servReq2.0/src/main/java/testerFiletable";

    public static void main(String[] args) throws IOException {
        int visit = 0;//счетчик посетителей
        ServerSocket servsock = new ServerSocket(12000);//серверсокет с указанным портом
        do {
            try ( var socket = servsock.accept();// сокет соединения с клиентом
                 var isr = socket.getInputStream();//принимаю поток
                 var out = new DataOutputStream(socket.getOutputStream()) ) //вывожу поток
            {
                System.out.println("Client " + (++visit) + " accepted.");//информирую об обращении клиента
                Request request = Request.parse(isr);//создаю побъект типа Реквест, вызываю метод парсинга и передаю туда входящий поток
                //возвращаю обработанные данные
                File file = new File(request.path);
                if (file.isDirectory()) { //если запроше не файл
                    out.write(badRequest.getBytes());
                    System.out.println("Warning! The user requested not a file!");
                } else {//иначе
                        try (var in = new FileInputStream(file)) { //открываю поток для чтения файла
                            byte[] bytes = new byte[5*1024];//создаю байт-массив для хранения информации из файла

                            Properties prop = new Properties();
                            String contentType = null;
                            try {
                                FileInputStream fisProperties = new FileInputStream(pathToProperties);
                                prop.load(fisProperties);
                                contentType = prop.getProperty(request.exe);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            String httpResponse = "HTTP/1.0 200 OK\r\n" + "Content-Type:" + contentType +  "\r\n" + "\r\n";
                            out.write(httpResponse.getBytes());//отправляю хттп-ответ
                            int count;
                            while ((count = in.read(bytes)) > -1) {
                                out.write(bytes, 0, count);//отправляю содержимое файла
                            }
                            System.out.println("The request from user was completed succesfully!");

                        } catch (FileNotFoundException ex) {
                            System.out.println("Внимание! Розыск: " + ex);//шуткую, если файла нет
                            out.write(notFound.getBytes());
                        }
                }
            }
        } while (visit < 5);
        servsock.close();//закрываю сервер
        System.out.println("Server closed due to settings.");
    }

}
