package util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDateDeserializer extends StdDeserializer<Date> {

    public CustomDateDeserializer() {
        this(null);
    }

    protected CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
        String date = jsonParser.getText();

        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
