package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration localDate) throws IOException {
        jsonWriter.value(localDate.toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.parse(jsonReader.nextString());
    }
}
