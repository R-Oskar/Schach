package AI;

import java.io.*;

public class StockfishEngine {

    private Process stockfishProcess;
    private BufferedReader reader;
    private BufferedWriter writer;

    public boolean startEngine(String stockfishPath) {
        try {
            stockfishProcess = new ProcessBuilder(stockfishPath).redirectErrorStream(true).start();
            reader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
            sendCommand("uci"); // initialize UCI mode
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public String getBestMove(String fen, int depth) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + depth);

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }
        return null;
    }

    public void stopEngine() throws IOException {
        sendCommand("quit");
        writer.close();
        reader.close();
        stockfishProcess.destroy();
    }
}
