package Server;


import java.net.ServerSocket;
import java.util.concurrent.Executors;


public class Server {
	
	/*
	 * Vale, guay, tenemos un server. Vamos a ordenar las cosas que sino luego es un lio y tal.
	 * Con ordenar nos referimos a mirar los nombres de usuario para que no haya ninguna confusión
	 * Cosa que haremos en el manipulador de hilos
	 */


    public static void main(String[] args) throws Exception {
        System.out.println("El server va a pedales, pero va...");
        var pool = Executors.newFixedThreadPool(500);
        //Buscamos este puerto, que coincide con el del cliente, y que encima está libre siempre
        try (var listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Manipulahilos(listener.accept()));
            }
        }
    }

    }