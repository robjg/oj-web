package org.oddjob.web.gson.plugin;

import com.google.gson.*;
import org.oddjob.input.InputRequest;
import org.oddjob.input.requests.*;
import org.oddjob.web.gson.GsonConfigurator;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Json Adapter for {@link InputRequest}s.
 */
public class InputRequestGson implements JsonSerializer<InputRequest>, JsonDeserializer<InputRequest>, GsonConfigurator {

    public static final String MESSAGE_TYPE = "message";

    public static final String TEXT_TYPE = "text";

    public static final String PASSWORD_TYPE = "password";

    public static final String FILE_TYPE = "file";

    public static final String CONFIRM_TYPE = "confirm";

    public static final String TYPE = "@type";

    public static final String MESSAGE = "message";

    public static final String PROPERTY = "property";

    public static final String PROMPT = "prompt";

    public static final String DEFAULT = "default";

    @Override
    public GsonBuilder configure(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeHierarchyAdapter(InputRequest.class, this);
    }

    @Override
    public JsonElement serialize(InputRequest src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        if (src instanceof InputMessage) {
            InputMessage inputMessage = (InputMessage) src;
            jsonObject.addProperty(TYPE, MESSAGE_TYPE);
            jsonObject.addProperty(MESSAGE, inputMessage.getMessage());
        }
        else if (src instanceof InputText) {
            InputText inputText = (InputText) src;
            jsonObject.addProperty(TYPE, TEXT_TYPE);
            jsonObject.addProperty(PROPERTY, inputText.getProperty());
            jsonObject.addProperty(PROMPT, inputText.getPrompt());
            jsonObject.addProperty(DEFAULT, inputText.getDefault());
        }
        else if (src instanceof InputPassword) {
            InputPassword inputPassword = (InputPassword) src;
            jsonObject.addProperty(TYPE, PASSWORD_TYPE);
            jsonObject.addProperty(PROPERTY, inputPassword.getProperty());
            jsonObject.addProperty(PROMPT, inputPassword.getPrompt());
        }
        else if (src instanceof InputFile) {
            InputFile inputFile = (InputFile) src;
            jsonObject.addProperty(TYPE, FILE_TYPE);
            jsonObject.addProperty(PROPERTY, inputFile.getProperty());
            jsonObject.addProperty(PROMPT, inputFile.getPrompt());
            // Only partially implement file
        }
        else if (src instanceof InputConfirm) {
            InputConfirm inputConfirm = (InputConfirm) src;
            jsonObject.addProperty(TYPE, CONFIRM_TYPE);
            jsonObject.addProperty(PROPERTY, inputConfirm.getProperty());
            jsonObject.addProperty(PROMPT, inputConfirm.getPrompt());
            jsonObject.addProperty(DEFAULT, inputConfirm.getDefault());
        }
        else {
            jsonObject.addProperty(TYPE, MESSAGE_TYPE);
            jsonObject.addProperty(MESSAGE, "ERROR: Unknown Input Type + " + src.getClass());
        }

        return jsonObject;
    }

    @Override
    public InputRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) json;

        String type = jsonObject.getAsJsonPrimitive(TYPE).getAsString();


        switch (type) {
            case MESSAGE_TYPE:
                InputMessage inputMessage = new InputMessage();
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(MESSAGE))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputMessage::setMessage);
                return inputMessage;
            case TEXT_TYPE:
                InputText inputText = new InputText();
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROPERTY))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputText::setProperty);
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROMPT))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputText::setPrompt);
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(DEFAULT))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputText::setDefault);
                return inputText;
            case PASSWORD_TYPE:
                InputPassword inputPassword = new InputPassword();
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROPERTY))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputPassword::setProperty);
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROMPT))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputPassword::setPrompt);
                return inputPassword;
            case FILE_TYPE:
                InputFile inputFile = new InputFile();
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROPERTY))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputFile::setProperty);
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROMPT))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputFile::setPrompt);
                return inputFile;
            case CONFIRM_TYPE:
                InputConfirm inputConfirm = new InputConfirm();
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROPERTY))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputConfirm::setProperty);
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(PROMPT))
                        .map(JsonPrimitive::getAsString)
                        .ifPresent(inputConfirm::setPrompt);
                Optional.ofNullable(jsonObject.getAsJsonPrimitive(DEFAULT))
                        .map(JsonPrimitive::getAsBoolean)
                        .ifPresent(inputConfirm::setDefault);
                return inputConfirm;
            default:
                InputMessage errorMessage = new InputMessage();
                errorMessage.setMessage("ERROR: No input type: " + type);
                return errorMessage;
        }
    }
}
