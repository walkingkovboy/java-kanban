package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(localDate.toString());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        var str = jsonReader.nextString();
        if (str.equals("null")) {
            return null;
        }
        return Duration.parse(str);
    }
}
