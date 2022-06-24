import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class MapWorker extends Worker<MapTask<Integer, String>> {

    public MapWorker(BlockingQueue<MapTask<Integer, String>> queue) {
        super(queue);
    }

    public boolean isAlphaNumerical(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            MapTask<Integer, String> task = queue.poll();
            if(task == null) {
                return;
            }
            try {
                // open the file
                FileInputStream inputStream = new FileInputStream(task.getNameOfFile());
                StringBuilder stringBuilder = new StringBuilder();
                int position;
                char ch;
                int count = 0;

                // check if we are in the middle of a word
                if (task.getOffset() > 0) {
                    inputStream.getChannel().position(task.getOffset() - 1);
                    ch = (char) inputStream.read();
                    if (isAlphaNumerical(ch)) {
                        // parse the file until the word ends
                        while (inputStream.available() > 0) {
                            ch = (char) inputStream.read();
                            if (isAlphaNumerical(ch)) {
                                count++;
                            } else {
                                break;
                            }
                        }
                    }
                }

                // move to the starting point and parse the required fragment
                inputStream.getChannel().position(count + task.getOffset());
                for (position = count + task.getOffset(); position < task.getSize() + task.getOffset(); position++) {
                    ch = (char) inputStream.read();
                    stringBuilder.append(ch);
                }

                // check if the last word is incomplete
                if (stringBuilder.length() > 0) {
                    char lastCharacter = stringBuilder.charAt(stringBuilder.length() - 1);
                    if (isAlphaNumerical(lastCharacter)) {
                        inputStream.getChannel().position(task.getSize() + task.getOffset());
                        // take the last part of the word from the other fragment
                        while (inputStream.available() > 0) {
                            ch = (char) inputStream.read();
                            if (isAlphaNumerical(ch)) {
                                stringBuilder.append(ch);
                            } else {
                                break;
                            }
                        }
                    }
                }
                // split the portion using the separators
                String[] words = stringBuilder.toString().split("[;:/?˜.,><‘\\[\\]{}()!@#$'%ˆ&\\- +’=*”|\\r\\n\\t]+");
                Map<Integer, Integer> map = Collections.synchronizedMap(new HashMap<>());
                int maxLen = -1;

                // compute maximum length and find the words with that length
                for (position = 0; position < words.length; position++) {
                    int len = words[position].length();
                    if (len <= 0) {
                        continue;
                    }
                    maxLen = Math.max(maxLen, len);
                    if (!map.containsKey(len)) {
                        map.put(len, 1);
                    } else {
                        map.put(len, map.get(len) + 1);
                    }
                }
                List<String> list = Collections.synchronizedList(new ArrayList<>());
                for (position = 0; position < words.length; position++) {
                    if (words[position].length() == maxLen) {
                        list.add(words[position]);
                    }
                }

                // the result of the Map operations
                task.setOut(new MapReturn<>(map, list, task.getNameOfFile()));
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
