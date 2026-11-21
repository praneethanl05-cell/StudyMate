
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StudentPortalServer {

    private static TaskManager manager = new TaskManager();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/api/tasks", exchange -> {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );
            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"
            );
            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
            );

            String method = exchange.getRequestMethod();

            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (method.equals("GET")) {

                StringBuilder json = new StringBuilder("[");

                var tasks = manager.getTasks();

                for (int i = 0; i < tasks.size(); i++) {
                    var task = tasks.get(i);

                    if (i > 0) json.append(",");

                    json.append("{\"name\":\"")
                        .append(task.getTaskName().replace("\"", "\\\""))
                        .append("\",\"completed\":")
                        .append(task.isCompleted())
                        .append("}");
                }

                json.append("]");
                sendResponse(exchange, 200, json.toString());

            } else if (method.equals("POST")) {

                String name = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                ).trim();

                if (name.isEmpty()) {
                    sendResponse(exchange, 400, "Task name is required");
                    return;
                }

                manager.addTask(name);
                sendResponse(exchange, 200, "Task added successfully");

            } else if (method.equals("PUT")) {

                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");

                int index = Integer.parseInt(parts[3]);

                if (parts.length > 4 && parts[4].equals("undo")) {

                manager.undoTask(index);
                 sendResponse(exchange, 200, "Task marked as pending!");

            } else {

                 manager.completeTask(index);
                sendResponse(exchange, 200, "Task completed!");

        }

    }else if (method.equals("DELETE")) {

                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");

                int index = Integer.parseInt(parts[3]);

                manager.deleteTask(index);

                sendResponse(exchange, 200, "Task deleted!");
            }else {
                sendResponse(exchange, 405, "Method not allowed");
            }
        });

        server.start();

        System.out.println(
            "StudyMate server running at http://localhost:8080"
        );
    }

    private static void sendResponse(
        HttpExchange exchange, int status, String response
    ) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
