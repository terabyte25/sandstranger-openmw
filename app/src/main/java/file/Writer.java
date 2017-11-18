package file;

import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sandstranger on 13.10.16.
 */

  public class Writer {

        public static void write(String data, String path, String value)
                throws IOException {
            FileInputStream file = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            StringBuilder builder = new StringBuilder();
            boolean contains = false;
            while (line != null) {
                if (line.startsWith(value) && !contains) {
                    builder.append(value + "=" + data);
                    contains = true;
                } else
                    builder.append(line);
                builder.append("\n");
                line = reader.readLine();
            }
            if (!contains)
                builder.append(value + "=" + data);

            reader.close();
            FileWriter writer = new FileWriter(path);
            writer.write(builder.toString());
            writer.flush();
            writer.close();

        }
    }
