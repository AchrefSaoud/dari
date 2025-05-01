package utm.tn.dari.config;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class DockerElasticsearchLauncher {

    public static void lauchDockerContainer() throws IOException, InterruptedException {
        // Check if the container already exists

        ProcessBuilder checkContainer = new ProcessBuilder(
                "docker", "ps", "-a", "--filter", "name=elasticsearch", "--format", "{{.Names}}"
        );
        Process checkProcess = checkContainer.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(checkProcess.getInputStream()));
        String line = reader.readLine();
        checkProcess.waitFor();

        if (line != null && line.equals("elasticsearch")) {
            // If container exists, try to start it (in case it's stopped)
            ProcessBuilder start = new ProcessBuilder("docker", "start", "elasticsearch").inheritIO();
            start.start().waitFor();
            System.out.println("Elasticsearch container already exists. Starting it...");
            Thread.sleep(25000);

        } else {
            // Pull the Elasticsearch image
            ProcessBuilder pull = new ProcessBuilder(
                    "docker", "pull", "docker.elastic.co/elasticsearch/elasticsearch:8.11.1"
            ).inheritIO();
            pull.start().waitFor();

            // Run Elasticsearch container
            ProcessBuilder run = new ProcessBuilder(
                    "docker", "run", "-d",
                    "-p", "9200:9200",
                    "--name", "elasticsearch",
                    "-e", "discovery.type=single-node",
                    "docker.elastic.co/elasticsearch/elasticsearch:8.11.1"
            ).inheritIO();
            run.start().waitFor();
            System.out.println("Elasticsearch container launched successfully.");
        }
    }

}
