package Cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Cliente {

	/*
	 * Esta clase de cliente va a crearse también con JSwing debido a que escribir en la consola daña los ojos y nadie hace eso desde los 80,
	 * probablemente nadie hace eso desde el principio de internet.
	 */
    String direccionIP;
    Scanner sc;
    PrintWriter pw;
    JFrame frame = new JFrame("Walkie Talkie");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(25, 50);


    public Cliente(String direccionIP) {
    	//Solamente necesitamos la IP para empezar
        this.direccionIP = direccionIP;
        //Aparte de eso, creamos todo el aparatejo de Swing
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        //Conforme se active, vamos a coger el JTextfield vacío para usarlo.
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pw.println(textField.getText());
                textField.setText("");
            }
        });
    }
    
    //Hasta en los walkie talkie, la comunicación puede ser de 2 a más personas, por lo que sería racional nombrar a cada integrante
    private String getNombre() {
    	//Aquí le damos la opción al soldado de elegir un nombre
        return JOptionPane.showInputDialog(frame, "Elige un usuario, soldado:", "Selección de usuario",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException {
        try {
        	//Usamos TCP, puesto que es imperativo que ningún paquete se pierda por el camino.
        	//El puerto 59001 debería de estar siempre libre para nuestro uso
            var socket = new Socket(direccionIP, 59001);
            //Con el scanner escribimos por el socket
            sc = new Scanner(socket.getInputStream());
            //Y con el printwritter, vemos lo que nos ha mandado el socket
            pw = new PrintWriter(socket.getOutputStream(), true);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                //Empezamos por poner el nombre
                if (line.startsWith("NOMBRE")) {
                    pw.println(getNombre());
                    textField.setEditable(true);
                } 
                //Cuando el nombre esté puesto, activamos el chat
                else if (line.startsWith("LISTO")) {
                    textField.setEditable(true);
                }
	            else if (line.startsWith("MENSAJE")) {
	                messageArea.append(line.substring(8) + "\n");
	            }
                else if (line.startsWith("CAMBIO")) {
                	if (textField.isEditable()) {
                		textField.setEditable(false);
                	} else {
                    	textField.setEditable(true);
                	}

                }
                else if (line.startsWith("CORTO")) {
                	messageArea.append(line.substring(8) + "La conexión va a cerrarse.");
                	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) { e.printStackTrace(); };
                	this.frame.dispose();
                }
            }
        } 
        //Al final, pues cerramos todos
        finally {
            this.frame.setVisible(false);
            this.frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Introduce la IP de tu compañero, soldado");
    	String iP = sc.nextLine();
        //Activamos toda la maquinaria, chuu chuu.
        var client = new Cliente(iP);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}