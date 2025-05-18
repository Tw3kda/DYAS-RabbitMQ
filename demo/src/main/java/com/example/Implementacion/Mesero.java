package com.example.Implementacion;

// Importaciones necesarias para fecha/hora, entrada por consola y RabbitMQ
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Mesero {

    // Nombre de la cola que usará RabbitMQ
    private final static String QUEUE_NAME = "ordenes";

    public static void main(String[] argv) throws Exception {
        // 1. Crear la fábrica de conexiones a RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // Se conecta al servidor RabbitMQ local

        // 2. Crear conexión, canal y escáner para entrada por consola
        try (
            Connection connection = factory.newConnection();  // Conexión a RabbitMQ
            Channel channel = connection.createChannel();     // Canal de comunicación
            Scanner scanner = new Scanner(System.in)          // Entrada por consola
        ) {
            // 3. Declarar la cola (debe coincidir con la usada por Cocina)
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // 4. Menú de opciones disponible para el usuario
            String[] menu = {"1. Hamburguesa", "2. Pizza", "3. Ensalada", "4. Salir"};
            String[] platos = {"Hamburguesa", "Pizza", "Ensalada"};

            // 5. Ciclo principal de interacción
            while (true) {
                // Mostrar menú al usuario
                System.out.println("\n  Menú del restaurante:");
                for (String item : menu) {
                    System.out.println(item);
                }

                System.out.print("Seleccione una opción (1-4): ");
                int opcion;

                // Validación de entrada: asegurarse de que el usuario escriba un número
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                } else {
                    System.out.println("❌ Entrada inválida. Intente de nuevo.");
                    scanner.next(); // Limpiar entrada inválida
                    continue;
                }

                // Si selecciona "Salir"
                if (opcion == 4) {
                    System.out.println("👋 Saliendo del sistema de pedidos...");
                    break;
                } 
                // Si selecciona una opción válida del menú
                else if (opcion >= 1 && opcion <= 3) {
                    // Obtener el nombre del plato
                    String orden = platos[opcion - 1];

                    // Enviar la orden como mensaje a la cola "ordenes"
                    channel.basicPublish("", QUEUE_NAME, null, orden.getBytes("UTF-8"));

                    // Obtener la hora de envío y formatearla
                    LocalDateTime horaEnvio = LocalDateTime.now();
                    String horaFormateada = horaEnvio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    // Mostrar confirmación de envío
                    System.out.println(horaFormateada + " Orden enviada a cocina: " + orden);
                } 
                // Si el número está fuera del rango 1-4
                else {
                    System.out.println("Opción inválida. Intente de nuevo.");
                }
            }
        }
    }
}
