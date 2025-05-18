package com.example.Implementacion;

// Importaciones para manejo de fecha y hora
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Cocina {

    // Nombre de la cola desde la que se recibirán las órdenes
    private final static String QUEUE_NAME = "ordenes";

    public static void main(String[] argv) throws Exception {
        // Mensaje informativo al iniciar el programa
        System.out.println("Cocina lista para recibir órdenes...");

        // 1. Crear y configurar la conexión con el servidor RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // El servidor está en la misma máquina (localhost)

        // 2. Establecer conexión y canal de comunicación con RabbitMQ
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 3. Declarar (asegurar la existencia de) la cola desde donde se reciben las órdenes
        // Parámetros: nombre, durable, exclusive, autoDelete, argumentos (null)
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println("Esperando órdenes. CTRL+C para salir.");

        // 4. Definir cómo se manejará cada mensaje recibido (callback)
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // Convertir el cuerpo del mensaje de bytes a texto
            String orden = new String(delivery.getBody(), "UTF-8");

            // Obtener la hora actual en formato legible
            String horaRecepcion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Imprimir la hora de recepción y la orden recibida
            System.out.println(horaRecepcion + " Orden recibida: " + orden);

            // Simular la preparación del platillo
            System.out.println("Preparando " + orden + "...\n");
        };

        // 5. Iniciar la escucha de mensajes desde la cola
        // autoAck=true indica que los mensajes se confirman automáticamente al recibirse
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}
 