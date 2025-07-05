package ru.yandex.practicum.http.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(value != null ? value.toString() : null);
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        String durationString = in.nextString();
        return durationString != null ? Duration.parse(durationString) : null;
    }
}

