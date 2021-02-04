package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

//Haha, threads go brrrr

public class Manipulahilos implements Runnable {
	
    private String name;
    private Socket socket;
    private Scanner sc;
    private PrintWriter pw;
    //Me he leido cosas de sets y hashsets durante una hora para entender qué estoy haciendo aquí precisamente
    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    /*
     * Basicamente, nombres y escritores de texto para cada uno, desordenados porque tampoco es importante
     * Lo que si hace el hashset es buscarlo a toda hostia con su propio método de busqueda dandole a cada cosa
     * un propio index que más tarde usa en el array interno
     * 
     * No se, acabo de mirarlo en google.
     */


    public Manipulahilos(Socket socket) {
        this.socket = socket;
    }

/**
 * Vale, esto es simple
 * -Pillamos el nombre
 * -Pillamos el texto
 * -Lo enseñamos a todo el mundo
 */
    public void run() {
        try {
        	//Igual que en el cliente, con el scanner escribimos a través del socket, y con el printwriter, leemos a través del socket
            sc = new Scanner(socket.getInputStream());
            pw = new PrintWriter(socket.getOutputStream(), true);

            //Pide el nombre hasta que tengamos uno
            while (true) {
                pw.println("NOMBRE");
                name = sc.nextLine();
                if (name == null) {
                    return;
                }
                synchronized (names) {
                    if (!name.isBlank() && !names.contains(name)) {
                    	//Añadimos dicho nombre al hashset
                        names.add(name);
                        System.out.println(names.toString());
                        break;
                    }
                }
            }

            /**
             * Wachi, tenemos el nombre, pero hay que darle un escritor de texto
             * Ya que estamos, vamos a anunciar cual héroe entrando en el foro romano que el usuario ha entrado a la sala
             */
            pw.println("LISTO " + name);
            for (PrintWriter writer : writers) {
                writer.println("MENSAJE " + name + " ha encendido el walkie-talkie.");
            }
            writers.add(pw);

            // Nos queda pillar los mensajes del usuario, y ponerlos para todos
            while (true) {
                String input = sc.nextLine();
                //Cuando dice corto, se turbopira
                if (input.toLowerCase().startsWith("CORTO") || (input.toLowerCase().startsWith("Corto"))
                	|| (input.toLowerCase().startsWith("corto")))
                {
                    for (PrintWriter writer : writers) {
                        writer.println("CORTO");
                    }
                    
                }
                //Si el usuario dice cambio, da el paso de palabra
                else if (input.toLowerCase().startsWith("cambio") || (input.toLowerCase().startsWith("Cambio"))
                    	|| (input.toLowerCase().startsWith("CAMBIO")) || (input.toLowerCase().startsWith("/C"))) 
                {
                    for (PrintWriter writer : writers) {
                        writer.println("CAMBIO");
                    }
                }
                for (PrintWriter writer : writers) {
                    writer.println("MENSAJE " + name + ": " + input);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            //Quitamos al usuario del chat y a todo lo referente
        } finally {
            if (pw != null) {
                writers.remove(pw);
            }
            if (name != null) {
                System.out.println(name + " cierra el waklie-talkie.");
                names.remove(name);
                for (PrintWriter writer : writers) {
                    writer.println("MENSAJE " + name + " se ha marchado.");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
